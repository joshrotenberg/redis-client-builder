package com.joshrotenberg.redis.client.builder.jedis

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import redis.clients.jedis.UnifiedJedis
import java.net.URI

class UnifiedJedisClientBuilderTest {
    @Test
    fun testDefaultConfiguration() {
        // Test that the builder can be created and configured with default values
        val builder = UnifiedJedisClientBuilder.create()
        assertNotNull(builder)

        // Test that the build method returns a non-null UnifiedJedis instance
        val unifiedJedis = builder.build()
        assertNotNull(unifiedJedis)
        assertTrue(unifiedJedis is UnifiedJedis)
        
        // Close the connection to avoid resource leaks
        unifiedJedis.close()
    }

    @Test
    fun testCustomConfiguration() {
        // Test that the builder can be created and configured with custom values
        val host = "redis-host"
        val port = 6380
        val password = "secret"
        val user = "default"
        val database = 2
        val connectionTimeout = 5000
        val socketTimeout = 3000
        val ssl = true
        val clientName = "test-client"

        val builder = UnifiedJedisClientBuilder.create()
            .host(host)
            .port(port)
            .password(password)
            .user(user)
            .database(database)
            .connectionTimeout(connectionTimeout)
            .socketTimeout(socketTimeout)
            .ssl(ssl)
            .clientName(clientName)

        assertNotNull(builder)

        // Test that the build method returns a non-null UnifiedJedis instance
        val unifiedJedis = builder.build()
        assertNotNull(unifiedJedis)
        assertTrue(unifiedJedis is UnifiedJedis)
        
        // Close the connection to avoid resource leaks
        unifiedJedis.close()

        // Note: We don't try to connect to the Redis server in this test
        // because it's just testing the builder configuration, not the connection.
        // For connection tests, see the integration tests.
    }
    
    @Test
    fun testUriConfiguration() {
        // Test that the builder can be created and configured with a URI
        val uri = URI.create("redis://localhost:6379/0")
        
        val builder = UnifiedJedisClientBuilder.create()
            .uri(uri)
            
        assertNotNull(builder)
        
        // Test that the build method returns a non-null UnifiedJedis instance
        val unifiedJedis = builder.build()
        assertNotNull(unifiedJedis)
        assertTrue(unifiedJedis is UnifiedJedis)
        
        // Close the connection to avoid resource leaks
        unifiedJedis.close()
    }
    
    @Test
    fun testUriStringConfiguration() {
        // Test that the builder can be created and configured with a URI string
        val uriString = "redis://localhost:6379/0"
        
        val builder = UnifiedJedisClientBuilder.create()
            .uri(uriString)
            
        assertNotNull(builder)
        
        // Test that the build method returns a non-null UnifiedJedis instance
        val unifiedJedis = builder.build()
        assertNotNull(unifiedJedis)
        assertTrue(unifiedJedis is UnifiedJedis)
        
        // Close the connection to avoid resource leaks
        unifiedJedis.close()
    }
}