package com.joshrotenberg.redis.client.builder.failover.event

/**
 * Base interface for all events in the Redis failover system.
 * Events are used to notify listeners about important occurrences in the system.
 */
interface Event {
    /**
     * Gets the timestamp when the event occurred.
     *
     * @return the timestamp in milliseconds
     */
    fun getTimestamp(): Long
}

/**
 * Abstract base class for all events in the Redis failover system.
 * Provides common functionality for all events.
 */
abstract class AbstractEvent : Event {
    private val timestamp: Long = System.currentTimeMillis()

    /**
     * Gets the timestamp when the event occurred.
     *
     * @return the timestamp in milliseconds
     */
    override fun getTimestamp(): Long {
        return timestamp
    }
}
