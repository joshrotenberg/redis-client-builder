package com.joshrotenberg.redis.client.builder

import com.joshrotenberg.redis.client.builder.jedis.JedisClientBuilder
import com.joshrotenberg.redis.client.builder.lettuce.LettuceClientBuilder
import io.lettuce.core.RedisClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import redis.clients.jedis.JedisPool

class RedisClientBuilderFactoryTest {

    @Test
    fun testJedisBuilder() {
        val builder = RedisClientBuilderFactory.jedis()
        assertNotNull(builder)
        assertTrue(builder is JedisClientBuilder)
    }

    @Test
    fun testLettuceBuilder() {
        val builder = RedisClientBuilderFactory.lettuce()
        assertNotNull(builder)
        assertTrue(builder is LettuceClientBuilder)
    }

    @Test
    fun testGenericBuilderWithJedisPool() {
        val builder = RedisClientBuilderFactory.builder(JedisPool::class.java)
        assertNotNull(builder)
        assertTrue(builder is JedisClientBuilder)
    }

    @Test
    fun testGenericBuilderWithRedisClient() {
        val builder = RedisClientBuilderFactory.builder(RedisClient::class.java)
        assertNotNull(builder)
        assertTrue(builder is LettuceClientBuilder)
    }

    @Test
    fun testGenericBuilderWithUnsupportedType() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            RedisClientBuilderFactory.builder(String::class.java)
        }
        assertEquals("Unsupported Redis client type: java.lang.String", exception.message)
    }
}
