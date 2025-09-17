package com.joshrotenberg.redis.client.builder.failover

import com.joshrotenberg.redis.client.builder.failover.event.EndpointStatusChangeEvent
import com.joshrotenberg.redis.client.builder.failover.event.EventBus
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implementation of the RedisEndpointManager interface.
 * This class manages Redis endpoints, their health status, and provides
 * endpoint selection based on different strategies.
 */
class RedisEndpointManagerImpl : RedisEndpointManager {
    // Map to store endpoints and their health status
    private val endpoints = ConcurrentHashMap<Pair<String, Int>, Boolean>()

    // Default selection strategy
    private var selectionStrategy: RedisEndpointManager.EndpointSelectionStrategy = RoundRobinSelectionStrategy()

    // Event bus for publishing events
    private var eventBus: EventBus? = null

    /**
     * Sets the event bus for this endpoint manager.
     * This allows the endpoint manager to publish events when endpoint health status changes.
     *
     * @param eventBus the event bus to use
     * @return this endpoint manager instance
     */
    fun setEventBus(eventBus: EventBus): RedisEndpointManagerImpl {
        this.eventBus = eventBus
        return this
    }

    override fun addEndpoint(host: String, port: Int): RedisEndpointManager {
        endpoints[Pair(host, port)] = true // Default to healthy
        return this
    }

    override fun removeEndpoint(host: String, port: Int): RedisEndpointManager {
        endpoints.remove(Pair(host, port))
        return this
    }

    override fun getEndpoints(): List<Pair<String, Int>> {
        return endpoints.keys.toList()
    }

    override fun updateEndpointHealth(host: String, port: Int, isHealthy: Boolean): RedisEndpointManager {
        val endpoint = Pair(host, port)
        if (endpoints.containsKey(endpoint)) {
            val previousStatus = endpoints[endpoint] ?: false
            endpoints[endpoint] = isHealthy

            // If the health status has changed, publish an event
            if (previousStatus != isHealthy && eventBus != null) {
                val event = EndpointStatusChangeEvent(host, port, isHealthy, previousStatus)
                eventBus!!.publishEvent(event)
            }
        }
        return this
    }

    override fun isEndpointHealthy(host: String, port: Int): Boolean {
        return endpoints[Pair(host, port)] ?: false
    }

    override fun getHealthyEndpoints(): List<Pair<String, Int>> {
        return endpoints.entries
            .filter { it.value }
            .map { it.key }
            .toList()
    }

    override fun selectHealthyEndpoint(): Pair<String, Int>? {
        val healthyEndpoints = getHealthyEndpoints()
        return selectionStrategy.selectEndpoint(healthyEndpoints)
    }

    override fun setSelectionStrategy(strategy: RedisEndpointManager.EndpointSelectionStrategy): RedisEndpointManager {
        this.selectionStrategy = strategy
        return this
    }

    override fun getSelectionStrategy(): RedisEndpointManager.EndpointSelectionStrategy {
        return selectionStrategy
    }

    /**
     * Round-robin selection strategy implementation.
     * Selects endpoints in a circular sequence.
     */
    class RoundRobinSelectionStrategy : RedisEndpointManager.EndpointSelectionStrategy {
        private val counter = AtomicInteger(0)

        override fun selectEndpoint(endpoints: List<Pair<String, Int>>): Pair<String, Int>? {
            if (endpoints.isEmpty()) {
                return null
            }

            val index = counter.getAndIncrement() % endpoints.size
            return endpoints[index]
        }
    }

    /**
     * Random selection strategy implementation.
     * Selects endpoints randomly.
     */
    class RandomSelectionStrategy : RedisEndpointManager.EndpointSelectionStrategy {
        override fun selectEndpoint(endpoints: List<Pair<String, Int>>): Pair<String, Int>? {
            if (endpoints.isEmpty()) {
                return null
            }

            return endpoints.random()
        }
    }

    /**
     * Weighted selection strategy implementation.
     * Selects endpoints based on weights (response time).
     */
    class WeightedSelectionStrategy : RedisEndpointManager.EndpointSelectionStrategy {
        private val weights = ConcurrentHashMap<Pair<String, Int>, Double>()

        /**
         * Updates the weight for an endpoint.
         * Lower weights are preferred (e.g., faster response times).
         *
         * @param endpoint the endpoint to update
         * @param weight the weight value (e.g., response time in ms)
         */
        fun updateWeight(endpoint: Pair<String, Int>, weight: Double) {
            weights[endpoint] = weight
        }

        override fun selectEndpoint(endpoints: List<Pair<String, Int>>): Pair<String, Int>? {
            if (endpoints.isEmpty()) {
                return null
            }

            // If we don't have weights for all endpoints, default to the first one
            if (endpoints.any { !weights.containsKey(it) }) {
                return endpoints.first()
            }

            // Select the endpoint with the lowest weight
            return endpoints.minByOrNull { weights[it] ?: Double.MAX_VALUE }
        }
    }

    /**
     * Priority-based selection strategy implementation.
     * Selects endpoints based on priority.
     */
    class PrioritySelectionStrategy : RedisEndpointManager.EndpointSelectionStrategy {
        private val priorities = ConcurrentHashMap<Pair<String, Int>, Int>()

        /**
         * Sets the priority for an endpoint.
         * Lower priority values are preferred (1 is highest priority).
         *
         * @param endpoint the endpoint to update
         * @param priority the priority value (1 is highest)
         */
        fun setPriority(endpoint: Pair<String, Int>, priority: Int) {
            priorities[endpoint] = priority
        }

        override fun selectEndpoint(endpoints: List<Pair<String, Int>>): Pair<String, Int>? {
            if (endpoints.isEmpty()) {
                return null
            }

            // If we don't have priorities for all endpoints, default to the first one
            if (endpoints.any { !priorities.containsKey(it) }) {
                return endpoints.first()
            }

            // Select the endpoint with the highest priority (lowest number)
            return endpoints.minByOrNull { priorities[it] ?: Int.MAX_VALUE }
        }
    }
}
