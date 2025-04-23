package com.joshrotenberg.redis.client.builder

import com.joshrotenberg.redis.client.builder.jedis.JedisClientBuilder
import com.joshrotenberg.redis.client.builder.jedis.JedisClusterClientBuilder
import com.joshrotenberg.redis.client.builder.jedis.JedisPooledClientBuilder
import com.joshrotenberg.redis.client.builder.jedis.JedisSentinelClientBuilder
import com.joshrotenberg.redis.client.builder.jedis.UnifiedJedisClientBuilder
import com.joshrotenberg.redis.client.builder.lettuce.LettuceClientBuilder
import com.joshrotenberg.redis.client.builder.lettuce.LettuceClusterClientBuilder
import io.lettuce.core.RedisClient
import io.lettuce.core.cluster.RedisClusterClient
import redis.clients.jedis.JedisCluster
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPooled
import redis.clients.jedis.JedisSentinelPool
import redis.clients.jedis.UnifiedJedis

/**
 * Factory class for creating Redis client builder instances.
 * This class provides static methods for creating instances of each Redis client builder,
 * making it easier for users to instantiate the appropriate builder without having to know
 * the specific implementation details.
 */
object RedisClientBuilderFactory {
    /**
     * Creates a new JedisClientBuilder instance.
     *
     * @return A new JedisClientBuilder instance
     */
    @JvmStatic
    fun jedis(): JedisClientBuilder = JedisClientBuilder.create()

    /**
     * Creates a new UnifiedJedisClientBuilder instance.
     *
     * @return A new UnifiedJedisClientBuilder instance
     */
    @JvmStatic
    fun unifiedJedis(): UnifiedJedisClientBuilder = UnifiedJedisClientBuilder.create()

    /**
     * Creates a new JedisPooledClientBuilder instance.
     *
     * @return A new JedisPooledClientBuilder instance
     */
    @JvmStatic
    fun jedisPooled(): JedisPooledClientBuilder = JedisPooledClientBuilder.create()

    /**
     * Creates a new LettuceClientBuilder instance.
     *
     * @return A new LettuceClientBuilder instance
     */
    @JvmStatic
    fun lettuce(): LettuceClientBuilder = LettuceClientBuilder.create()

    /**
     * Creates a new JedisClusterClientBuilder instance.
     *
     * @return A new JedisClusterClientBuilder instance
     */
    @JvmStatic
    fun jedisCluster(): JedisClusterClientBuilder = JedisClusterClientBuilder.create()

    /**
     * Creates a new LettuceClusterClientBuilder instance.
     *
     * @return A new LettuceClusterClientBuilder instance
     */
    @JvmStatic
    fun lettuceCluster(): LettuceClusterClientBuilder = LettuceClusterClientBuilder.create()

    /**
     * Creates a new JedisSentinelClientBuilder instance.
     *
     * @return A new JedisSentinelClientBuilder instance
     */
    @JvmStatic
    fun jedisSentinel(): JedisSentinelClientBuilder = JedisSentinelClientBuilder.create()

    /**
     * Creates a new builder instance for the specified Redis client type.
     *
     * @param type The Redis client type
     * @return A new builder instance for the specified Redis client type
     * @throws IllegalArgumentException if the specified Redis client type is not supported
     */
    @JvmStatic
    fun <T> builder(type: Class<T>): RedisClientBuilder<*> =
        when {
            JedisPool::class.java.isAssignableFrom(type) -> jedis()
            UnifiedJedis::class.java.isAssignableFrom(type) -> unifiedJedis()
            JedisPooled::class.java.isAssignableFrom(type) -> jedisPooled()
            RedisClient::class.java.isAssignableFrom(type) -> lettuce()
            else -> throw IllegalArgumentException("Unsupported Redis client type: ${type.name}")
        }

    /**
     * Creates a new cluster builder instance for the specified Redis cluster client type.
     *
     * @param type The Redis cluster client type
     * @return A new cluster builder instance for the specified Redis cluster client type
     * @throws IllegalArgumentException if the specified Redis cluster client type is not supported
     */
    @JvmStatic
    fun <T> clusterBuilder(type: Class<T>): RedisClusterClientBuilder<*> =
        when {
            JedisCluster::class.java.isAssignableFrom(type) -> jedisCluster()
            RedisClusterClient::class.java.isAssignableFrom(type) -> lettuceCluster()
            else -> throw IllegalArgumentException("Unsupported Redis cluster client type: ${type.name}")
        }

    /**
     * Creates a new sentinel builder instance for the specified Redis sentinel client type.
     *
     * @param type The Redis sentinel client type
     * @return A new sentinel builder instance for the specified Redis sentinel client type
     * @throws IllegalArgumentException if the specified Redis sentinel client type is not supported
     */
    @JvmStatic
    fun <T> sentinelBuilder(type: Class<T>): RedisSentinelClientBuilder<*> =
        when {
            JedisSentinelPool::class.java.isAssignableFrom(type) -> jedisSentinel()
            else -> throw IllegalArgumentException("Unsupported Redis sentinel client type: ${type.name}")
        }
}
