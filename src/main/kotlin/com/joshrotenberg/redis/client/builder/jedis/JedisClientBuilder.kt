package com.joshrotenberg.redis.client.builder.jedis

import com.joshrotenberg.redis.client.builder.RedisClientBuilder
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Protocol
import java.time.Duration

/**
 * Builder for Jedis client instances.
 * Provides a fluent API for configuring and building Jedis client instances.
 */
class JedisClientBuilder : RedisClientBuilder<JedisPool> {
    private var host: String = Protocol.DEFAULT_HOST
    private var port: Int = Protocol.DEFAULT_PORT
    private var password: String? = null
    private var database: Int = Protocol.DEFAULT_DATABASE
    private var connectionTimeoutMs: Int = Protocol.DEFAULT_TIMEOUT
    private var socketTimeoutMs: Int = Protocol.DEFAULT_TIMEOUT
    private var ssl: Boolean = false
    private var maxTotal: Int = JedisPoolConfig.DEFAULT_MAX_TOTAL
    private var maxIdle: Int = JedisPoolConfig.DEFAULT_MAX_IDLE
    private var minIdle: Int = JedisPoolConfig.DEFAULT_MIN_IDLE
    private var testOnBorrow: Boolean = JedisPoolConfig.DEFAULT_TEST_ON_BORROW
    private var testOnReturn: Boolean = JedisPoolConfig.DEFAULT_TEST_ON_RETURN
    private var testWhileIdle: Boolean = JedisPoolConfig.DEFAULT_TEST_WHILE_IDLE
    private var timeBetweenEvictionRunsMs: Long = JedisPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS.toMillis()
    private var blockWhenExhausted: Boolean = JedisPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED
    private var jmxEnabled: Boolean = true // Default JMX enabled value

    override fun host(host: String): JedisClientBuilder {
        this.host = host
        return this
    }

    override fun port(port: Int): JedisClientBuilder {
        this.port = port
        return this
    }

    override fun password(password: String): JedisClientBuilder {
        this.password = password
        return this
    }

    override fun database(database: Int): JedisClientBuilder {
        this.database = database
        return this
    }

    override fun connectionTimeout(timeoutMs: Int): JedisClientBuilder {
        this.connectionTimeoutMs = timeoutMs
        return this
    }

    override fun socketTimeout(timeoutMs: Int): JedisClientBuilder {
        this.socketTimeoutMs = timeoutMs
        return this
    }

    override fun ssl(useSSL: Boolean): JedisClientBuilder {
        this.ssl = useSSL
        return this
    }

    /**
     * Sets the maximum number of connections that can be allocated by the pool at a given time.
     *
     * @param maxTotal The maximum number of connections
     * @return This builder instance
     */
    fun maxTotal(maxTotal: Int): JedisClientBuilder {
        this.maxTotal = maxTotal
        return this
    }

    /**
     * Sets the maximum number of idle connections that can be maintained by the pool without being closed.
     *
     * @param maxIdle The maximum number of idle connections
     * @return This builder instance
     */
    fun maxIdle(maxIdle: Int): JedisClientBuilder {
        this.maxIdle = maxIdle
        return this
    }

    /**
     * Sets the minimum number of idle connections to maintain in the pool.
     *
     * @param minIdle The minimum number of idle connections
     * @return This builder instance
     */
    fun minIdle(minIdle: Int): JedisClientBuilder {
        this.minIdle = minIdle
        return this
    }

    /**
     * Sets whether connections should be validated before being borrowed from the pool.
     *
     * @param testOnBorrow Whether to test connections on borrow
     * @return This builder instance
     */
    fun testOnBorrow(testOnBorrow: Boolean): JedisClientBuilder {
        this.testOnBorrow = testOnBorrow
        return this
    }

    /**
     * Sets whether connections should be validated before being returned to the pool.
     *
     * @param testOnReturn Whether to test connections on return
     * @return This builder instance
     */
    fun testOnReturn(testOnReturn: Boolean): JedisClientBuilder {
        this.testOnReturn = testOnReturn
        return this
    }

    /**
     * Sets whether idle connections should be validated by the idle connection evictor.
     *
     * @param testWhileIdle Whether to test idle connections
     * @return This builder instance
     */
    fun testWhileIdle(testWhileIdle: Boolean): JedisClientBuilder {
        this.testWhileIdle = testWhileIdle
        return this
    }

    /**
     * Sets the time between runs of the idle connection evictor thread in milliseconds.
     *
     * @param timeBetweenEvictionRunsMs The time between eviction runs in milliseconds
     * @return This builder instance
     */
    fun timeBetweenEvictionRuns(timeBetweenEvictionRunsMs: Long): JedisClientBuilder {
        this.timeBetweenEvictionRunsMs = timeBetweenEvictionRunsMs
        return this
    }

    /**
     * Sets whether clients should block when the pool is exhausted.
     *
     * @param blockWhenExhausted Whether to block when the pool is exhausted
     * @return This builder instance
     */
    fun blockWhenExhausted(blockWhenExhausted: Boolean): JedisClientBuilder {
        this.blockWhenExhausted = blockWhenExhausted
        return this
    }

    /**
     * Sets whether JMX should be enabled for the pool.
     *
     * @param jmxEnabled Whether to enable JMX
     * @return This builder instance
     */
    fun jmxEnabled(jmxEnabled: Boolean): JedisClientBuilder {
        this.jmxEnabled = jmxEnabled
        return this
    }

    /**
     * Builds and returns a JedisPool instance with the configured settings.
     *
     * @return A configured JedisPool instance
     */
    override fun build(): JedisPool {
        val poolConfig =
            JedisPoolConfig().apply {
                maxTotal = this@JedisClientBuilder.maxTotal
                maxIdle = this@JedisClientBuilder.maxIdle
                minIdle = this@JedisClientBuilder.minIdle
                testOnBorrow = this@JedisClientBuilder.testOnBorrow
                testOnReturn = this@JedisClientBuilder.testOnReturn
                testWhileIdle = this@JedisClientBuilder.testWhileIdle
                timeBetweenEvictionRuns = Duration.ofMillis(this@JedisClientBuilder.timeBetweenEvictionRunsMs)
                blockWhenExhausted = this@JedisClientBuilder.blockWhenExhausted
                jmxEnabled = this@JedisClientBuilder.jmxEnabled
            }

        return JedisPool(
            poolConfig,
            host,
            port,
            connectionTimeoutMs,
            socketTimeoutMs,
            password,
            database,
            null, // clientName
            ssl,
            null, // sslSocketFactory
            null, // sslParameters
            null, // hostnameVerifier
        )
    }

    companion object {
        /**
         * Creates a new JedisClientBuilder instance.
         *
         * @return A new JedisClientBuilder instance
         */
        @JvmStatic
        fun create(): JedisClientBuilder = JedisClientBuilder()
    }
}
