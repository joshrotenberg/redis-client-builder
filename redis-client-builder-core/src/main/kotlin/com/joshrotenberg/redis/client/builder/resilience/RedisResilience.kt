package com.joshrotenberg.redis.client.builder.resilience

/**
 * Interface for resilience4j integration with Redis clients.
 * This interface defines methods for configuring resilience4j features
 * and wrapping Redis clients with resilience4j functionality.
 *
 * @param T The type of Redis client that will be wrapped
 */
interface RedisResilience<T> {
    /**
     * Wraps the Redis client with resilience4j functionality.
     *
     * @param client The Redis client to wrap
     * @return The wrapped Redis client with resilience4j functionality
     */
    fun wrap(client: T): T
}

/**
 * Interface for configuring circuit breaker functionality for Redis clients.
 *
 * @param T The type of Redis client that will be wrapped
 */
interface RedisCircuitBreaker<T> : RedisResilience<T> {
    /**
     * Sets the name of the circuit breaker.
     *
     * @param name The name of the circuit breaker
     * @return This circuit breaker instance
     */
    fun name(name: String): RedisCircuitBreaker<T>

    /**
     * Sets the failure rate threshold in percentage above which the circuit breaker should trip open.
     *
     * @param threshold The failure rate threshold in percentage
     * @return This circuit breaker instance
     */
    fun failureRateThreshold(threshold: Float): RedisCircuitBreaker<T>

    /**
     * Sets the minimum number of calls required before the circuit breaker can calculate the error rate.
     *
     * @param calls The minimum number of calls
     * @return This circuit breaker instance
     */
    fun minimumNumberOfCalls(calls: Int): RedisCircuitBreaker<T>

    /**
     * Sets the wait duration in milliseconds after which the circuit breaker should transition from open to half-open.
     *
     * @param durationMs The wait duration in milliseconds
     * @return This circuit breaker instance
     */
    fun waitDurationInOpenState(durationMs: Long): RedisCircuitBreaker<T>

    /**
     * Sets the number of permitted calls when the circuit breaker is half open.
     *
     * @param calls The number of permitted calls
     * @return This circuit breaker instance
     */
    fun permittedNumberOfCallsInHalfOpenState(calls: Int): RedisCircuitBreaker<T>

    /**
     * Sets whether to automatically transition from open to half-open state without calls.
     *
     * @param autoTransition Whether to automatically transition
     * @return This circuit breaker instance
     */
    fun automaticTransitionFromOpenToHalfOpenEnabled(autoTransition: Boolean): RedisCircuitBreaker<T>
}
