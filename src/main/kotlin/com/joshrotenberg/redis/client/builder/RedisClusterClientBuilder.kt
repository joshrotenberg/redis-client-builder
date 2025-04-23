package com.joshrotenberg.redis.client.builder

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
     * Builds and returns the Redis cluster client instance.
     *
     * @return The configured Redis cluster client instance
     */
    fun build(): T
}