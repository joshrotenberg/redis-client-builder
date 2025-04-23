package com.joshrotenberg.redis.client.builder.failover

/**
 * Interface for health checks that verify Redis endpoint availability and functionality.
 * Health checks are used to determine if a Redis endpoint is operational and can be used
 * for client connections.
 */
interface RedisHealthCheck {
    /**
     * Executes the health check against a Redis endpoint.
     * This method should perform the actual check operation and update the health status.
     *
     * @return true if the health check passed, false otherwise
     */
    fun execute(): Boolean

    /**
     * Gets the current health status of the endpoint.
     *
     * @return true if the endpoint is healthy, false otherwise
     */
    fun isHealthy(): Boolean

    /**
     * Gets the last execution time of the health check in milliseconds.
     *
     * @return the timestamp of the last execution, or null if never executed
     */
    fun getLastExecutionTime(): Long?

    /**
     * Gets the last response time of the health check in milliseconds.
     * This represents how long the health check operation took to complete.
     *
     * @return the response time in milliseconds, or null if never executed
     */
    fun getLastResponseTime(): Long?

    /**
     * Configures the timeout for the health check in milliseconds.
     * The health check should fail if it takes longer than this timeout.
     *
     * @param timeoutMs the timeout in milliseconds
     * @return this health check instance
     */
    fun timeout(timeoutMs: Long): RedisHealthCheck

    /**
     * Configures the number of retries for the health check.
     * The health check should retry this many times before considering the endpoint unhealthy.
     *
     * @param retries the number of retries
     * @return this health check instance
     */
    fun retries(retries: Int): RedisHealthCheck

    /**
     * Configures the delay between retries in milliseconds.
     *
     * @param delayMs the delay in milliseconds
     * @return this health check instance
     */
    fun retryDelay(delayMs: Long): RedisHealthCheck
}