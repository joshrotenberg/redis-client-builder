package com.joshrotenberg.redis.client.builder.failover

import com.google.common.util.concurrent.AbstractScheduledService
import com.google.common.util.concurrent.Service
import com.joshrotenberg.redis.client.builder.failover.event.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Abstract base class for Redis health checks that provides scheduling and metrics collection.
 * This class extends Guava's AbstractScheduledService to provide scheduled execution of health checks.
 */
abstract class AbstractRedisHealthCheck : AbstractScheduledService(), RedisHealthCheck {
    private val healthy = AtomicBoolean(false)
    private val lastExecutionTime = AtomicLong(0)
    private val lastResponseTime = AtomicLong(0)

    protected var timeoutMs: Long = DEFAULT_TIMEOUT_MS
    protected var retries: Int = DEFAULT_RETRIES
    protected var retryDelayMs: Long = DEFAULT_RETRY_DELAY_MS

    private var schedulePeriodMs: Long = DEFAULT_SCHEDULE_PERIOD_MS
    private var scheduleTimeUnit: TimeUnit = DEFAULT_SCHEDULE_TIME_UNIT

    // Event bus for publishing events
    private var eventBus: EventBus? = null
    private var host: String? = null
    private var port: Int? = null

    /**
     * Sets the event bus for this health check.
     * This allows the health check to publish events when executed.
     *
     * @param eventBus the event bus to use
     * @param host the host of the endpoint this health check is for
     * @param port the port of the endpoint this health check is for
     */
    fun setEventBus(eventBus: EventBus, host: String, port: Int) {
        this.eventBus = eventBus
        this.host = host
        this.port = port
    }

    /**
     * Executes the health check and updates the health status.
     * This method is called by the scheduler.
     */
    override fun runOneIteration() {
        val startTime = System.currentTimeMillis()
        lastExecutionTime.set(startTime)

        // Publish health check started event
        publishHealthCheckEvent(EventType.STARTED)

        val result = executeWithRetries()
        val success = result.first
        val exception = result.second

        healthy.set(success)
        val responseTime = System.currentTimeMillis() - startTime
        lastResponseTime.set(responseTime)

        // Publish health check completed or failed event
        if (success) {
            publishHealthCheckEvent(EventType.COMPLETED, responseTime)
        } else {
            publishHealthCheckEvent(EventType.FAILED, exception = exception)
        }
    }

    /**
     * Helper method to execute the health check with retries.
     * 
     * @return a pair of (success, exception) where success is true if the health check passed,
     *         and exception is the last exception caught (if any)
     */
    private fun executeWithRetries(): Pair<Boolean, Exception?> {
        var success = false
        var attempts = 0
        var exception: Exception? = null

        while (attempts < retries && !success) {
            try {
                success = doExecute()
            } catch (e: IOException) {
                exception = e
            } catch (e: IllegalArgumentException) {
                exception = e
            } catch (e: IllegalStateException) {
                exception = e
            } catch (e: UnsupportedOperationException) {
                exception = e
            } catch (e: InterruptedException) {
                exception = e
            }

            // If not successful and more retries are available, sleep before retrying
            if (!success && attempts < retries - 1) {
                Thread.sleep(retryDelayMs)
            }

            attempts++
        }

        return Pair(success, exception)
    }

    /**
     * Configures the scheduler for this health check.
     * This method is called by AbstractScheduledService to set up the scheduling.
     */
    override fun scheduler(): Scheduler {
        return Scheduler.newFixedRateSchedule(0, schedulePeriodMs, scheduleTimeUnit)
    }

    /**
     * Executes the health check and returns the result.
     * This method is called by runOneIteration() and should be implemented by subclasses.
     * 
     * @return true if the health check passed, false otherwise
     */
    protected abstract fun doExecute(): Boolean

    /**
     * Executes the health check immediately and returns the result.
     * This method can be called manually to execute the health check outside of the schedule.
     * 
     * @return true if the health check passed, false otherwise
     */
    override fun execute(): Boolean {
        val startTime = System.currentTimeMillis()
        lastExecutionTime.set(startTime)

        // Publish health check started event
        publishHealthCheckEvent(EventType.STARTED)

        val result = executeWithRetries()
        val success = result.first
        val exception = result.second

        healthy.set(success)
        val responseTime = System.currentTimeMillis() - startTime
        lastResponseTime.set(responseTime)

        // Publish health check completed or failed event
        if (success) {
            publishHealthCheckEvent(EventType.COMPLETED, responseTime)
        } else {
            publishHealthCheckEvent(EventType.FAILED, exception = exception)
        }

        return isHealthy()
    }

    /**
     * Gets the current health status of the endpoint.
     * 
     * @return true if the endpoint is healthy, false otherwise
     */
    override fun isHealthy(): Boolean {
        return healthy.get()
    }

    /**
     * Gets the last execution time of the health check in milliseconds.
     * 
     * @return the timestamp of the last execution, or null if never executed
     */
    override fun getLastExecutionTime(): Long? {
        val time = lastExecutionTime.get()
        return if (time > 0) time else null
    }

    /**
     * Gets the last response time of the health check in milliseconds.
     * This represents how long the health check operation took to complete.
     * 
     * @return the response time in milliseconds, or null if never executed
     */
    override fun getLastResponseTime(): Long? {
        val time = lastResponseTime.get()
        return if (time > 0) time else null
    }

    /**
     * Configures the timeout for the health check in milliseconds.
     * The health check should fail if it takes longer than this timeout.
     * 
     * @param timeoutMs the timeout in milliseconds
     * @return this health check instance
     */
    override fun timeout(timeoutMs: Long): RedisHealthCheck {
        this.timeoutMs = timeoutMs
        return this
    }

    /**
     * Configures the number of retries for the health check.
     * The health check should retry this many times before considering the endpoint unhealthy.
     * 
     * @param retries the number of retries
     * @return this health check instance
     */
    override fun retries(retries: Int): RedisHealthCheck {
        this.retries = retries
        return this
    }

    /**
     * Configures the delay between retries in milliseconds.
     * 
     * @param delayMs the delay in milliseconds
     * @return this health check instance
     */
    override fun retryDelay(delayMs: Long): RedisHealthCheck {
        this.retryDelayMs = delayMs
        return this
    }

    /**
     * Configures the schedule period for the health check.
     * 
     * @param period the period between executions
     * @param unit the time unit for the period
     * @return this health check instance
     */
    fun schedulePeriod(period: Long, unit: TimeUnit): AbstractRedisHealthCheck {
        this.schedulePeriodMs = TimeUnit.MILLISECONDS.convert(period, unit)
        this.scheduleTimeUnit = unit
        return this
    }


    /**
     * Enum defining the types of health check events.
     */
    private enum class EventType {
        STARTED, COMPLETED, FAILED
    }

    /**
     * Publishes a health check event.
     * 
     * @param eventType the type of event to publish (STARTED, COMPLETED, FAILED)
     * @param responseTime the response time of the health check in milliseconds (for COMPLETED events)
     * @param exception the exception that caused the failure (for FAILED events)
     */
    private fun publishHealthCheckEvent(
        eventType: EventType,
        responseTime: Long = 0,
        exception: Exception? = null
    ) {
        if (eventBus != null && host != null && port != null) {
            val event = when (eventType) {
                EventType.STARTED -> HealthCheckStartedEvent(host!!, port!!, this)
                EventType.COMPLETED -> HealthCheckCompletedEvent(host!!, port!!, this, responseTime)
                EventType.FAILED -> HealthCheckFailedEvent(host!!, port!!, this, exception)
            }
            eventBus!!.publishEvent(event)
        }
    }

    companion object {
        const val DEFAULT_TIMEOUT_MS: Long = 5000
        const val DEFAULT_RETRIES: Int = 3
        const val DEFAULT_RETRY_DELAY_MS: Long = 1000
        const val DEFAULT_SCHEDULE_PERIOD_MS: Long = 30000
        val DEFAULT_SCHEDULE_TIME_UNIT: TimeUnit = TimeUnit.MILLISECONDS
    }
}
