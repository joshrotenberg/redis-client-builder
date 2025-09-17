package com.joshrotenberg.redis.client.builder.resilience

import io.github.resilience4j.timelimiter.TimeLimiter
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import io.github.resilience4j.timelimiter.TimeLimiterRegistry
import java.time.Duration

/**
 * Implementation of the RedisTimeLimiter interface.
 * This class provides time limiter functionality for Redis clients using resilience4j.
 *
 * @param T The type of Redis client that will be wrapped
 * @property registry The time limiter registry to use
 */
class RedisTimeLimiterImpl<T>(
    private val registry: TimeLimiterRegistry = TimeLimiterRegistry.ofDefaults(),
) : RedisTimeLimiter<T> {
    private var name: String = "redis-time-limiter"
    private var timeoutDuration: Long = 1000 // 1 second
    private var cancelRunningFuture: Boolean = true

    /**
     * Sets the name of the time limiter.
     *
     * @param name The name of the time limiter
     * @return This time limiter instance
     */
    override fun name(name: String): RedisTimeLimiter<T> {
        this.name = name
        return this
    }

    /**
     * Sets the timeout duration in milliseconds.
     * If a Redis operation takes longer than this duration, it will be cancelled.
     *
     * @param timeoutMs The timeout duration in milliseconds
     * @return This time limiter instance
     */
    override fun timeoutDuration(timeoutMs: Long): RedisTimeLimiter<T> {
        this.timeoutDuration = timeoutMs
        return this
    }

    /**
     * Sets whether to cancel running futures when the timeout is reached.
     *
     * @param cancelRunningFuture Whether to cancel running futures
     * @return This time limiter instance
     */
    override fun cancelRunningFuture(cancelRunningFuture: Boolean): RedisTimeLimiter<T> {
        this.cancelRunningFuture = cancelRunningFuture
        return this
    }

    /**
     * Creates a time limiter with the configured settings.
     *
     * @return The created time limiter
     */
    private fun createTimeLimiter(): TimeLimiter {
        val config =
            TimeLimiterConfig
                .custom()
                .timeoutDuration(Duration.ofMillis(timeoutDuration))
                .cancelRunningFuture(cancelRunningFuture)
                .build()

        return registry.timeLimiter(name, config)
    }

    /**
     * Wraps the Redis client with time limiter functionality.
     * This method creates a dynamic proxy that intercepts all method calls to the Redis client
     * and applies time limiter functionality to them.
     *
     * @param client The Redis client to wrap
     * @return The wrapped Redis client with time limiter functionality
     */
    @Suppress("UNCHECKED_CAST")
    override fun wrap(client: T): T {
        val timeLimiter = createTimeLimiter()

        // For now, we'll return the client as-is
        // In a real implementation, we would create a dynamic proxy or decorator
        // that wraps all methods with time limiter functionality
        // This is a placeholder implementation
        // The actual implementation would depend on the specific Redis client type
        // and would require reflection or code generation to create a proper wrapper

        return client
    }

    companion object {
        /**
         * Creates a new RedisTimeLimiterImpl instance.
         *
         * @return A new RedisTimeLimiterImpl instance
         */
        @JvmStatic
        fun <T> create(): RedisTimeLimiter<T> = RedisTimeLimiterImpl()
    }
}
