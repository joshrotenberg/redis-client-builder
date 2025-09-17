package com.joshrotenberg.redis.client.builder.failover

/**
 * Interface for a registry that manages multiple Redis health checks.
 * The registry allows registering, unregistering, and querying health checks for Redis endpoints.
 */
interface RedisHealthCheckRegistry {
    /**
     * Registers a health check for a specific Redis endpoint.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @param healthCheck the health check to register
     * @return this registry instance
     */
    fun registerHealthCheck(host: String, port: Int, healthCheck: RedisHealthCheck): RedisHealthCheckRegistry

    /**
     * Unregisters all health checks for a specific Redis endpoint.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @return this registry instance
     */
    fun unregisterHealthChecks(host: String, port: Int): RedisHealthCheckRegistry

    /**
     * Unregisters a specific health check for a Redis endpoint.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @param healthCheck the health check to unregister
     * @return this registry instance
     */
    fun unregisterHealthCheck(host: String, port: Int, healthCheck: RedisHealthCheck): RedisHealthCheckRegistry

    /**
     * Gets all health checks registered for a specific Redis endpoint.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @return a list of health checks for the endpoint, or an empty list if none are registered
     */
    fun getHealthChecks(host: String, port: Int): List<RedisHealthCheck>

    /**
     * Gets all registered health checks across all endpoints.
     *
     * @return a map of endpoint (host:port) to list of health checks
     */
    fun getAllHealthChecks(): Map<Pair<String, Int>, List<RedisHealthCheck>>

    /**
     * Executes all health checks for a specific Redis endpoint.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @return true if all health checks pass, false if any fail
     */
    fun executeHealthChecks(host: String, port: Int): Boolean

    /**
     * Executes all registered health checks across all endpoints.
     *
     * @return a map of endpoint (host:port) to health check result (true if all checks pass, false otherwise)
     */
    fun executeAllHealthChecks(): Map<Pair<String, Int>, Boolean>

    /**
     * Checks if a specific Redis endpoint is healthy based on its registered health checks.
     *
     * @param host the Redis endpoint host
     * @param port the Redis endpoint port
     * @return true if all health checks for the endpoint report healthy, false otherwise
     */
    fun isEndpointHealthy(host: String, port: Int): Boolean

    /**
     * Gets all healthy Redis endpoints based on their registered health checks.
     *
     * @return a list of healthy endpoint pairs (host, port)
     */
    fun getHealthyEndpoints(): List<Pair<String, Int>>
}
