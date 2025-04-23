package com.joshrotenberg.redis.client.builder.resilience

/**
 * Interface for configuring retry functionality for Redis clients.
 *
 * @param T The type of Redis client that will be wrapped
 */
interface RedisRetry<T> : RedisResilience<T> {
    /**
     * Sets the name of the retry.
     *
     * @param name The name of the retry
     * @return This retry instance
     */
    fun name(name: String): RedisRetry<T>

    /**
     * Sets the maximum number of retry attempts.
     *
     * @param attempts The maximum number of retry attempts
     * @return This retry instance
     */
    fun maxAttempts(attempts: Int): RedisRetry<T>

    /**
     * Sets the wait duration in milliseconds between retry attempts.
     *
     * @param durationMs The wait duration in milliseconds
     * @return This retry instance
     */
    fun waitDuration(durationMs: Long): RedisRetry<T>

    /**
     * Sets the retry backoff configuration.
     * If enabled, the wait duration will increase exponentially between retries.
     *
     * @param enabled Whether to enable exponential backoff
     * @return This retry instance
     */
    fun enableExponentialBackoff(enabled: Boolean): RedisRetry<T>

    /**
     * Sets the multiplier for exponential backoff.
     * This is only used if exponential backoff is enabled.
     *
     * @param multiplier The multiplier for exponential backoff
     * @return This retry instance
     */
    fun exponentialBackoffMultiplier(multiplier: Double): RedisRetry<T>

    /**
     * Sets the retry on result predicate.
     * If the result of a Redis operation matches the specified value, a retry will be triggered.
     *
     * @param value The result value that should trigger a retry
     * @return This retry instance
     */
    fun retryOnResult(value: Any?): RedisRetry<T>
}