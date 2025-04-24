package com.joshrotenberg.redis.client.builder.failover

import com.joshrotenberg.redis.client.builder.failover.event.*
import com.joshrotenberg.redis.client.builder.failover.metrics.MetricsCollector

/**
 * Implementation of the RedisFailoverManager interface.
 * This class coordinates health checks, endpoint management, and event publishing
 * to provide automatic failover capabilities.
 */
class RedisFailoverManagerImpl : RedisFailoverManager {
    private val healthCheckRegistry: RedisHealthCheckRegistry = RedisHealthCheckRegistryImpl()
    private val endpointManager: RedisEndpointManager = RedisEndpointManagerImpl()
    private val eventBus: EventBus = EventBus()
    private val metricsCollector: MetricsCollector = MetricsCollector()

    private var running = false
    private var previousEndpoint: Pair<String, Int>? = null

    init {
        // Register the metrics collector as a listener for events
        eventBus.registerListener(metricsCollector)

        // Set the event bus for the endpoint manager
        if (endpointManager is RedisEndpointManagerImpl) {
            endpointManager.setEventBus(eventBus)
        }
    }

    override fun getHealthCheckRegistry(): RedisHealthCheckRegistry {
        return healthCheckRegistry
    }

    override fun getEndpointManager(): RedisEndpointManager {
        return endpointManager
    }

    override fun addEndpoint(host: String, port: Int): RedisFailoverManager {
        endpointManager.addEndpoint(host, port)
        return this
    }

    override fun removeEndpoint(host: String, port: Int): RedisFailoverManager {
        endpointManager.removeEndpoint(host, port)
        healthCheckRegistry.unregisterHealthChecks(host, port)
        return this
    }

    override fun registerHealthCheck(host: String, port: Int, healthCheck: RedisHealthCheck): RedisFailoverManager {
        healthCheckRegistry.registerHealthCheck(host, port, healthCheck)

        // If the health check is an AbstractRedisHealthCheck, set its event bus
        if (healthCheck is AbstractRedisHealthCheck) {
            healthCheck.setEventBus(eventBus, host, port)
        }

        return this
    }

    override fun setSelectionStrategy(strategy: RedisEndpointManager.EndpointSelectionStrategy): RedisFailoverManager {
        endpointManager.setSelectionStrategy(strategy)
        return this
    }

    override fun start(): RedisFailoverManager {
        if (!running) {
            running = true
            startAllHealthChecks()
        }
        return this
    }

    /**
     * Helper method to start all registered health checks.
     */
    private fun startAllHealthChecks() {
        val allHealthChecks = healthCheckRegistry.getAllHealthChecks()
        for ((_, healthChecks) in allHealthChecks) {
            for (healthCheck in healthChecks) {
                if (healthCheck is AbstractRedisHealthCheck) {
                    healthCheck.startAsync()
                }
            }
        }
    }

    override fun stop(): RedisFailoverManager {
        if (running) {
            running = false
            stopAllHealthChecks()
        }
        return this
    }

    /**
     * Helper method to stop all registered health checks.
     */
    private fun stopAllHealthChecks() {
        val allHealthChecks = healthCheckRegistry.getAllHealthChecks()
        for ((_, healthChecks) in allHealthChecks) {
            for (healthCheck in healthChecks) {
                if (healthCheck is AbstractRedisHealthCheck) {
                    healthCheck.stopAsync()
                }
            }
        }
    }

    override fun isRunning(): Boolean {
        return running
    }

    override fun getHealthyEndpoint(): Pair<String, Int>? {
        val newEndpoint = endpointManager.selectHealthyEndpoint()

        // If we have a new endpoint and it's different from the previous one, publish a failover event
        if (newEndpoint != null && newEndpoint != previousEndpoint) {
            val reason = if (previousEndpoint == null) {
                FailoverReason.OTHER // First endpoint selection
            } else if (!endpointManager.isEndpointHealthy(previousEndpoint!!.first, previousEndpoint!!.second)) {
                FailoverReason.ENDPOINT_UNHEALTHY
            } else {
                FailoverReason.BETTER_ENDPOINT_AVAILABLE
            }

            val failoverEvent = FailoverEvent(previousEndpoint, newEndpoint, reason)
            eventBus.publishEvent(failoverEvent)

            // Notify failover event listeners
            for (listener in failoverEventListeners) {
                try {
                    listener.onFailover(previousEndpoint, newEndpoint)
                } catch (e: Exception) {
                    // Log the exception but continue notifying other listeners
                    System.err.println("Error notifying failover listener: ${e.message}")
                    e.printStackTrace()
                }
            }

            previousEndpoint = newEndpoint
        }

        return newEndpoint
    }

    private val failoverEventListeners = mutableListOf<RedisFailoverManager.FailoverEventListener>()

    override fun registerEventListener(listener: RedisFailoverManager.FailoverEventListener): RedisFailoverManager {
        failoverEventListeners.add(listener)
        return this
    }

    override fun unregisterEventListener(listener: RedisFailoverManager.FailoverEventListener): RedisFailoverManager {
        failoverEventListeners.remove(listener)
        return this
    }

    /**
     * Gets the internal components used by this failover manager.
     * This method is primarily for testing and debugging purposes.
     *
     * @return a pair of (eventBus, metricsCollector)
     */
    fun getInternalComponents(): Pair<EventBus, MetricsCollector> {
        return Pair(eventBus, metricsCollector)
    }
}
