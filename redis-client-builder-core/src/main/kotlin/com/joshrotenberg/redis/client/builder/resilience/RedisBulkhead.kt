package com.joshrotenberg.redis.client.builder.resilience

/**
 * Interface for configuring bulkhead functionality for Redis clients.
 * Bulkheads are used to limit the number of concurrent Redis operations.
 *
 * @param T The type of Redis client that will be wrapped
 */
interface RedisBulkhead<T> : RedisResilience<T> {
    /**
     * Sets the name of the bulkhead.
     *
     * @param name The name of the bulkhead
     * @return This bulkhead instance
     */
    fun name(name: String): RedisBulkhead<T>

    /**
     * Sets the maximum number of concurrent calls permitted.
     *
     * @param maxConcurrentCalls The maximum number of concurrent calls
     * @return This bulkhead instance
     */
    fun maxConcurrentCalls(maxConcurrentCalls: Int): RedisBulkhead<T>

    /**
     * Sets the maximum number of waiting calls permitted.
     *
     * @param maxWaitingCalls The maximum number of waiting calls
     * @return This bulkhead instance
     */
    fun maxWaitingCalls(maxWaitingCalls: Int): RedisBulkhead<T>

    /**
     * Sets the wait time in milliseconds for a permission to execute a Redis operation.
     *
     * @param waitTimeMs The wait time in milliseconds
     * @return This bulkhead instance
     */
    fun waitTime(waitTimeMs: Long): RedisBulkhead<T>

    /**
     * Sets whether to use a fair semaphore for the bulkhead.
     * A fair semaphore will grant permissions in the order they were requested.
     *
     * @param fairSemaphore Whether to use a fair semaphore
     * @return This bulkhead instance
     */
    fun fairSemaphore(fairSemaphore: Boolean): RedisBulkhead<T>
}