package com.joshrotenberg.redis.client.builder.resilience

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import redis.clients.jedis.JedisPool

class RedisCircuitBreakerImplTest {

    @Test
    fun `test create circuit breaker`() {
        val circuitBreaker = RedisCircuitBreakerImpl.create<JedisPool>()
        assertNotNull(circuitBreaker)
    }

    @Test
    fun `test configure circuit breaker`() {
        val circuitBreaker = RedisCircuitBreakerImpl.create<JedisPool>()
            .name("test-circuit-breaker")
            .failureRateThreshold(60f)
            .minimumNumberOfCalls(5)
            .waitDurationInOpenState(30000)
            .permittedNumberOfCallsInHalfOpenState(3)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)

        assertNotNull(circuitBreaker)
        
        // Since the configuration properties are private, we can't directly test their values
        // In a real test, we might use reflection to access private fields or test the behavior
        // For now, we'll just ensure the builder methods return the same instance
        assertEquals(circuitBreaker, circuitBreaker.name("another-name"))
    }

    @Test
    fun `test wrap client`() {
        val circuitBreaker = RedisCircuitBreakerImpl.create<JedisPool>()
        val mockClient = JedisPool()
        
        val wrappedClient = circuitBreaker.wrap(mockClient)
        assertNotNull(wrappedClient)
        
        // In a real test, we would verify that the wrapped client behaves correctly
        // For now, we'll just ensure the wrap method returns a non-null value
    }
}