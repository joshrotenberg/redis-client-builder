package com.joshrotenberg.redis.client.builder.failover

/**
 * Interface for managing Redis endpoints and their health status.
 * The endpoint manager is responsible for tracking available Redis endpoints,
 * their health status, and selecting healthy endpoints for client connections.
 */
interface RedisEndpointManager {
    /**
     * Adds a Redis endpoint to the manager.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @return this manager instance
     */
    fun addEndpoint(host: String, port: Int): RedisEndpointManager

    /**
     * Removes a Redis endpoint from the manager.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @return this manager instance
     */
    fun removeEndpoint(host: String, port: Int): RedisEndpointManager

    /**
     * Gets all registered Redis endpoints.
     *
     * @return a list of endpoint pairs (host, port)
     */
    fun getEndpoints(): List<Pair<String, Int>>

    /**
     * Updates the health status of a Redis endpoint.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @param isHealthy true if the endpoint is healthy, false otherwise
     * @return this manager instance
     */
    fun updateEndpointHealth(host: String, port: Int, isHealthy: Boolean): RedisEndpointManager

    /**
     * Gets the health status of a Redis endpoint.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @return true if the endpoint is healthy, false otherwise
     */
    fun isEndpointHealthy(host: String, port: Int): Boolean

    /**
     * Gets all healthy Redis endpoints.
     *
     * @return a list of healthy endpoint pairs (host, port)
     */
    fun getHealthyEndpoints(): List<Pair<String, Int>>

    /**
     * Selects a healthy Redis endpoint based on the configured selection strategy.
     *
     * @return a selected endpoint pair (host, port), or null if no healthy endpoints are available
     */
    fun selectHealthyEndpoint(): Pair<String, Int>?

    /**
     * Sets the endpoint selection strategy.
     *
     * @param strategy the selection strategy to use
     * @return this manager instance
     */
    fun setSelectionStrategy(strategy: EndpointSelectionStrategy): RedisEndpointManager

    /**
     * Gets the current endpoint selection strategy.
     *
     * @return the current selection strategy
     */
    fun getSelectionStrategy(): EndpointSelectionStrategy

    /**
     * Interface for endpoint selection strategies.
     * Selection strategies determine which healthy endpoint to use for client connections.
     */
    interface EndpointSelectionStrategy {
        /**
         * Selects a healthy endpoint from the provided list.
         *
         * @param endpoints the list of healthy endpoints to select from
         * @return the selected endpoint, or null if the list is empty
         */
        fun selectEndpoint(endpoints: List<Pair<String, Int>>): Pair<String, Int>?
    }
}
