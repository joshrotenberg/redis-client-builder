package com.joshrotenberg.redis.client.builder.integration

import com.joshrotenberg.redis.client.builder.RedisClientBuilderFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JedisPooledIntegrationTest : RedisContainerTest() {

    @Test
    fun testJedisPooledConnection() {
        // Create a JedisPooled instance using the builder with the container's host and port
        val jedisPooled = RedisClientBuilderFactory.jedisPooled()
            .host(redisHost)
            .port(redisPort)
            .build()

        // Use the JedisPooled instance to perform operations
        jedisPooled.use { jedis ->
            // Test basic Redis operations

            // Test SET and GET
            val key = "test:jedispooled:key"
            val value = "Hello, JedisPooled!"
            jedis.set(key, value)
            val retrievedValue = jedis.get(key)
            assertEquals(value, retrievedValue, "Retrieved value should match the set value")

            // Test EXISTS
            assertTrue(jedis.exists(key), "Key should exist after setting a value")

            // Test DEL
            jedis.del(key)
            assertFalse(jedis.exists(key), "Key should not exist after deletion")

            // Test INCR
            val counterKey = "test:jedispooled:counter"
            jedis.set(counterKey, "0")
            val newValue = jedis.incr(counterKey)
            assertEquals(1, newValue, "Counter should be incremented to 1")

            // Test EXPIRE and TTL
            jedis.expire(counterKey, 10)
            val ttl = jedis.ttl(counterKey)
            assertTrue(ttl > 0 && ttl <= 10, "TTL should be between 0 and 10 seconds")

            // Clean up
            jedis.del(counterKey)
        }
    }

    @Test
    fun testJedisPooledWithCustomConfiguration() {
        // Create a JedisPooled instance with custom configuration
        val jedisPooled = RedisClientBuilderFactory.jedisPooled()
            .host(redisHost)
            .port(redisPort)
            .connectionTimeout(5000)
            .socketTimeout(3000)
            .clientName("test-client")
            .build()

        // Verify that we can use the JedisPooled instance
        jedisPooled.use { jedis ->
            val key = "test:jedispooled:custom:config"
            val value = "Custom configuration works with JedisPooled!"
            jedis.set(key, value)
            val retrievedValue = jedis.get(key)
            assertEquals(value, retrievedValue)

            // Clean up
            jedis.del(key)
        }
    }
}