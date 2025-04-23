package com.joshrotenberg.redis.client.builder.integration

import com.joshrotenberg.redis.client.builder.RedisClientBuilderFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import redis.clients.jedis.JedisPool

class JedisIntegrationTest : RedisContainerTest() {

    @Test
    fun testJedisConnection() {
        // Create a JedisPool using the builder with the container's host and port
        val jedisPool = RedisClientBuilderFactory.jedis()
            .host(redisHost)
            .port(redisPort)
            .build()

        // Use the JedisPool to get a Jedis instance
        jedisPool.resource.use { jedis ->
            // Test basic Redis operations
            
            // Test SET and GET
            val key = "test:key"
            val value = "Hello, Redis!"
            jedis.set(key, value)
            val retrievedValue = jedis.get(key)
            assertEquals(value, retrievedValue, "Retrieved value should match the set value")
            
            // Test EXISTS
            assertTrue(jedis.exists(key), "Key should exist after setting a value")
            
            // Test DEL
            jedis.del(key)
            assertFalse(jedis.exists(key), "Key should not exist after deletion")
            
            // Test INCR
            val counterKey = "test:counter"
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
    fun testJedisPoolWithCustomConfiguration() {
        // Create a JedisPool with custom configuration
        val jedisPool = RedisClientBuilderFactory.jedis()
            .host(redisHost)
            .port(redisPort)
            .connectionTimeout(5000)
            .socketTimeout(3000)
            .maxTotal(50)
            .maxIdle(10)
            .minIdle(5)
            .build()

        // Verify that we can get a resource from the pool and use it
        jedisPool.resource.use { jedis ->
            val key = "test:custom:config"
            val value = "Custom configuration works!"
            jedis.set(key, value)
            val retrievedValue = jedis.get(key)
            assertEquals(value, retrievedValue)
            
            // Clean up
            jedis.del(key)
        }
    }
}