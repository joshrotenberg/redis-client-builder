package com.joshrotenberg.redis.client.builder.jedis

import com.joshrotenberg.redis.client.builder.RedisClusterClientBuilder
import redis.clients.jedis.ConnectionPoolConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisCluster
import java.time.Duration
import java.util.HashSet

/**
 * Builder for Jedis cluster client instances.
 * Provides a fluent API for configuring and building Jedis cluster client instances.
 */
class JedisClusterClientBuilder : RedisClusterClientBuilder<JedisCluster> {
    private val nodes = HashSet<HostAndPort>()
    private var password: String? = null
    private var clientName: String? = null
    private var connectionTimeoutMs: Int = 2000
    private var socketTimeoutMs: Int = 2000
    private var ssl: Boolean = false
    private var maxRedirections: Int = 5
    private var maxTotal: Int = 8
    private var maxIdle: Int = 8
    private var minIdle: Int = 0
    private var testOnBorrow: Boolean = false
    private var testOnReturn: Boolean = false
    private var testWhileIdle: Boolean = false
    private var timeBetweenEvictionRunsMs: Long = 30000
    private var blockWhenExhausted: Boolean = true
    private var jmxEnabled: Boolean = true

    override fun addNode(host: String, port: Int): JedisClusterClientBuilder {
        nodes.add(HostAndPort(host, port))
        return this
    }

    override fun password(password: String): JedisClusterClientBuilder {
        this.password = password
        return this
    }

    override fun connectionTimeout(timeoutMs: Int): JedisClusterClientBuilder {
        this.connectionTimeoutMs = timeoutMs
        return this
    }

    override fun socketTimeout(timeoutMs: Int): JedisClusterClientBuilder {
        this.socketTimeoutMs = timeoutMs
        return this
    }

    override fun ssl(useSSL: Boolean): JedisClusterClientBuilder {
        this.ssl = useSSL
        return this
    }

    override fun maxRedirections(maxRedirections: Int): JedisClusterClientBuilder {
        this.maxRedirections = maxRedirections
        return this
    }

    /**
     * Sets the maximum number of connections that can be allocated by the pool at a given time.
     *
     * @param maxTotal The maximum number of connections
     * @return This builder instance
     */
    fun maxTotal(maxTotal: Int): JedisClusterClientBuilder {
        this.maxTotal = maxTotal
        return this
    }

    /**
     * Sets the maximum number of idle connections that can be maintained by the pool without being closed.
     *
     * @param maxIdle The maximum number of idle connections
     * @return This builder instance
     */
    fun maxIdle(maxIdle: Int): JedisClusterClientBuilder {
        this.maxIdle = maxIdle
        return this
    }

    /**
     * Sets the minimum number of idle connections to maintain in the pool.
     *
     * @param minIdle The minimum number of idle connections
     * @return This builder instance
     */
    fun minIdle(minIdle: Int): JedisClusterClientBuilder {
        this.minIdle = minIdle
        return this
    }

    /**
     * Sets whether connections should be validated before being borrowed from the pool.
     *
     * @param testOnBorrow Whether to test connections on borrow
     * @return This builder instance
     */
    fun testOnBorrow(testOnBorrow: Boolean): JedisClusterClientBuilder {
        this.testOnBorrow = testOnBorrow
        return this
    }

    /**
     * Sets whether connections should be validated before being returned to the pool.
     *
     * @param testOnReturn Whether to test connections on return
     * @return This builder instance
     */
    fun testOnReturn(testOnReturn: Boolean): JedisClusterClientBuilder {
        this.testOnReturn = testOnReturn
        return this
    }

    /**
     * Sets whether idle connections should be validated by the idle connection evictor.
     *
     * @param testWhileIdle Whether to test idle connections
     * @return This builder instance
     */
    fun testWhileIdle(testWhileIdle: Boolean): JedisClusterClientBuilder {
        this.testWhileIdle = testWhileIdle
        return this
    }

    /**
     * Sets the time between runs of the idle connection evictor thread in milliseconds.
     *
     * @param timeBetweenEvictionRunsMs The time between eviction runs in milliseconds
     * @return This builder instance
     */
    fun timeBetweenEvictionRuns(timeBetweenEvictionRunsMs: Long): JedisClusterClientBuilder {
        this.timeBetweenEvictionRunsMs = timeBetweenEvictionRunsMs
        return this
    }

    /**
     * Sets whether clients should block when the pool is exhausted.
     *
     * @param blockWhenExhausted Whether to block when the pool is exhausted
     * @return This builder instance
     */
    fun blockWhenExhausted(blockWhenExhausted: Boolean): JedisClusterClientBuilder {
        this.blockWhenExhausted = blockWhenExhausted
        return this
    }

    /**
     * Sets whether JMX should be enabled for the pool.
     *
     * @param jmxEnabled Whether to enable JMX
     * @return This builder instance
     */
    fun jmxEnabled(jmxEnabled: Boolean): JedisClusterClientBuilder {
        this.jmxEnabled = jmxEnabled
        return this
    }

    /**
     * Sets the client name.
     *
     * @param clientName The client name
     * @return This builder instance
     */
    fun clientName(clientName: String): JedisClusterClientBuilder {
        this.clientName = clientName
        return this
    }

    /**
     * Builds and returns a JedisCluster instance with the configured settings.
     *
     * @return A configured JedisCluster instance
     */
    override fun build(): JedisCluster {
        if (nodes.isEmpty()) {
            throw IllegalStateException("At least one cluster node must be added")
        }

        val poolConfig =
            ConnectionPoolConfig().apply {
                maxTotal = this@JedisClusterClientBuilder.maxTotal
                maxIdle = this@JedisClusterClientBuilder.maxIdle
                minIdle = this@JedisClusterClientBuilder.minIdle
                testOnBorrow = this@JedisClusterClientBuilder.testOnBorrow
                testOnReturn = this@JedisClusterClientBuilder.testOnReturn
                testWhileIdle = this@JedisClusterClientBuilder.testWhileIdle
                timeBetweenEvictionRuns = Duration.ofMillis(this@JedisClusterClientBuilder.timeBetweenEvictionRunsMs)
                blockWhenExhausted = this@JedisClusterClientBuilder.blockWhenExhausted
                jmxEnabled = this@JedisClusterClientBuilder.jmxEnabled
            }

        // If there's only one node, use the single node constructor
        if (nodes.size == 1) {
            return JedisCluster(
                nodes.first(),
                connectionTimeoutMs,
                socketTimeoutMs,
                maxRedirections,
                password ?: "",
                clientName ?: "",
                poolConfig,
                ssl
            )
        } else {
            // For multiple nodes, use the set of nodes constructor
            return JedisCluster(
                nodes,
                connectionTimeoutMs,
                socketTimeoutMs,
                maxRedirections,
                password ?: "",
                clientName ?: "",
                poolConfig,
                ssl
            )
        }
    }

    companion object {
        /**
         * Creates a new JedisClusterClientBuilder instance.
         *
         * @return A new JedisClusterClientBuilder instance
         */
        @JvmStatic
        fun create(): JedisClusterClientBuilder = JedisClusterClientBuilder()
    }
}
