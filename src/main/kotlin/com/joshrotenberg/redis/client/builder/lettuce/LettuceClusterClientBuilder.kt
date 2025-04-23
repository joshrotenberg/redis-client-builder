package com.joshrotenberg.redis.client.builder.lettuce

import com.joshrotenberg.redis.client.builder.RedisClusterClientBuilder
import io.lettuce.core.ClientOptions
import io.lettuce.core.RedisURI
import io.lettuce.core.SocketOptions
import io.lettuce.core.TimeoutOptions
import io.lettuce.core.cluster.ClusterClientOptions
import io.lettuce.core.cluster.RedisClusterClient
import java.time.Duration
import java.util.ArrayList

/**
 * Builder for Lettuce cluster client instances.
 * Provides a fluent API for configuring and building Lettuce cluster client instances.
 */
class LettuceClusterClientBuilder : RedisClusterClientBuilder<RedisClusterClient> {
    private val nodes = ArrayList<RedisURI>()
    private var password: String? = null
    private var connectionTimeoutMs: Int = 60000
    private var socketTimeoutMs: Int = 60000
    private var ssl: Boolean = false
    private var maxRedirections: Int = 5
    private var autoReconnect: Boolean = true
    private var requestQueueSize: Int = 2147483647 // Integer.MAX_VALUE
    private var publishOnScheduler: Boolean = false
    private var disconnectedBehavior: ClientOptions.DisconnectedBehavior = ClientOptions.DisconnectedBehavior.DEFAULT

    override fun addNode(host: String, port: Int): LettuceClusterClientBuilder {
        val redisURI = RedisURI.builder()
            .withHost(host)
            .withPort(port)
            .withTimeout(Duration.ofMillis(connectionTimeoutMs.toLong()))
            .apply {
                if (password != null) {
                    withPassword(password!!.toCharArray())
                }
                if (ssl) {
                    withSsl(ssl)
                }
            }.build()
        nodes.add(redisURI)
        return this
    }

    override fun password(password: String): LettuceClusterClientBuilder {
        this.password = password
        // Update password for all existing nodes
        nodes.forEach { uri ->
            uri.password = password.toCharArray()
        }
        return this
    }

    override fun connectionTimeout(timeoutMs: Int): LettuceClusterClientBuilder {
        this.connectionTimeoutMs = timeoutMs
        // Update timeout for all existing nodes
        nodes.forEach { uri ->
            uri.timeout = Duration.ofMillis(timeoutMs.toLong())
        }
        return this
    }

    override fun socketTimeout(timeoutMs: Int): LettuceClusterClientBuilder {
        this.socketTimeoutMs = timeoutMs
        return this
    }

    override fun ssl(useSSL: Boolean): LettuceClusterClientBuilder {
        this.ssl = useSSL
        // Update SSL for all existing nodes
        nodes.forEach { uri ->
            uri.isSsl = useSSL
        }
        return this
    }

    override fun maxRedirections(maxRedirections: Int): LettuceClusterClientBuilder {
        this.maxRedirections = maxRedirections
        return this
    }

    /**
     * Sets whether the client should automatically reconnect.
     *
     * @param autoReconnect Whether to automatically reconnect
     * @return This builder instance
     */
    fun autoReconnect(autoReconnect: Boolean): LettuceClusterClientBuilder {
        this.autoReconnect = autoReconnect
        return this
    }

    /**
     * Sets the request queue size.
     *
     * @param requestQueueSize The request queue size
     * @return This builder instance
     */
    fun requestQueueSize(requestQueueSize: Int): LettuceClusterClientBuilder {
        this.requestQueueSize = requestQueueSize
        return this
    }

    /**
     * Sets whether to publish on the scheduler.
     *
     * @param publishOnScheduler Whether to publish on the scheduler
     * @return This builder instance
     */
    fun publishOnScheduler(publishOnScheduler: Boolean): LettuceClusterClientBuilder {
        this.publishOnScheduler = publishOnScheduler
        return this
    }

    /**
     * Sets the disconnected behavior.
     *
     * @param disconnectedBehavior The disconnected behavior
     * @return This builder instance
     */
    fun disconnectedBehavior(disconnectedBehavior: ClientOptions.DisconnectedBehavior): LettuceClusterClientBuilder {
        this.disconnectedBehavior = disconnectedBehavior
        return this
    }

    /**
     * Builds and returns a RedisClusterClient instance with the configured settings.
     *
     * @return A configured RedisClusterClient instance
     */
    override fun build(): RedisClusterClient {
        if (nodes.isEmpty()) {
            throw IllegalStateException("At least one cluster node must be added")
        }

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
            ClusterClientOptions
                .builder()
                .autoReconnect(autoReconnect)
                .requestQueueSize(requestQueueSize)
                .publishOnScheduler(publishOnScheduler)
                .disconnectedBehavior(disconnectedBehavior)
                .socketOptions(socketOptions)
                .timeoutOptions(timeoutOptions)
                .maxRedirects(maxRedirections)
                .build()

        val client = RedisClusterClient.create(nodes)
        client.setOptions(clientOptions)

        return client
    }

    companion object {
        /**
         * Creates a new LettuceClusterClientBuilder instance.
         *
         * @return A new LettuceClusterClientBuilder instance
         */
        @JvmStatic
        fun create(): LettuceClusterClientBuilder = LettuceClusterClientBuilder()
    }
}