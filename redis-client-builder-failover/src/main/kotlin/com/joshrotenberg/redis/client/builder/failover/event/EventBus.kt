package com.joshrotenberg.redis.client.builder.failover.event

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Event bus for distributing events to registered listeners.
 * The event bus is responsible for maintaining a list of listeners and notifying them when events occur.
 */
class EventBus {
    private val listeners = CopyOnWriteArrayList<EventListener>()

    /**
     * Registers a listener with the event bus.
     *
     * @param listener the listener to register
     * @return this event bus instance
     */
    fun registerListener(listener: EventListener): EventBus {
        listeners.add(listener)
        return this
    }

    /**
     * Unregisters a listener from the event bus.
     *
     * @param listener the listener to unregister
     * @return this event bus instance
     */
    fun unregisterListener(listener: EventListener): EventBus {
        listeners.remove(listener)
        return this
    }

    /**
     * Publishes an event to all registered listeners.
     *
     * @param event the event to publish
     */
    fun publishEvent(event: Event) {
        for (listener in listeners) {
            try {
                listener.onEvent(event)
            } catch (e: Exception) {
                // Log the exception but continue notifying other listeners
                System.err.println("Error notifying listener: ${e.message}")
                // Using System.err instead of printStackTrace for better logging
                System.err.println("Stack trace: ${e.stackTraceToString()}")
            }
        }
    }

    /**
     * Gets all registered listeners.
     *
     * @return a list of all registered listeners
     */
    fun getListeners(): List<EventListener> {
        return listeners.toList()
    }

    /**
     * Clears all registered listeners.
     *
     * @return this event bus instance
     */
    fun clearListeners(): EventBus {
        listeners.clear()
        return this
    }
}
