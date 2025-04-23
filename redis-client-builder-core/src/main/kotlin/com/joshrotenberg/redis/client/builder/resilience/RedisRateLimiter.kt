package com.joshrotenberg.redis.client.builder.resilience

/**
 * Interface for configuring rate limiter functionality for Redis clients.
 * Rate limiters are used to limit the rate of Redis operations.
 *
 * @param T The type of Redis client that will be wrapped
 */
interface RedisRateLimiter<T> : RedisResilience<T> {
    /**
     * Sets the name of the rate limiter.
     *
     * @param name The name of the rate limiter
     * @return This rate limiter instance
     */
    fun name(name: String): RedisRateLimiter<T>

    /**
     * Sets the limit for the number of permissions in the refresh period.
     *
     * @param limit The limit for the number of permissions
     * @return This rate limiter instance
     */
    fun limitForPeriod(limit: Int): RedisRateLimiter<T>

    /**
     * Sets the period in nanoseconds for which the permissions limit applies.
     *
     * @param periodNanos The period in nanoseconds
     * @return This rate limiter instance
     */
    fun limitRefreshPeriod(periodNanos: Long): RedisRateLimiter<T>

    /**
     * Sets the default wait time in milliseconds for a permission.
     *
     * @param timeoutMs The timeout in milliseconds
     * @return This rate limiter instance
     */
    fun timeoutDuration(timeoutMs: Long): RedisRateLimiter<T>
}