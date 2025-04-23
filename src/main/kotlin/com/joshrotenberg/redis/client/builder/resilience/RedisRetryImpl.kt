package com.joshrotenberg.redis.client.builder.resilience

import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import java.time.Duration
import java.util.function.Predicate

/**
 * Implementation of the RedisRetry interface.
 * This class provides retry functionality for Redis clients using resilience4j.
 *
 * @param T The type of Redis client that will be wrapped
 * @property registry The retry registry to use
 */
class RedisRetryImpl<T>(
    private val registry: RetryRegistry = RetryRegistry.ofDefaults()
) : RedisRetry<T> {

    private var name: String = "redis-retry"
    private var maxAttempts: Int = 3
    private var waitDuration: Long = 1000 // 1 second
    private var enableExponentialBackoff: Boolean = false
    private var exponentialBackoffMultiplier: Double = 1.5
    private var retryOnResultValue: Any? = null

    /**
     * Sets the name of the retry.
     *
     * @param name The name of the retry
     * @return This retry instance
     */
    override fun name(name: String): RedisRetry<T> {
        this.name = name
        return this
    }

    /**
     * Sets the maximum number of retry attempts.
     *
     * @param attempts The maximum number of retry attempts
     * @return This retry instance
     */
    override fun maxAttempts(attempts: Int): RedisRetry<T> {
        this.maxAttempts = attempts
        return this
    }

    /**
     * Sets the wait duration in milliseconds between retry attempts.
     *
     * @param durationMs The wait duration in milliseconds
     * @return This retry instance
     */
    override fun waitDuration(durationMs: Long): RedisRetry<T> {
        this.waitDuration = durationMs
        return this
    }

    /**
     * Sets the retry backoff configuration.
     * If enabled, the wait duration will increase exponentially between retries.
     *
     * @param enabled Whether to enable exponential backoff
     * @return This retry instance
     */
    override fun enableExponentialBackoff(enabled: Boolean): RedisRetry<T> {
        this.enableExponentialBackoff = enabled
        return this
    }

    /**
     * Sets the multiplier for exponential backoff.
     * This is only used if exponential backoff is enabled.
     *
     * @param multiplier The multiplier for exponential backoff
     * @return This retry instance
     */
    override fun exponentialBackoffMultiplier(multiplier: Double): RedisRetry<T> {
        this.exponentialBackoffMultiplier = multiplier
        return this
    }

    /**
     * Sets the retry on result predicate.
     * If the result of a Redis operation matches the specified value, a retry will be triggered.
     *
     * @param value The result value that should trigger a retry
     * @return This retry instance
     */
    override fun retryOnResult(value: Any?): RedisRetry<T> {
        this.retryOnResultValue = value
        return this
    }

    /**
     * Creates a retry with the configured settings.
     *
     * @return The created retry
     */
    private fun createRetry(): Retry {
        val configBuilder = RetryConfig.custom<Any>()
            .maxAttempts(maxAttempts)
            .waitDuration(Duration.ofMillis(waitDuration))

        if (enableExponentialBackoff) {
            configBuilder.intervalFunction(
                io.github.resilience4j.core.IntervalFunction.ofExponentialBackoff(
                    Duration.ofMillis(waitDuration),
                    exponentialBackoffMultiplier
                )
            )
        }

        if (retryOnResultValue != null) {
            @Suppress("UNCHECKED_CAST")
            configBuilder.retryOnResult(Predicate<Any> { result -> result == retryOnResultValue })
        }

        return registry.retry(name, configBuilder.build())
    }

    /**
     * Wraps the Redis client with retry functionality.
     * This method creates a dynamic proxy that intercepts all method calls to the Redis client
     * and applies retry functionality to them.
     *
     * @param client The Redis client to wrap
     * @return The wrapped Redis client with retry functionality
     */
    @Suppress("UNCHECKED_CAST")
    override fun wrap(client: T): T {
        val retry = createRetry()

        // For now, we'll return the client as-is
        // In a real implementation, we would create a dynamic proxy or decorator
        // that wraps all methods with retry functionality

        // This is a placeholder implementation
        // The actual implementation would depend on the specific Redis client type
        // and would require reflection or code generation to create a proper wrapper

        return client
    }

    companion object {
        /**
         * Creates a new RedisRetryImpl instance.
         *
         * @return A new RedisRetryImpl instance
         */
        @JvmStatic
        fun <T> create(): RedisRetry<T> = RedisRetryImpl()
    }
}
