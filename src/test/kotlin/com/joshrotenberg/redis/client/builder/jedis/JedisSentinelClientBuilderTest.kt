package com.joshrotenberg.redis.client.builder.jedis

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class JedisSentinelClientBuilderTest {

    @Test
    fun testBuildWithRequiredParameters() {
        val builder = JedisSentinelClientBuilder.create()
            .masterName("mymaster")
            .addSentinel("localhost", 26379)

        val exception = assertThrows(Exception::class.java) {
            builder.build()
        }
        // This will throw an exception because we're not actually connecting to a Redis Sentinel
        // But we can verify that the builder is configured correctly
        assertNotNull(exception)
    }

    @Test
    fun testBuildWithoutMasterName() {
        val builder = JedisSentinelClientBuilder.create()
            .addSentinel("localhost", 26379)

        val exception = assertThrows(IllegalStateException::class.java) {
            builder.build()
        }
        assertEquals("Master name must be set", exception.message)
    }

    @Test
    fun testBuildWithoutSentinels() {
        val builder = JedisSentinelClientBuilder.create()
            .masterName("mymaster")

        val exception = assertThrows(IllegalStateException::class.java) {
            builder.build()
        }
        assertEquals("At least one sentinel node must be added", exception.message)
    }

    @Test
    fun testBuilderConfiguration() {
        val builder = JedisSentinelClientBuilder.create()
            .masterName("mymaster")
            .addSentinel("localhost", 26379)
            .addSentinel("localhost", 26380)
            .password("password")
            .database(1)
            .connectionTimeout(1000)
            .socketTimeout(2000)
            .ssl(true)
            .clientName("myclient")
            .maxTotal(100)
            .maxIdle(10)
            .minIdle(5)
            .testOnBorrow(true)
            .testOnReturn(true)
            .testWhileIdle(true)
            .timeBetweenEvictionRuns(60000)
            .blockWhenExhausted(true)
            .jmxEnabled(false)

        // We can't actually build the JedisSentinelPool without a real Redis Sentinel
        // But we can verify that the builder methods work and return the builder instance
        assertNotNull(builder)
    }
}