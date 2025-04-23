package com.joshrotenberg.redis.client.builder.jedis

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class JedisClientBuilderTest {
    @Test
    fun testDefaultConfiguration() {
        // Test that the builder can be created and configured with default values
        val builder = JedisClientBuilder.create()
        assertNotNull(builder)

        // Test that the build method returns a non-null JedisPool instance
        val jedisPool = builder.build()
        assertNotNull(jedisPool)
        assertTrue(jedisPool is JedisPool)
    }

    @Test
    fun testCustomConfiguration() {
        // Test that the builder can be created and configured with custom values
        val host = "redis-host"
        val port = 6380
        val password = "secret"
        val database = 2
        val connectionTimeout = 5000
        val socketTimeout = 3000
        val ssl = true
        val maxTotal = 50
        val maxIdle = 10
        val minIdle = 5

        val builder = JedisClientBuilder.create()
            .host(host)
            .port(port)
            .password(password)
            .database(database)
            .connectionTimeout(connectionTimeout)
            .socketTimeout(socketTimeout)
            .ssl(ssl)
            .maxTotal(maxTotal)
            .maxIdle(maxIdle)
            .minIdle(minIdle)

        assertNotNull(builder)

        // Test that the build method returns a non-null JedisPool instance
        val jedisPool = builder.build()
        assertNotNull(jedisPool)
        assertTrue(jedisPool is JedisPool)

        // Note: We don't try to connect to the Redis server in this test
        // because it's just testing the builder configuration, not the connection.
        // For connection tests, see the integration tests.
    }
}
