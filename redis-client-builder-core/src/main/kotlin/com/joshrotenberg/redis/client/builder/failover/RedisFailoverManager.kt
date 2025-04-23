package com.joshrotenberg.redis.client.builder.failover

/**
 * Interface for the main entry point of the Redis failover feature.
 * The failover manager coordinates health checks, endpoint management,
 * and client builder integration to provide automatic failover capabilities.
 */
interface RedisFailoverManager {
    /**
     * Gets the health check registry used by this failover manager.
     *
     * @return the health check registry
     */
    fun getHealthCheckRegistry(): RedisHealthCheckRegistry

    /**
     * Gets the endpoint manager used by this failover manager.
     *
     * @return the endpoint manager
     */
    fun getEndpointManager(): RedisEndpointManager

    /**
     * Adds a Redis endpoint to be managed for failover.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @return this manager instance
     */
    fun addEndpoint(host: String, port: Int): RedisFailoverManager

    /**
     * Removes a Redis endpoint from failover management.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @return this manager instance
     */
    fun removeEndpoint(host: String, port: Int): RedisFailoverManager

    /**
     * Registers a health check for a specific Redis endpoint.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @param healthCheck the health check to register
     * @return this manager instance
     */
    fun registerHealthCheck(host: String, port: Int, healthCheck: RedisHealthCheck): RedisFailoverManager

    /**
     * Sets the endpoint selection strategy for failover.
     *
     * @param strategy the selection strategy to use
     * @return this manager instance
     */
    fun setSelectionStrategy(strategy: RedisEndpointManager.EndpointSelectionStrategy): RedisFailoverManager

    /**
     * Starts the failover manager, which begins monitoring endpoints and executing health checks.
     *
     * @return this manager instance
     */
    fun start(): RedisFailoverManager

    /**
     * Stops the failover manager, which stops monitoring endpoints and executing health checks.
     *
     * @return this manager instance
     */
    fun stop(): RedisFailoverManager

    /**
     * Checks if the failover manager is currently running.
     *
     * @return true if the manager is running, false otherwise
     */
    fun isRunning(): Boolean

    /**
     * Gets a healthy Redis endpoint based on the configured selection strategy.
     *
     * @return a selected endpoint pair (host, port), or null if no healthy endpoints are available
     */
    fun getHealthyEndpoint(): Pair<String, Int>?

    /**
     * Registers a listener for failover events.
     *
     * @param listener the event listener to register
     * @return this manager instance
     */
    fun registerEventListener(listener: FailoverEventListener): RedisFailoverManager

    /**
     * Unregisters a listener for failover events.
     *
     * @param listener the event listener to unregister
     * @return this manager instance
     */
    fun unregisterEventListener(listener: FailoverEventListener): RedisFailoverManager

    /**
     * Interface for failover event listeners.
     * Event listeners receive notifications about failover-related events.
     */
    interface FailoverEventListener {
        /**
         * Called when a Redis endpoint's health status changes.
         *
         * @param host the Redis endpoint host
         * @param port the Redis endpoint port
         * @param isHealthy the new health status
         */
        fun onEndpointHealthChanged(host: String, port: Int, isHealthy: Boolean)

        /**
         * Called when a failover occurs (switching from one endpoint to another).
         *
         * @param previousEndpoint the previous endpoint that was in use, or null if this is the first endpoint
         * @param newEndpoint the new endpoint that is now in use
         */
        fun onFailover(previousEndpoint: Pair<String, Int>?, newEndpoint: Pair<String, Int>)

        /**
         * Called when a health check is executed.
         *
         * @param host the Redis endpoint host
         * @param port the Redis endpoint port
         * @param healthCheck the health check that was executed
         * @param result the result of the health check
         */
        fun onHealthCheckExecuted(host: String, port: Int, healthCheck: RedisHealthCheck, result: Boolean)
    }
}