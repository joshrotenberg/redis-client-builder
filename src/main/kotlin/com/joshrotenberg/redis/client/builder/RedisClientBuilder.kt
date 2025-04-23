package com.joshrotenberg.redis.client.builder

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
     * Builds and returns the Redis client instance.
     *
     * @return The configured Redis client instance
     */
    fun build(): T
}
