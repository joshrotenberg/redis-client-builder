package com.joshrotenberg.redis.client.builder.failover.event

/**
 * Event triggered when a Redis endpoint's health status changes.
 * This event is fired when an endpoint transitions between healthy and unhealthy states.
 */
class EndpointStatusChangeEvent(
    val host: String,
    val port: Int,
    val isHealthy: Boolean,
    val previousStatus: Boolean
) : AbstractEvent() {
    /**
     * Checks if this event represents a transition to healthy status.
     *
     * @return true if the endpoint transitioned from unhealthy to healthy
     */
    fun isTransitionToHealthy(): Boolean {
        return isHealthy && !previousStatus
    }

    /**
     * Checks if this event represents a transition to unhealthy status.
     *
     * @return true if the endpoint transitioned from healthy to unhealthy
     */
    fun isTransitionToUnhealthy(): Boolean {
        return !isHealthy && previousStatus
    }
}
