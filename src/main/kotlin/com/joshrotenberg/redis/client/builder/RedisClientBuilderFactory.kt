package com.joshrotenberg.redis.client.builder

import com.joshrotenberg.redis.client.builder.jedis.JedisClientBuilder
import com.joshrotenberg.redis.client.builder.jedis.UnifiedJedisClientBuilder
import com.joshrotenberg.redis.client.builder.lettuce.LettuceClientBuilder
import io.lettuce.core.RedisClient
import redis.clients.jedis.JedisPool
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
     * Creates a new LettuceClientBuilder instance.
     *
     * @return A new LettuceClientBuilder instance
     */
    @JvmStatic
    fun lettuce(): LettuceClientBuilder = LettuceClientBuilder.create()

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
            RedisClient::class.java.isAssignableFrom(type) -> lettuce()
            else -> throw IllegalArgumentException("Unsupported Redis client type: ${type.name}")
        }
}
