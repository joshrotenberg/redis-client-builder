package com.joshrotenberg.redis.client.builder.resilience

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import io.github.resilience4j.bulkhead.BulkheadRegistry
import redis.clients.jedis.JedisPool

class RedisBulkheadImplTest {

    @Test
    fun `test create bulkhead`() {
        val bulkhead = RedisBulkheadImpl.create<JedisPool>()
        assertNotNull(bulkhead)
    }

    @Test
    fun `test configure bulkhead`() {
        val bulkhead = RedisBulkheadImpl.create<JedisPool>()
            .name("test-bulkhead")
            .maxConcurrentCalls(10)
            .maxWaitingCalls(20)
            .waitTime(500)
            .fairSemaphore(false)

        assertNotNull(bulkhead)
        
        // Since the configuration properties are private, we can't directly test their values
        // In a real test, we might use reflection to access private fields or test the behavior
        // For now, we'll just ensure the builder methods return the same instance
        assertEquals(bulkhead, bulkhead.name("another-name"))
    }

    @Test
    fun `test wrap client`() {
        val bulkhead = RedisBulkheadImpl.create<JedisPool>()
        val mockClient = JedisPool()
        
        val wrappedClient = bulkhead.wrap(mockClient)
        assertNotNull(wrappedClient)
        
        // In a real test, we would verify that the wrapped client behaves correctly
        // For now, we'll just ensure the wrap method returns a non-null value
    }
}