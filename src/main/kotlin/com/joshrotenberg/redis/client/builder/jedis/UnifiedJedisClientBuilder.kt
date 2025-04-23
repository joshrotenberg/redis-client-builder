package com.joshrotenberg.redis.client.builder.jedis

import com.joshrotenberg.redis.client.builder.RedisClientBuilder
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisClientConfig
import redis.clients.jedis.Protocol
import redis.clients.jedis.UnifiedJedis
import java.net.URI
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLParameters
import javax.net.ssl.SSLSocketFactory

/**
 * Builder for UnifiedJedis client instances.
 * Provides a fluent API for configuring and building UnifiedJedis client instances.
 */
class UnifiedJedisClientBuilder : RedisClientBuilder<UnifiedJedis> {
    // Connection parameters
    private var host: String = Protocol.DEFAULT_HOST
    private var port: Int = Protocol.DEFAULT_PORT
    private var connectionTimeoutMs: Int = Protocol.DEFAULT_TIMEOUT
    private var socketTimeoutMs: Int = Protocol.DEFAULT_TIMEOUT
    private var database: Int = Protocol.DEFAULT_DATABASE
    private var clientName: String? = null
    private var password: String? = null
    private var user: String? = null
    private var ssl: Boolean = false
    private var sslSocketFactory: SSLSocketFactory? = null
    private var sslParameters: SSLParameters? = null
    private var hostnameVerifier: HostnameVerifier? = null

    // URI for connection
    private var uri: URI? = null

    // Connection mode
    private enum class ConnectionMode {
        DIRECT, URI
    }

    private var connectionMode: ConnectionMode = ConnectionMode.DIRECT

    override fun host(host: String): UnifiedJedisClientBuilder {
        this.host = host
        return this
    }

    override fun port(port: Int): UnifiedJedisClientBuilder {
        this.port = port
        return this
    }

    override fun password(password: String): UnifiedJedisClientBuilder {
        this.password = password
        return this
    }

    override fun database(database: Int): UnifiedJedisClientBuilder {
        this.database = database
        return this
    }

    override fun connectionTimeout(timeoutMs: Int): UnifiedJedisClientBuilder {
        this.connectionTimeoutMs = timeoutMs
        return this
    }

    override fun socketTimeout(timeoutMs: Int): UnifiedJedisClientBuilder {
        this.socketTimeoutMs = timeoutMs
        return this
    }

    override fun ssl(useSSL: Boolean): UnifiedJedisClientBuilder {
        this.ssl = useSSL
        return this
    }

    /**
     * Sets the client name.
     *
     * @param clientName The client name
     * @return This builder instance
     */
    fun clientName(clientName: String): UnifiedJedisClientBuilder {
        this.clientName = clientName
        return this
    }

    /**
     * Sets the username for ACL authentication.
     *
     * @param user The username
     * @return This builder instance
     */
    fun user(user: String): UnifiedJedisClientBuilder {
        this.user = user
        return this
    }

    /**
     * Sets the SSL socket factory.
     *
     * @param sslSocketFactory The SSL socket factory
     * @return This builder instance
     */
    fun sslSocketFactory(sslSocketFactory: SSLSocketFactory): UnifiedJedisClientBuilder {
        this.sslSocketFactory = sslSocketFactory
        return this
    }

    /**
     * Sets the SSL parameters.
     *
     * @param sslParameters The SSL parameters
     * @return This builder instance
     */
    fun sslParameters(sslParameters: SSLParameters): UnifiedJedisClientBuilder {
        this.sslParameters = sslParameters
        return this
    }

    /**
     * Sets the hostname verifier.
     *
     * @param hostnameVerifier The hostname verifier
     * @return This builder instance
     */
    fun hostnameVerifier(hostnameVerifier: HostnameVerifier): UnifiedJedisClientBuilder {
        this.hostnameVerifier = hostnameVerifier
        return this
    }

    /**
     * Sets the URI for the Redis connection.
     * Using this method will override any previously set connection parameters.
     *
     * @param uri The Redis URI
     * @return This builder instance
     */
    fun uri(uri: URI): UnifiedJedisClientBuilder {
        this.uri = uri
        this.connectionMode = ConnectionMode.URI
        return this
    }

    /**
     * Sets the URI for the Redis connection.
     * Using this method will override any previously set connection parameters.
     *
     * @param uri The Redis URI as a string
     * @return This builder instance
     */
    fun uri(uri: String): UnifiedJedisClientBuilder {
        return uri(URI.create(uri))
    }

    /**
     * Builds and returns a UnifiedJedis instance with the configured settings.
     *
     * @return A configured UnifiedJedis instance
     */
    override fun build(): UnifiedJedis {
        return when (connectionMode) {
            ConnectionMode.URI -> buildFromUri()
            ConnectionMode.DIRECT -> buildDirectConnection()
        }
    }

    private fun buildFromUri(): UnifiedJedis {
        return uri?.let { UnifiedJedis(it) } ?: throw IllegalStateException("URI is not set")
    }

    private fun buildDirectConnection(): UnifiedJedis {
        val config = createJedisClientConfig()
        return UnifiedJedis(HostAndPort(host, port), config)
    }

    private fun createJedisClientConfig(): JedisClientConfig {
        return DefaultJedisClientConfig.builder()
            .connectionTimeoutMillis(connectionTimeoutMs)
            .socketTimeoutMillis(socketTimeoutMs)
            .database(database)
            .clientName(clientName)
            .password(password)
            .user(user)
            .ssl(ssl)
            .sslSocketFactory(sslSocketFactory)
            .sslParameters(sslParameters)
            .hostnameVerifier(hostnameVerifier)
            .build()
    }

    companion object {
        /**
         * Creates a new UnifiedJedisClientBuilder instance.
         *
         * @return A new UnifiedJedisClientBuilder instance
         */
        @JvmStatic
        fun create(): UnifiedJedisClientBuilder = UnifiedJedisClientBuilder()
    }
}
