package com.joshrotenberg.redis.client.builder.lettuce

import com.joshrotenberg.redis.client.builder.RedisClientBuilder
import io.lettuce.core.ClientOptions
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.SocketOptions
import io.lettuce.core.TimeoutOptions
import java.time.Duration

/**
 * Builder for Lettuce client instances.
 * Provides a fluent API for configuring and building Lettuce client instances.
 */
class LettuceClientBuilder : RedisClientBuilder<RedisClient> {
    private var host: String = "localhost"
    private var port: Int = 6379
    private var password: String? = null
    private var database: Int = 0
    private var connectionTimeoutMs: Int = 60000
    private var socketTimeoutMs: Int = 60000
    private var ssl: Boolean = false
    private var autoReconnect: Boolean = true
    private var requestQueueSize: Int = 2147483647 // Integer.MAX_VALUE
    private var publishOnScheduler: Boolean = false
    private var disconnectedBehavior: ClientOptions.DisconnectedBehavior = ClientOptions.DisconnectedBehavior.DEFAULT

    override fun host(host: String): LettuceClientBuilder {
        this.host = host
        return this
    }

    override fun port(port: Int): LettuceClientBuilder {
        this.port = port
        return this
    }

    override fun password(password: String): LettuceClientBuilder {
        this.password = password
        return this
    }

    override fun database(database: Int): LettuceClientBuilder {
        this.database = database
        return this
    }

    override fun connectionTimeout(timeoutMs: Int): LettuceClientBuilder {
        this.connectionTimeoutMs = timeoutMs
        return this
    }

    override fun socketTimeout(timeoutMs: Int): LettuceClientBuilder {
        this.socketTimeoutMs = timeoutMs
        return this
    }

    override fun ssl(useSSL: Boolean): LettuceClientBuilder {
        this.ssl = useSSL
        return this
    }

    /**
     * Sets whether the client should automatically reconnect.
     *
     * @param autoReconnect Whether to automatically reconnect
     * @return This builder instance
     */
    fun autoReconnect(autoReconnect: Boolean): LettuceClientBuilder {
        this.autoReconnect = autoReconnect
        return this
    }

    /**
     * Sets the request queue size.
     *
     * @param requestQueueSize The request queue size
     * @return This builder instance
     */
    fun requestQueueSize(requestQueueSize: Int): LettuceClientBuilder {
        this.requestQueueSize = requestQueueSize
        return this
    }

    /**
     * Sets whether to publish on the scheduler.
     *
     * @param publishOnScheduler Whether to publish on the scheduler
     * @return This builder instance
     */
    fun publishOnScheduler(publishOnScheduler: Boolean): LettuceClientBuilder {
        this.publishOnScheduler = publishOnScheduler
        return this
    }

    /**
     * Sets the disconnected behavior.
     *
     * @param disconnectedBehavior The disconnected behavior
     * @return This builder instance
     */
    fun disconnectedBehavior(disconnectedBehavior: ClientOptions.DisconnectedBehavior): LettuceClientBuilder {
        this.disconnectedBehavior = disconnectedBehavior
        return this
    }

    /**
     * Builds and returns a RedisClient instance with the configured settings.
     *
     * @return A configured RedisClient instance
     */
    override fun build(): RedisClient {
        val redisURI =
            RedisURI
                .builder()
                .withHost(host)
                .withPort(port)
                .withDatabase(database)
                .withTimeout(Duration.ofMillis(connectionTimeoutMs.toLong()))
                .apply {
                    if (password != null) {
                        withPassword(password!!.toCharArray())
                    }
                    if (ssl) {
                        withSsl(ssl)
                    }
                }.build()

        val socketOptions =
            SocketOptions
                .builder()
                .connectTimeout(Duration.ofMillis(connectionTimeoutMs.toLong()))
                .apply {
                    if (socketTimeoutMs > 0) {
                        keepAlive(true)
                        tcpNoDelay(true)
                    }
                }.build()

        val timeoutOptions =
            TimeoutOptions
                .builder()
                .fixedTimeout(Duration.ofMillis(socketTimeoutMs.toLong()))
                .build()

        val clientOptions =
            ClientOptions
                .builder()
                .autoReconnect(autoReconnect)
                .requestQueueSize(requestQueueSize)
                .publishOnScheduler(publishOnScheduler)
                .disconnectedBehavior(disconnectedBehavior)
                .socketOptions(socketOptions)
                .timeoutOptions(timeoutOptions)
                .build()

        val client = RedisClient.create(redisURI)
        client.options = clientOptions

        return client
    }

    companion object {
        /**
         * Creates a new LettuceClientBuilder instance.
         *
         * @return A new LettuceClientBuilder instance
         */
        @JvmStatic
        fun create(): LettuceClientBuilder = LettuceClientBuilder()
    }
}
