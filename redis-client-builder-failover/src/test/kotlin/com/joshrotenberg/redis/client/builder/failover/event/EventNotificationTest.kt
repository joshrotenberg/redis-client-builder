package com.joshrotenberg.redis.client.builder.failover.event

import com.joshrotenberg.redis.client.builder.failover.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class EventNotificationTest {

    @Test
    fun testHealthCheckEvents() {
        // Create a failover manager
        val failoverManager = RedisFailoverManagerImpl()

        // Create a test health check
        val healthCheck = TestHealthCheck()

        // Create a test event listener
        val eventListener = TestEventListener()

        // Register the event listener with the failover manager
        failoverManager.getEventBus().registerListener(eventListener)

        // Add an endpoint and register the health check
        failoverManager.addEndpoint("localhost", 6379)
        failoverManager.registerHealthCheck("localhost", 6379, healthCheck)

        // Execute the health check
        healthCheck.execute()

        // Wait for events to be processed
        eventListener.awaitEvents(2) // Started and Completed events

        // Verify that the correct events were received
        assertEquals(1, eventListener.getEventCount(HealthCheckStartedEvent::class.java))
        assertEquals(1, eventListener.getEventCount(HealthCheckCompletedEvent::class.java))
        assertEquals(0, eventListener.getEventCount(HealthCheckFailedEvent::class.java))

        // Make the health check fail
        healthCheck.setShouldFail(true)

        // Execute the health check again
        healthCheck.execute()

        // Wait for events to be processed
        eventListener.awaitEvents(4) // Previous 2 + Started and Failed events

        // Verify that the correct events were received
        assertEquals(2, eventListener.getEventCount(HealthCheckStartedEvent::class.java))
        assertEquals(1, eventListener.getEventCount(HealthCheckCompletedEvent::class.java))
        assertEquals(1, eventListener.getEventCount(HealthCheckFailedEvent::class.java))
    }

    @Test
    fun testEndpointStatusChangeEvents() {
        // Create a failover manager
        val failoverManager = RedisFailoverManagerImpl()

        // Create a test event listener
        val eventListener = TestEventListener()

        // Register the event listener with the failover manager
        failoverManager.getEventBus().registerListener(eventListener)

        // Add an endpoint
        failoverManager.addEndpoint("localhost", 6379)

        // Update the endpoint health to unhealthy
        failoverManager.getEndpointManager().updateEndpointHealth("localhost", 6379, false)

        // Wait for events to be processed
        eventListener.awaitEvents(1)

        // Verify that the correct events were received
        assertEquals(1, eventListener.getEventCount(EndpointStatusChangeEvent::class.java))

        // Get the event and verify its properties
        val event = eventListener.getLastEvent(EndpointStatusChangeEvent::class.java)
        assertNotNull(event)
        assertEquals("localhost", event!!.host)
        assertEquals(6379, event.port)
        assertFalse(event.isHealthy)
        assertTrue(event.previousStatus) // Default is healthy
        assertTrue(event.isTransitionToUnhealthy())

        // Update the endpoint health back to healthy
        failoverManager.getEndpointManager().updateEndpointHealth("localhost", 6379, true)

        // Wait for events to be processed
        eventListener.awaitEvents(2)

        // Verify that the correct events were received
        assertEquals(2, eventListener.getEventCount(EndpointStatusChangeEvent::class.java))

        // Get the event and verify its properties
        val event2 = eventListener.getLastEvent(EndpointStatusChangeEvent::class.java)
        assertNotNull(event2)
        assertEquals("localhost", event2!!.host)
        assertEquals(6379, event2.port)
        assertTrue(event2.isHealthy)
        assertFalse(event2.previousStatus)
        assertTrue(event2.isTransitionToHealthy())
    }

    @Test
    fun testFailoverEvents() {
        // Create a failover manager
        val failoverManager = RedisFailoverManagerImpl()

        // Create a test event listener
        val eventListener = TestEventListener()

        // Register the event listener with the failover manager
        failoverManager.getEventBus().registerListener(eventListener)

        // Add two endpoints
        failoverManager.addEndpoint("localhost", 6379)
        failoverManager.addEndpoint("localhost", 6380)

        // Get a healthy endpoint
        val endpoint1 = failoverManager.getHealthyEndpoint()
        assertNotNull(endpoint1)
        assertEquals("localhost", endpoint1!!.first)
        // The endpoint could be either 6379 or 6380 depending on the selection strategy
        assertTrue(endpoint1.second == 6379 || endpoint1.second == 6380)

        // Wait for events to be processed
        eventListener.awaitEvents(1)

        // Verify that the correct events were received
        assertEquals(1, eventListener.getEventCount(FailoverEvent::class.java))

        // Get the event and verify its properties
        val event = eventListener.getLastEvent(FailoverEvent::class.java)
        assertNotNull(event)
        assertNull(event!!.previousEndpoint) // First endpoint selection
        assertEquals("localhost", event.newEndpoint.first)
        // The event's newEndpoint should match the endpoint we got from getHealthyEndpoint()
        assertEquals(endpoint1.second, event.newEndpoint.second)
        assertEquals(FailoverReason.OTHER, event.reason)

        // Mark the selected endpoint as unhealthy
        failoverManager.getEndpointManager().updateEndpointHealth("localhost", endpoint1.second, false)

        // Get a healthy endpoint (should be the other one)
        val endpoint2 = failoverManager.getHealthyEndpoint()
        assertNotNull(endpoint2)
        assertEquals("localhost", endpoint2!!.first)
        // The second endpoint should be the other one (not the one we marked as unhealthy)
        assertTrue(endpoint2.second != endpoint1.second)

        // Wait for events to be processed
        eventListener.awaitEvents(3) // Failover + EndpointStatusChange + Failover

        // Verify that the correct events were received
        assertEquals(2, eventListener.getEventCount(FailoverEvent::class.java))

        // Get the event and verify its properties
        val event2 = eventListener.getLastEvent(FailoverEvent::class.java)
        assertNotNull(event2)
        assertNotNull(event2!!.previousEndpoint)
        assertEquals("localhost", event2.previousEndpoint!!.first)
        assertEquals(endpoint1.second, event2.previousEndpoint!!.second)
        assertEquals("localhost", event2.newEndpoint.first)
        assertEquals(endpoint2.second, event2.newEndpoint.second)
        assertEquals(FailoverReason.ENDPOINT_UNHEALTHY, event2.reason)
    }

    /**
     * Test health check implementation that can be configured to succeed or fail.
     */
    private class TestHealthCheck : AbstractRedisHealthCheck() {
        private val shouldFail = AtomicBoolean(false)

        fun setShouldFail(shouldFail: Boolean) {
            this.shouldFail.set(shouldFail)
        }

        override fun doExecute(): Boolean {
            return !shouldFail.get()
        }
    }

    /**
     * Test event listener that counts events by type and provides methods to wait for events.
     */
    private class TestEventListener : EventListener {
        private val eventCounts = mutableMapOf<Class<out Event>, AtomicInteger>()
        private val eventsByType = mutableMapOf<Class<out Event>, MutableList<Event>>()
        private val latch = CountDownLatch(1)
        private var expectedEventCount = 0
        private var actualEventCount = 0

        override fun onEvent(event: Event) {
            val eventClass = event.javaClass
            eventCounts.computeIfAbsent(eventClass) { AtomicInteger(0) }.incrementAndGet()
            eventsByType.computeIfAbsent(eventClass) { mutableListOf() }.add(event)
            actualEventCount++

            if (actualEventCount >= expectedEventCount) {
                latch.countDown()
            }
        }

        fun getEventCount(eventClass: Class<out Event>): Int {
            return eventCounts[eventClass]?.get() ?: 0
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Event> getLastEvent(eventClass: Class<T>): T? {
            return eventsByType[eventClass]?.lastOrNull() as T?
        }

        fun awaitEvents(count: Int): Boolean {
            expectedEventCount = count
            if (actualEventCount >= expectedEventCount) {
                return true
            }
            return latch.await(5, TimeUnit.SECONDS)
        }
    }
}
