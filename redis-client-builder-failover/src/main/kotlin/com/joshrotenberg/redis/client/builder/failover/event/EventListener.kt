package com.joshrotenberg.redis.client.builder.failover.event

/**
 * Interface for event listeners in the Redis failover system.
 * Event listeners receive notifications about events in the system.
 */
interface EventListener {
    /**
     * Called when an event occurs in the system.
     *
     * @param event the event that occurred
     */
    fun onEvent(event: Event)
}

/**
 * Interface for typed event listeners in the Redis failover system.
 * Typed event listeners receive notifications about specific types of events.
 *
 * @param T the type of event this listener is interested in
 */
interface TypedEventListener<T : Event> : EventListener {
    /**
     * Called when an event of the specified type occurs in the system.
     *
     * @param event the event that occurred
     */
    fun onTypedEvent(event: T)

    /**
     * Called when any event occurs in the system.
     * This implementation filters events based on their type and calls onTypedEvent for matching events.
     *
     * @param event the event that occurred
     */
    override fun onEvent(event: Event) {
        @Suppress("UNCHECKED_CAST")
        if (isInterestedIn(event)) {
            onTypedEvent(event as T)
        }
    }

    /**
     * Checks if this listener is interested in the given event.
     *
     * @param event the event to check
     * @return true if this listener is interested in the event, false otherwise
     */
    fun isInterestedIn(event: Event): Boolean
}

/**
 * Abstract base class for typed event listeners.
 * Provides a default implementation of isInterestedIn based on the event class.
 *
 * @param T the type of event this listener is interested in
 * @param eventClass the class of events this listener is interested in
 */
abstract class AbstractTypedEventListener<T : Event>(
    private val eventClass: Class<T>
) : TypedEventListener<T> {
    /**
     * Checks if this listener is interested in the given event based on its class.
     *
     * @param event the event to check
     * @return true if the event is an instance of the specified class, false otherwise
     */
    override fun isInterestedIn(event: Event): Boolean {
        return eventClass.isInstance(event)
    }
}
