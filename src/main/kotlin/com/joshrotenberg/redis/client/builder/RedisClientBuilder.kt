package com.joshrotenberg.redis.client.builder

import com.joshrotenberg.redis.client.builder.failover.RedisFailoverManager
import com.joshrotenberg.redis.client.builder.failover.RedisHealthCheck
import com.joshrotenberg.redis.client.builder.resilience.RedisBulkhead
import com.joshrotenberg.redis.client.builder.resilience.RedisCircuitBreaker
import com.joshrotenberg.redis.client.builder.resilience.RedisRateLimiter
import com.joshrotenberg.redis.client.builder.resilience.RedisRetry
import com.joshrotenberg.redis.client.builder.resilience.RedisTimeLimiter

/**
 * Common interface for all Redis client builders.
 * This interface defines the common methods and properties that all Redis client builders should support,
 * providing a consistent API across different Redis client implementations.
 *
 * @param T The type of Redis client that will be built
 */
interface RedisClientBuilder<T> {
    /**
     * Sets the Redis host.
     *
     * @param host The Redis server host
     * @return This builder instance
     */
    fun host(host: String): RedisClientBuilder<T>

    /**
     * Sets the Redis port.
     *
     * @param port The Redis server port
     * @return This builder instance
     */
    fun port(port: Int): RedisClientBuilder<T>

    /**
     * Sets the Redis password for authentication.
     *
     * @param password The Redis server password
     * @return This builder instance
     */
    fun password(password: String): RedisClientBuilder<T>

    /**
     * Sets the Redis database index.
     *
     * @param database The Redis database index
     * @return This builder instance
     */
    fun database(database: Int): RedisClientBuilder<T>

    /**
     * Sets the connection timeout in milliseconds.
     *
     * @param timeoutMs The connection timeout in milliseconds
     * @return This builder instance
     */
    fun connectionTimeout(timeoutMs: Int): RedisClientBuilder<T>

    /**
     * Sets the socket timeout in milliseconds.
     *
     * @param timeoutMs The socket timeout in milliseconds
     * @return This builder instance
     */
    fun socketTimeout(timeoutMs: Int): RedisClientBuilder<T>

    /**
     * Enables SSL/TLS for the connection.
     *
     * @param useSSL Whether to use SSL/TLS
     * @return This builder instance
     */
    fun ssl(useSSL: Boolean): RedisClientBuilder<T>

    /**
     * Configures a circuit breaker for the Redis client.
     * The provided function will be used to configure the circuit breaker.
     *
     * @param configurer A function that configures the circuit breaker
     * @return This builder instance
     */
    fun withCircuitBreaker(configurer: (RedisCircuitBreaker<T>) -> RedisCircuitBreaker<T>): RedisClientBuilder<T>

    /**
     * Configures a retry mechanism for the Redis client.
     * The provided function will be used to configure the retry mechanism.
     *
     * @param configurer A function that configures the retry mechanism
     * @return This builder instance
     */
    fun withRetry(configurer: (RedisRetry<T>) -> RedisRetry<T>): RedisClientBuilder<T>

    /**
     * Configures a time limiter for the Redis client.
     * The provided function will be used to configure the time limiter.
     *
     * @param configurer A function that configures the time limiter
     * @return This builder instance
     */
    fun withTimeLimiter(configurer: (RedisTimeLimiter<T>) -> RedisTimeLimiter<T>): RedisClientBuilder<T>

    /**
     * Configures a bulkhead for the Redis client.
     * The provided function will be used to configure the bulkhead.
     *
     * @param configurer A function that configures the bulkhead
     * @return This builder instance
     */
    fun withBulkhead(configurer: (RedisBulkhead<T>) -> RedisBulkhead<T>): RedisClientBuilder<T>

    /**
     * Configures a rate limiter for the Redis client.
     * The provided function will be used to configure the rate limiter.
     *
     * @param configurer A function that configures the rate limiter
     * @return This builder instance
     */
    fun withRateLimiter(configurer: (RedisRateLimiter<T>) -> RedisRateLimiter<T>): RedisClientBuilder<T>

    /**
     * Configures a failover manager for the Redis client.
     * The provided function will be used to configure the failover manager.
     *
     * @param configurer A function that configures the failover manager
     * @return This builder instance
     */
    fun withFailover(configurer: (RedisFailoverManager) -> RedisFailoverManager): RedisClientBuilder<T>

    /**
     * Adds a Redis endpoint to be managed for failover.
     *
     * @param host The Redis endpoint host
     * @param port The Redis endpoint port
     * @return This builder instance
     */
    fun addEndpoint(host: String, port: Int): RedisClientBuilder<T>

    /**
     * Registers a health check for a specific Redis endpoint.
     *
     * @param host The Redis endpoint host
     * @param port The Redis endpoint port
     * @param healthCheck The health check to register
     * @return This builder instance
     */
    fun registerHealthCheck(host: String, port: Int, healthCheck: RedisHealthCheck): RedisClientBuilder<T>

    /**
     * Sets the endpoint selection strategy for failover.
     *
     * @param strategy The selection strategy to use
     * @return This builder instance
     */
    fun setSelectionStrategy(strategy: RedisFailoverManager.EndpointSelectionStrategy): RedisClientBuilder<T>

    /**
     * Builds and returns the Redis client instance.
     *
     * @return The configured Redis client instance
     */
    fun build(): T
}
