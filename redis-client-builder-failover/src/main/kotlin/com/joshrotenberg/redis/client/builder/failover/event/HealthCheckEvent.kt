package com.joshrotenberg.redis.client.builder.failover.event

import com.joshrotenberg.redis.client.builder.failover.RedisHealthCheck

/**
 * Base class for all health check related events.
 * Health check events are triggered when health checks are executed.
 */
abstract class HealthCheckEvent(
    val host: String,
    val port: Int,
    val healthCheck: RedisHealthCheck
) : AbstractEvent()

/**
 * Event triggered when a health check is started.
 */
class HealthCheckStartedEvent(
    host: String,
    port: Int,
    healthCheck: RedisHealthCheck
) : HealthCheckEvent(host, port, healthCheck)

/**
 * Event triggered when a health check is completed successfully.
 */
class HealthCheckCompletedEvent(
    host: String,
    port: Int,
    healthCheck: RedisHealthCheck,
    val responseTime: Long
) : HealthCheckEvent(host, port, healthCheck)

/**
 * Event triggered when a health check fails.
 */
class HealthCheckFailedEvent(
    host: String,
    port: Int,
    healthCheck: RedisHealthCheck,
    val exception: Throwable? = null
) : HealthCheckEvent(host, port, healthCheck)
