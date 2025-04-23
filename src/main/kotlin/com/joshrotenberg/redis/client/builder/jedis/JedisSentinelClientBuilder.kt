package com.joshrotenberg.redis.client.builder.jedis

import com.joshrotenberg.redis.client.builder.RedisSentinelClientBuilder
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisSentinelPool
import redis.clients.jedis.Protocol
import java.time.Duration
import java.util.HashSet

/**
 * Builder for Jedis client instances with Sentinel support.
 * Provides a fluent API for configuring and building Jedis client instances that connect through Redis Sentinel.
 */
class JedisSentinelClientBuilder : RedisSentinelClientBuilder<JedisSentinelPool> {
    private val sentinels = HashSet<String>()
    private var masterName: String = ""
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
    private var clientName: String? = null

    override fun addSentinel(host: String, port: Int): JedisSentinelClientBuilder {
        sentinels.add("$host:$port")
        return this
    }

    override fun masterName(masterName: String): JedisSentinelClientBuilder {
        this.masterName = masterName
        return this
    }

    override fun password(password: String): JedisSentinelClientBuilder {
        this.password = password
        return this
    }

    override fun database(database: Int): JedisSentinelClientBuilder {
        this.database = database
        return this
    }

    override fun connectionTimeout(timeoutMs: Int): JedisSentinelClientBuilder {
        this.connectionTimeoutMs = timeoutMs
        return this
    }

    override fun socketTimeout(timeoutMs: Int): JedisSentinelClientBuilder {
        this.socketTimeoutMs = timeoutMs
        return this
    }

    override fun ssl(useSSL: Boolean): JedisSentinelClientBuilder {
        this.ssl = useSSL
        return this
    }

    /**
     * Sets the client name.
     *
     * @param clientName The client name
     * @return This builder instance
     */
    fun clientName(clientName: String): JedisSentinelClientBuilder {
        this.clientName = clientName
        return this
    }

    /**
     * Sets the maximum number of connections that can be allocated by the pool at a given time.
     *
     * @param maxTotal The maximum number of connections
     * @return This builder instance
     */
    fun maxTotal(maxTotal: Int): JedisSentinelClientBuilder {
        this.maxTotal = maxTotal
        return this
    }

    /**
     * Sets the maximum number of idle connections that can be maintained by the pool without being closed.
     *
     * @param maxIdle The maximum number of idle connections
     * @return This builder instance
     */
    fun maxIdle(maxIdle: Int): JedisSentinelClientBuilder {
        this.maxIdle = maxIdle
        return this
    }

    /**
     * Sets the minimum number of idle connections to maintain in the pool.
     *
     * @param minIdle The minimum number of idle connections
     * @return This builder instance
     */
    fun minIdle(minIdle: Int): JedisSentinelClientBuilder {
        this.minIdle = minIdle
        return this
    }

    /**
     * Sets whether connections should be validated before being borrowed from the pool.
     *
     * @param testOnBorrow Whether to test connections on borrow
     * @return This builder instance
     */
    fun testOnBorrow(testOnBorrow: Boolean): JedisSentinelClientBuilder {
        this.testOnBorrow = testOnBorrow
        return this
    }

    /**
     * Sets whether connections should be validated before being returned to the pool.
     *
     * @param testOnReturn Whether to test connections on return
     * @return This builder instance
     */
    fun testOnReturn(testOnReturn: Boolean): JedisSentinelClientBuilder {
        this.testOnReturn = testOnReturn
        return this
    }

    /**
     * Sets whether idle connections should be validated by the idle connection evictor.
     *
     * @param testWhileIdle Whether to test idle connections
     * @return This builder instance
     */
    fun testWhileIdle(testWhileIdle: Boolean): JedisSentinelClientBuilder {
        this.testWhileIdle = testWhileIdle
        return this
    }

    /**
     * Sets the time between runs of the idle connection evictor thread in milliseconds.
     *
     * @param timeBetweenEvictionRunsMs The time between eviction runs in milliseconds
     * @return This builder instance
     */
    fun timeBetweenEvictionRuns(timeBetweenEvictionRunsMs: Long): JedisSentinelClientBuilder {
        this.timeBetweenEvictionRunsMs = timeBetweenEvictionRunsMs
        return this
    }

    /**
     * Sets whether clients should block when the pool is exhausted.
     *
     * @param blockWhenExhausted Whether to block when the pool is exhausted
     * @return This builder instance
     */
    fun blockWhenExhausted(blockWhenExhausted: Boolean): JedisSentinelClientBuilder {
        this.blockWhenExhausted = blockWhenExhausted
        return this
    }

    /**
     * Sets whether JMX should be enabled for the pool.
     *
     * @param jmxEnabled Whether to enable JMX
     * @return This builder instance
     */
    fun jmxEnabled(jmxEnabled: Boolean): JedisSentinelClientBuilder {
        this.jmxEnabled = jmxEnabled
        return this
    }

    /**
     * Builds and returns a JedisSentinelPool instance with the configured settings.
     *
     * @return A configured JedisSentinelPool instance
     */
    override fun build(): JedisSentinelPool {
        if (sentinels.isEmpty()) {
            throw IllegalStateException("At least one sentinel node must be added")
        }

        if (masterName.isEmpty()) {
            throw IllegalStateException("Master name must be set")
        }

        val poolConfig =
            JedisPoolConfig().apply {
                maxTotal = this@JedisSentinelClientBuilder.maxTotal
                maxIdle = this@JedisSentinelClientBuilder.maxIdle
                minIdle = this@JedisSentinelClientBuilder.minIdle
                testOnBorrow = this@JedisSentinelClientBuilder.testOnBorrow
                testOnReturn = this@JedisSentinelClientBuilder.testOnReturn
                testWhileIdle = this@JedisSentinelClientBuilder.testWhileIdle
                timeBetweenEvictionRuns = Duration.ofMillis(this@JedisSentinelClientBuilder.timeBetweenEvictionRunsMs)
                blockWhenExhausted = this@JedisSentinelClientBuilder.blockWhenExhausted
                jmxEnabled = this@JedisSentinelClientBuilder.jmxEnabled
            }

        return JedisSentinelPool(
            masterName,
            sentinels,
            poolConfig,
            connectionTimeoutMs,
            socketTimeoutMs,
            password,
            database,
            clientName
        )
    }

    companion object {
        /**
         * Creates a new JedisSentinelClientBuilder instance.
         *
         * @return A new JedisSentinelClientBuilder instance
         */
        @JvmStatic
        fun create(): JedisSentinelClientBuilder = JedisSentinelClientBuilder()
    }
}
