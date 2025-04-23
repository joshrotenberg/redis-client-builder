package com.joshrotenberg.redis.client.builder.lettuce

import io.lettuce.core.ClientOptions
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.lang.reflect.Field
import java.time.Duration

class LettuceClientBuilderTest {

    @Test
    fun testDefaultConfiguration() {
        val redisClient = LettuceClientBuilder.create().build()

        // Get the RedisURI from the client
        val uri = getRedisURI(redisClient)

        // Verify default values
        assertEquals("localhost", uri.host)
        assertEquals(6379, uri.port)
        assertNull(uri.password)
        assertEquals(0, uri.database)
        assertEquals(Duration.ofMillis(60000), uri.timeout)
        assertFalse(uri.isSsl)

        // Verify client options
        val clientOptions = redisClient.options
        assertTrue(clientOptions.isAutoReconnect)
        assertEquals(2147483647, clientOptions.requestQueueSize)
        assertFalse(clientOptions.isPublishOnScheduler)
        assertEquals(ClientOptions.DisconnectedBehavior.DEFAULT, clientOptions.disconnectedBehavior)
    }

    @Test
    fun testCustomConfiguration() {
        val host = "redis-host"
        val port = 6380
        val password = "secret"
        val database = 2
        val connectionTimeout = 5000
        val socketTimeout = 3000
        val ssl = true
        val autoReconnect = false
        val requestQueueSize = 1000
        val publishOnScheduler = true
        val disconnectedBehavior = ClientOptions.DisconnectedBehavior.REJECT_COMMANDS

        val redisClient = LettuceClientBuilder.create()
            .host(host)
            .port(port)
            .password(password)
            .database(database)
            .connectionTimeout(connectionTimeout)
            .socketTimeout(socketTimeout)
            .ssl(ssl)
            .autoReconnect(autoReconnect)
            .requestQueueSize(requestQueueSize)
            .publishOnScheduler(publishOnScheduler)
            .disconnectedBehavior(disconnectedBehavior)
            .build()

        // Get the RedisURI from the client
        val uri = getRedisURI(redisClient)

        // Verify custom values
        assertEquals(host, uri.host)
        assertEquals(port, uri.port)
        assertNotNull(uri.password)
        assertEquals(database, uri.database)
        assertEquals(Duration.ofMillis(connectionTimeout.toLong()), uri.timeout)
        assertTrue(uri.isSsl)

        // Verify client options
        val clientOptions = redisClient.options
        assertEquals(autoReconnect, clientOptions.isAutoReconnect)
        assertEquals(requestQueueSize, clientOptions.requestQueueSize)
        assertEquals(publishOnScheduler, clientOptions.isPublishOnScheduler)
        assertEquals(disconnectedBehavior, clientOptions.disconnectedBehavior)
    }

    /**
     * Helper method to get the RedisURI from a RedisClient using reflection
     */
    private fun getRedisURI(redisClient: RedisClient): RedisURI {
        // The RedisURI is stored in a private field in the RedisClient
        // We need to use reflection to access it
        val redisURIField = findField(redisClient.javaClass, "redisURI")
        redisURIField.isAccessible = true
        return redisURIField.get(redisClient) as RedisURI
    }

    /**
     * Helper method to find a field in a class or its superclasses
     */
    private fun findField(clazz: Class<*>, fieldName: String): Field {
        var currentClass: Class<*>? = clazz
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName)
            } catch (e: NoSuchFieldException) {
                currentClass = currentClass.superclass
            }
        }
        throw NoSuchFieldException("Field $fieldName not found in class hierarchy of ${clazz.name}")
    }
}
