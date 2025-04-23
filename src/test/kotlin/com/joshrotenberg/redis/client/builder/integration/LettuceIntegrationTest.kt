package com.joshrotenberg.redis.client.builder.integration

import com.joshrotenberg.redis.client.builder.RedisClientBuilderFactory
import io.lettuce.core.ClientOptions
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LettuceIntegrationTest : RedisContainerTest() {

    private lateinit var redisClient: RedisClient
    private lateinit var connection: StatefulRedisConnection<String, String>
    private lateinit var syncCommands: RedisCommands<String, String>

    @BeforeEach
    fun setup() {
        // Create a RedisClient using the builder with the container's host and port
        redisClient = RedisClientBuilderFactory.lettuce()
            .host(redisHost)
            .port(redisPort)
            .build()

        // Get a connection and commands
        connection = redisClient.connect()
        syncCommands = connection.sync()
    }

    @AfterEach
    fun tearDown() {
        connection.close()
        redisClient.shutdown()
    }

    @Test
    fun testLettuceConnection() {
        // Test basic Redis operations
        
        // Test SET and GET
        val key = "test:lettuce:key"
        val value = "Hello, Lettuce!"
        syncCommands.set(key, value)
        val retrievedValue = syncCommands.get(key)
        assertEquals(value, retrievedValue, "Retrieved value should match the set value")
        
        // Test EXISTS
        assertTrue(syncCommands.exists(key) > 0, "Key should exist after setting a value")
        
        // Test DEL
        syncCommands.del(key)
        assertEquals(0, syncCommands.exists(key), "Key should not exist after deletion")
        
        // Test INCR
        val counterKey = "test:lettuce:counter"
        syncCommands.set(counterKey, "0")
        val newValue = syncCommands.incr(counterKey)
        assertEquals(1, newValue, "Counter should be incremented to 1")
        
        // Test EXPIRE and TTL
        syncCommands.expire(counterKey, 10)
        val ttl = syncCommands.ttl(counterKey)
        assertTrue(ttl > 0 && ttl <= 10, "TTL should be between 0 and 10 seconds")
        
        // Clean up
        syncCommands.del(counterKey)
    }

    @Test
    fun testLettuceClientWithCustomConfiguration() {
        // Close the default connection and client
        connection.close()
        redisClient.shutdown()
        
        // Create a RedisClient with custom configuration
        redisClient = RedisClientBuilderFactory.lettuce()
            .host(redisHost)
            .port(redisPort)
            .connectionTimeout(5000)
            .socketTimeout(3000)
            .autoReconnect(false)
            .requestQueueSize(1000)
            .publishOnScheduler(true)
            .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
            .build()

        // Get a new connection and commands
        connection = redisClient.connect()
        syncCommands = connection.sync()
        
        // Verify that we can use the connection
        val key = "test:lettuce:custom:config"
        val value = "Custom configuration works!"
        syncCommands.set(key, value)
        val retrievedValue = syncCommands.get(key)
        assertEquals(value, retrievedValue)
        
        // Clean up
        syncCommands.del(key)
    }
}