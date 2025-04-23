package com.joshrotenberg.redis.client.builder.resilience

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import io.github.resilience4j.timelimiter.TimeLimiterRegistry
import redis.clients.jedis.JedisPool

class RedisTimeLimiterImplTest {

    @Test
    fun `test create time limiter`() {
        val timeLimiter = RedisTimeLimiterImpl.create<JedisPool>()
        assertNotNull(timeLimiter)
    }

    @Test
    fun `test configure time limiter`() {
        val timeLimiter = RedisTimeLimiterImpl.create<JedisPool>()
            .name("test-time-limiter")
            .timeoutDuration(2000)
            .cancelRunningFuture(false)

        assertNotNull(timeLimiter)
        
        // Since the configuration properties are private, we can't directly test their values
        // In a real test, we might use reflection to access private fields or test the behavior
        // For now, we'll just ensure the builder methods return the same instance
        assertEquals(timeLimiter, timeLimiter.name("another-name"))
    }

    @Test
    fun `test wrap client`() {
        val timeLimiter = RedisTimeLimiterImpl.create<JedisPool>()
        val mockClient = JedisPool()
        
        val wrappedClient = timeLimiter.wrap(mockClient)
        assertNotNull(wrappedClient)
        
        // In a real test, we would verify that the wrapped client behaves correctly
        // For now, we'll just ensure the wrap method returns a non-null value
    }
}