package com.joshrotenberg.redis.client.builder.failover.metrics

import com.joshrotenberg.redis.client.builder.failover.event.AbstractTypedEventListener
import com.joshrotenberg.redis.client.builder.failover.event.Event
import com.joshrotenberg.redis.client.builder.failover.event.FailoverEvent
import com.joshrotenberg.redis.client.builder.failover.event.HealthCheckCompletedEvent
import com.joshrotenberg.redis.client.builder.failover.event.HealthCheckFailedEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.LongAdder

/**
 * Collector for metrics related to health checks and failover events.
 * This class tracks response times, success/failure rates, and failover frequency.
 */
class MetricsCollector : AbstractTypedEventListener<Event>(Event::class.java) {
    // Response time metrics
    private val responseTimesByEndpoint = ConcurrentHashMap<Pair<String, Int>, ResponseTimeMetrics>()

    // Success/failure metrics
    private val successCountByEndpoint = ConcurrentHashMap<Pair<String, Int>, LongAdder>()
    private val failureCountByEndpoint = ConcurrentHashMap<Pair<String, Int>, LongAdder>()

    // Failover metrics
    private val failoverCount = AtomicLong(0)
    private val lastFailoverTime = AtomicLong(0)

    /**
     * Handles events for metrics collection.
     *
     * @param event the event to process
     */
    override fun onTypedEvent(event: Event) {
        when (event) {
            is HealthCheckCompletedEvent -> {
                val endpoint = Pair(event.host, event.port)
                // Update response time metrics
                responseTimesByEndpoint.computeIfAbsent(endpoint) { ResponseTimeMetrics() }
                    .recordResponseTime(event.responseTime)

                // Update success count
                successCountByEndpoint.computeIfAbsent(endpoint) { LongAdder() }.increment()
            }
            is HealthCheckFailedEvent -> {
                val endpoint = Pair(event.host, event.port)
                // Update failure count
                failureCountByEndpoint.computeIfAbsent(endpoint) { LongAdder() }.increment()
            }
            is FailoverEvent -> {
                // Update failover metrics
                failoverCount.incrementAndGet()
                lastFailoverTime.set(event.getTimestamp())
            }
        }
    }

    /**
     * Gets the average response time for a specific endpoint.
     *
     * @param host the endpoint host
     * @param port the endpoint port
     * @return the average response time in milliseconds, or null if no data is available
     */
    fun getAverageResponseTime(host: String, port: Int): Double? {
        val metrics = responseTimesByEndpoint[Pair(host, port)]
        return metrics?.getAverageResponseTime()
    }

    /**
     * Gets the success rate for a specific endpoint.
     *
     * @param host the endpoint host
     * @param port the endpoint port
     * @return the success rate as a percentage (0-100), or null if no data is available
     */
    fun getSuccessRate(host: String, port: Int): Double? {
        val endpoint = Pair(host, port)
        val successCount = successCountByEndpoint[endpoint]?.sum() ?: 0
        val failureCount = failureCountByEndpoint[endpoint]?.sum() ?: 0

        val total = successCount + failureCount
        return if (total > 0) {
            (successCount.toDouble() / total) * 100
        } else {
            null
        }
    }

    /**
     * Gets the total number of failovers that have occurred.
     *
     * @return the failover count
     */
    fun getFailoverCount(): Long {
        return failoverCount.get()
    }

    /**
     * Gets the timestamp of the last failover.
     *
     * @return the timestamp in milliseconds, or null if no failover has occurred
     */
    fun getLastFailoverTime(): Long? {
        val time = lastFailoverTime.get()
        return if (time > 0) time else null
    }

    /**
     * Resets all metrics.
     */
    fun reset() {
        responseTimesByEndpoint.clear()
        successCountByEndpoint.clear()
        failureCountByEndpoint.clear()
        failoverCount.set(0)
        lastFailoverTime.set(0)
    }

    /**
     * Helper class for tracking response time metrics.
     */
    private class ResponseTimeMetrics {
        private val count = AtomicLong(0)
        private val sum = AtomicLong(0)
        private val min = AtomicLong(Long.MAX_VALUE)
        private val max = AtomicLong(0)

        /**
         * Records a response time.
         *
         * @param responseTime the response time in milliseconds
         */
        fun recordResponseTime(responseTime: Long) {
            count.incrementAndGet()
            sum.addAndGet(responseTime)

            // Update min if the new value is smaller
            var currentMin = min.get()
            while (responseTime < currentMin) {
                if (min.compareAndSet(currentMin, responseTime)) {
                    break
                }
                currentMin = min.get()
            }

            // Update max if the new value is larger
            var currentMax = max.get()
            while (responseTime > currentMax) {
                if (max.compareAndSet(currentMax, responseTime)) {
                    break
                }
                currentMax = max.get()
            }
        }

        /**
         * Gets the average response time.
         *
         * @return the average response time in milliseconds, or null if no data is available
         */
        fun getAverageResponseTime(): Double? {
            val currentCount = count.get()
            return if (currentCount > 0) {
                sum.get().toDouble() / currentCount
            } else {
                null
            }
        }

        /**
         * Gets the minimum response time.
         *
         * @return the minimum response time in milliseconds, or null if no data is available
         */
        fun getMinResponseTime(): Long? {
            val currentMin = min.get()
            return if (currentMin < Long.MAX_VALUE) currentMin else null
        }

        /**
         * Gets the maximum response time.
         *
         * @return the maximum response time in milliseconds, or null if no data is available
         */
        fun getMaxResponseTime(): Long? {
            val currentMax = max.get()
            return if (currentMax > 0) currentMax else null
        }
    }
}
