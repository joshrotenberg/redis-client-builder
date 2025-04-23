package com.joshrotenberg.redis.client.builder.failover

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class PingHealthCheckTest {

    @Test
    fun testSuccessfulPing() {
        // Create a ping function that always succeeds
        val pingExecuted = AtomicBoolean(false)
        val pingFunction = {
            pingExecuted.set(true)
            true
        }
        
        // Create the health check
        val healthCheck = PingHealthCheck.create(pingFunction)
        
        // Execute the health check
        val result = healthCheck.execute()
        
        // Verify it was executed and returned the expected result
        assertTrue(pingExecuted.get(), "Ping function should have been executed")
        assertTrue(result, "Health check should have returned true")
        assertTrue(healthCheck.isHealthy(), "Health check should report as healthy")
    }
    
    @Test
    fun testFailedPing() {
        // Create a ping function that always fails
        val pingFunction = { false }
        
        // Create the health check
        val healthCheck = PingHealthCheck.create(pingFunction)
        
        // Execute the health check
        val result = healthCheck.execute()
        
        // Verify it returned the expected result
        assertFalse(result, "Health check should have returned false")
        assertFalse(healthCheck.isHealthy(), "Health check should report as unhealthy")
    }
    
    @Test
    fun testPingException() {
        // Create a ping function that throws an exception
        val pingFunction = { throw RuntimeException("Simulated ping failure") }
        
        // Create the health check
        val healthCheck = PingHealthCheck.create(pingFunction)
        
        // Execute the health check
        val result = healthCheck.execute()
        
        // Verify it handled the exception and returned the expected result
        assertFalse(result, "Health check should have returned false")
        assertFalse(healthCheck.isHealthy(), "Health check should report as unhealthy")
    }
    
    @Test
    fun testScheduling() {
        // Create a ping function that always succeeds
        val pingCount = java.util.concurrent.atomic.AtomicInteger(0)
        val pingFunction = {
            pingCount.incrementAndGet()
            true
        }
        
        // Create the health check with a short schedule period
        val healthCheck = PingHealthCheck.create(pingFunction).apply {
            schedulePeriod(100, TimeUnit.MILLISECONDS)
        }
        
        // Start the health check
        healthCheck.startAsync().awaitRunning()
        
        // Wait for a few executions
        Thread.sleep(350) // Should allow for at least 3 executions
        
        // Stop the health check
        healthCheck.stopAsync().awaitTerminated()
        
        // Verify it was executed multiple times
        assertTrue(pingCount.get() >= 3, "Ping function should have been executed at least 3 times")
    }
    
    @Test
    fun testCustomConfiguration() {
        // Create a ping function that always succeeds
        val pingFunction = { true }
        
        // Create the health check with custom configuration
        val healthCheck = PingHealthCheck.create(pingFunction).apply {
            timeout(2000)
            retries(2)
            retryDelay(50)
            schedulePeriod(5, TimeUnit.SECONDS)
        }
        
        // Execute the health check
        val result = healthCheck.execute()
        
        // Verify it returned the expected result
        assertTrue(result, "Health check should have returned true")
        assertTrue(healthCheck.isHealthy(), "Health check should report as healthy")
    }
}