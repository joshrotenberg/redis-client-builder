package com.joshrotenberg.redis.client.builder

/**
 * Common interface for all Redis sentinel client builders.
 * This interface defines the common methods and properties that all Redis sentinel client builders should support,
 * providing a consistent API across different Redis client implementations.
 *
 * @param T The type of Redis client that will be built using sentinel configuration
 */
interface RedisSentinelClientBuilder<T> {
    /**
     * Adds a Redis sentinel node to the configuration.
     *
     * @param host The Redis sentinel host
     * @param port The Redis sentinel port
     * @return This builder instance
     */
    fun addSentinel(host: String, port: Int): RedisSentinelClientBuilder<T>

    /**
     * Sets the master name that the sentinels are monitoring.
     *
     * @param masterName The name of the Redis master
     * @return This builder instance
     */
    fun masterName(masterName: String): RedisSentinelClientBuilder<T>

    /**
     * Sets the password for authentication with the Redis sentinel.
     *
     * @param password The Redis sentinel password
     * @return This builder instance
     */
    fun password(password: String): RedisSentinelClientBuilder<T>

    /**
     * Sets the Redis database index.
     *
     * @param database The Redis database index
     * @return This builder instance
     */
    fun database(database: Int): RedisSentinelClientBuilder<T>

    /**
     * Sets the connection timeout in milliseconds.
     *
     * @param timeoutMs The connection timeout in milliseconds
     * @return This builder instance
     */
    fun connectionTimeout(timeoutMs: Int): RedisSentinelClientBuilder<T>

    /**
     * Sets the socket timeout in milliseconds.
     *
     * @param timeoutMs The socket timeout in milliseconds
     * @return This builder instance
     */
    fun socketTimeout(timeoutMs: Int): RedisSentinelClientBuilder<T>

    /**
     * Enables SSL/TLS for the connection.
     *
     * @param useSSL Whether to use SSL/TLS
     * @return This builder instance
     */
    fun ssl(useSSL: Boolean): RedisSentinelClientBuilder<T>

    /**
     * Builds and returns the Redis client instance configured with sentinel support.
     *
     * @return The configured Redis client instance
     */
    fun build(): T
}