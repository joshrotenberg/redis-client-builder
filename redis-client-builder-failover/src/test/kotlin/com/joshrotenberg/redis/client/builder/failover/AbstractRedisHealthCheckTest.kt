package com.joshrotenberg.redis.client.builder.failover

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class AbstractRedisHealthCheckTest {

    @Test
    fun testBasicExecution() {
        val executed = AtomicBoolean(false)
        val healthCheck = object : AbstractRedisHealthCheck() {
            override fun doExecute(): Boolean {
                executed.set(true)
                // Add a small delay to ensure response time is measurable
                Thread.sleep(10)
                return true
            }
        }

        // Execute the health check
        val result = healthCheck.execute()

        // Verify it was executed and returned the expected result
        assertTrue(executed.get(), "Health check should have been executed")
        assertTrue(result, "Health check should have returned true")
        assertTrue(healthCheck.isHealthy(), "Health check should report as healthy")
        assertNotNull(healthCheck.getLastExecutionTime(), "Last execution time should be set")
        assertNotNull(healthCheck.getLastResponseTime(), "Last response time should be set")

        // Print debug information
        println("Last execution time: ${healthCheck.getLastExecutionTime()}")
        println("Last response time: ${healthCheck.getLastResponseTime()}")
    }

    @Test
    fun testRetryLogic() {
        val attempts = AtomicInteger(0)
        val healthCheck = object : AbstractRedisHealthCheck() {
            override fun doExecute(): Boolean {
                val attempt = attempts.incrementAndGet()
                // Fail on the first two attempts, succeed on the third
                return attempt >= 3
            }
        }

        // Configure retries
        healthCheck.retries(3).retryDelay(10)

        // Execute the health check
        val result = healthCheck.execute()

        // Verify it was executed multiple times and eventually succeeded
        assertEquals(3, attempts.get(), "Health check should have been executed 3 times")
        assertTrue(result, "Health check should have returned true after retries")
        assertTrue(healthCheck.isHealthy(), "Health check should report as healthy")
    }

    @Test
    fun testExceptionHandling() {
        val attempts = AtomicInteger(0)
        val healthCheck = object : AbstractRedisHealthCheck() {
            override fun doExecute(): Boolean {
                val attempt = attempts.incrementAndGet()
                if (attempt < 3) {
                    throw RuntimeException("Simulated failure")
                }
                return true
            }
        }

        // Configure retries
        healthCheck.retries(3).retryDelay(10)

        // Execute the health check
        val result = healthCheck.execute()

        // Verify it was executed multiple times and eventually succeeded
        assertEquals(3, attempts.get(), "Health check should have been executed 3 times")
        assertTrue(result, "Health check should have returned true after retries")
        assertTrue(healthCheck.isHealthy(), "Health check should report as healthy")
    }

    @Test
    fun testConfiguration() {
        val healthCheck = object : AbstractRedisHealthCheck() {
            override fun doExecute(): Boolean {
                return true
            }
        }

        // Configure the health check
        healthCheck.timeout(1000).retries(5).retryDelay(100)

        // Verify the configuration was applied
        val result = healthCheck.execute()
        assertTrue(result, "Health check should have returned true")
    }

    @Test
    fun testScheduling() {
        val executions = AtomicInteger(0)
        val healthCheck = object : AbstractRedisHealthCheck() {
            override fun doExecute(): Boolean {
                executions.incrementAndGet()
                return true
            }
        }

        // Configure a very short schedule period for testing
        healthCheck.schedulePeriod(100, TimeUnit.MILLISECONDS)

        // Start the health check
        healthCheck.startAsync().awaitRunning()

        // Wait for a few executions
        Thread.sleep(350) // Should allow for at least 3 executions

        // Stop the health check
        healthCheck.stopAsync().awaitTerminated()

        // Verify it was executed multiple times
        assertTrue(executions.get() >= 3, "Health check should have been executed at least 3 times")
    }
}
