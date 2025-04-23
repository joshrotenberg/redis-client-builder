package com.joshrotenberg.redis.client.builder.resilience

/**
 * Interface for configuring time limiter functionality for Redis clients.
 * Time limiters are used to limit the duration of Redis operations.
 *
 * @param T The type of Redis client that will be wrapped
 */
interface RedisTimeLimiter<T> : RedisResilience<T> {
    /**
     * Sets the name of the time limiter.
     *
     * @param name The name of the time limiter
     * @return This time limiter instance
     */
    fun name(name: String): RedisTimeLimiter<T>

    /**
     * Sets the timeout duration in milliseconds.
     * If a Redis operation takes longer than this duration, it will be cancelled.
     *
     * @param timeoutMs The timeout duration in milliseconds
     * @return This time limiter instance
     */
    fun timeoutDuration(timeoutMs: Long): RedisTimeLimiter<T>

    /**
     * Sets whether to cancel running futures when the timeout is reached.
     *
     * @param cancelRunningFuture Whether to cancel running futures
     * @return This time limiter instance
     */
    fun cancelRunningFuture(cancelRunningFuture: Boolean): RedisTimeLimiter<T>
}