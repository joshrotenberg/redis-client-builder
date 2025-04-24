package com.joshrotenberg.redis.client.builder

import com.joshrotenberg.redis.client.builder.resilience.RedisBulkhead
import com.joshrotenberg.redis.client.builder.resilience.RedisCircuitBreaker
import com.joshrotenberg.redis.client.builder.resilience.RedisRateLimiter
import com.joshrotenberg.redis.client.builder.resilience.RedisRetry
import com.joshrotenberg.redis.client.builder.resilience.RedisTimeLimiter

/**
 * Common interface for all Redis cluster client builders.
 * This interface defines the common methods and properties that all Redis cluster client builders should support,
 * providing a consistent API across different Redis client implementations.
 *
 * @param T The type of Redis cluster client that will be built
 */
interface RedisClusterClientBuilder<T> {
    /**
     * Adds a Redis cluster node to the configuration.
     *
     * @param host The Redis server host
     * @param port The Redis server port
     * @return This builder instance
     */
    fun addNode(host: String, port: Int): RedisClusterClientBuilder<T>

    /**
     * Sets the password for authentication with the Redis cluster.
     *
     * @param password The Redis cluster password
     * @return This builder instance
     */
    fun password(password: String): RedisClusterClientBuilder<T>

    /**
     * Sets the connection timeout in milliseconds.
     *
     * @param timeoutMs The connection timeout in milliseconds
     * @return This builder instance
     */
    fun connectionTimeout(timeoutMs: Int): RedisClusterClientBuilder<T>

    /**
     * Sets the socket timeout in milliseconds.
     *
     * @param timeoutMs The socket timeout in milliseconds
     * @return This builder instance
     */
    fun socketTimeout(timeoutMs: Int): RedisClusterClientBuilder<T>

    /**
     * Enables SSL/TLS for the connection.
     *
     * @param useSSL Whether to use SSL/TLS
     * @return This builder instance
     */
    fun ssl(useSSL: Boolean): RedisClusterClientBuilder<T>

    /**
     * Sets the maximum number of redirections to follow during command execution.
     *
     * @param maxRedirections The maximum number of redirections
     * @return This builder instance
     */
    fun maxRedirections(maxRedirections: Int): RedisClusterClientBuilder<T>

    /**
     * Configures a circuit breaker for the Redis cluster client.
     * The provided function will be used to configure the circuit breaker.
     *
     * @param configurer A function that configures the circuit breaker
     * @return This builder instance
     */
    fun withCircuitBreaker(configurer: (RedisCircuitBreaker<T>) -> RedisCircuitBreaker<T>): RedisClusterClientBuilder<T>

    /**
     * Configures a retry mechanism for the Redis cluster client.
     * The provided function will be used to configure the retry mechanism.
     *
     * @param configurer A function that configures the retry mechanism
     * @return This builder instance
     */
    fun withRetry(configurer: (RedisRetry<T>) -> RedisRetry<T>): RedisClusterClientBuilder<T>

    /**
     * Configures a time limiter for the Redis cluster client.
     * The provided function will be used to configure the time limiter.
     *
     * @param configurer A function that configures the time limiter
     * @return This builder instance
     */
    fun withTimeLimiter(configurer: (RedisTimeLimiter<T>) -> RedisTimeLimiter<T>): RedisClusterClientBuilder<T>

    /**
     * Configures a bulkhead for the Redis cluster client.
     * The provided function will be used to configure the bulkhead.
     *
     * @param configurer A function that configures the bulkhead
     * @return This builder instance
     */
    fun withBulkhead(configurer: (RedisBulkhead<T>) -> RedisBulkhead<T>): RedisClusterClientBuilder<T>

    /**
     * Configures a rate limiter for the Redis cluster client.
     * The provided function will be used to configure the rate limiter.
     *
     * @param configurer A function that configures the rate limiter
     * @return This builder instance
     */
    fun withRateLimiter(configurer: (RedisRateLimiter<T>) -> RedisRateLimiter<T>): RedisClusterClientBuilder<T>

    /**
     * Builds and returns the Redis cluster client instance.
     *
     * @return The configured Redis cluster client instance
     */
    fun build(): T
}