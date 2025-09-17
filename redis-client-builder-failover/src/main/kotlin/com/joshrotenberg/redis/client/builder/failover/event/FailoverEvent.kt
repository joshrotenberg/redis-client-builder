package com.joshrotenberg.redis.client.builder.failover.event

/**
 * Event triggered when a failover action occurs.
 * This event is fired when the system switches from one Redis endpoint to another.
 */
class FailoverEvent(
    val previousEndpoint: Pair<String, Int>?,
    val newEndpoint: Pair<String, Int>,
    val reason: FailoverReason
) : AbstractEvent()

/**
 * Enum representing the reason for a failover action.
 */
enum class FailoverReason {
    /**
     * Failover occurred because the previous endpoint became unhealthy.
     */
    ENDPOINT_UNHEALTHY,

    /**
     * Failover occurred because a better endpoint became available.
     */
    BETTER_ENDPOINT_AVAILABLE,

    /**
     * Failover occurred due to a manual action.
     */
    MANUAL,

    /**
     * Failover occurred for some other reason.
     */
    OTHER
}
