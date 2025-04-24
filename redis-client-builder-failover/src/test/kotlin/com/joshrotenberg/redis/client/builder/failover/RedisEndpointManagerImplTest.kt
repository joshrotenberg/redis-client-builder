package com.joshrotenberg.redis.client.builder.failover

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull

class RedisEndpointManagerImplTest {

    @Test
    fun testAddAndRemoveEndpoints() {
        // Create the endpoint manager
        val manager = RedisEndpointManagerImpl()

        // Add endpoints
        manager.addEndpoint("localhost", 6379)
        manager.addEndpoint("localhost", 6380)

        // Verify endpoints were added
        val endpoints = manager.getEndpoints()
        assertEquals(2, endpoints.size, "Should have 2 endpoints")
        assertTrue(endpoints.contains(Pair("localhost", 6379)), "Should contain localhost:6379")
        assertTrue(endpoints.contains(Pair("localhost", 6380)), "Should contain localhost:6380")

        // Remove an endpoint
        manager.removeEndpoint("localhost", 6380)

        // Verify endpoint was removed
        val updatedEndpoints = manager.getEndpoints()
        assertEquals(1, updatedEndpoints.size, "Should have 1 endpoint")
        assertTrue(updatedEndpoints.contains(Pair("localhost", 6379)), "Should contain localhost:6379")
        assertFalse(updatedEndpoints.contains(Pair("localhost", 6380)), "Should not contain localhost:6380")
    }

    @Test
    fun testEndpointHealthTracking() {
        // Create the endpoint manager
        val manager = RedisEndpointManagerImpl()

        // Add endpoints (default to healthy)
        manager.addEndpoint("localhost", 6379)
        manager.addEndpoint("localhost", 6380)

        // Verify all endpoints are healthy by default
        assertTrue(manager.isEndpointHealthy("localhost", 6379), "Endpoint should be healthy by default")
        assertTrue(manager.isEndpointHealthy("localhost", 6380), "Endpoint should be healthy by default")

        // Mark an endpoint as unhealthy
        manager.updateEndpointHealth("localhost", 6380, false)

        // Verify health status was updated
        assertTrue(manager.isEndpointHealthy("localhost", 6379), "Endpoint should still be healthy")
        assertFalse(manager.isEndpointHealthy("localhost", 6380), "Endpoint should be unhealthy")

        // Get healthy endpoints
        val healthyEndpoints = manager.getHealthyEndpoints()
        assertEquals(1, healthyEndpoints.size, "Should have 1 healthy endpoint")
        assertTrue(healthyEndpoints.contains(Pair("localhost", 6379)), "Healthy endpoint should be localhost:6379")
    }

    @Test
    fun testRoundRobinSelectionStrategy() {
        // Create the endpoint manager with round-robin strategy (default)
        val manager = RedisEndpointManagerImpl()

        // Add endpoints
        manager.addEndpoint("localhost", 6379)
        manager.addEndpoint("localhost", 6380)
        manager.addEndpoint("localhost", 6381)

        // Select endpoints multiple times and verify round-robin behavior
        val selected1 = manager.selectHealthyEndpoint()
        val selected2 = manager.selectHealthyEndpoint()
        val selected3 = manager.selectHealthyEndpoint()
        val selected4 = manager.selectHealthyEndpoint()

        // First three selections should be different
        assertNotEquals(selected1, selected2, "First and second selections should be different")
        assertNotEquals(selected2, selected3, "Second and third selections should be different")

        // Fourth selection should be the same as the first (round-robin)
        assertEquals(selected1, selected4, "Fourth selection should be the same as the first")
    }

    @Test
    fun testRandomSelectionStrategy() {
        // Create the endpoint manager
        val manager = RedisEndpointManagerImpl()

        // Set random selection strategy
        manager.setSelectionStrategy(RedisEndpointManagerImpl.RandomSelectionStrategy())

        // Add endpoints
        manager.addEndpoint("localhost", 6379)
        manager.addEndpoint("localhost", 6380)

        // Select an endpoint
        val selected = manager.selectHealthyEndpoint()

        // Verify a valid endpoint was selected
        assertNotNull(selected, "Should have selected an endpoint")
        assertTrue(
            selected == Pair("localhost", 6379) || selected == Pair("localhost", 6380),
            "Selected endpoint should be one of the added endpoints"
        )
    }

    @Test
    fun testWeightedSelectionStrategy() {
        // Create the endpoint manager
        val manager = RedisEndpointManagerImpl()

        // Create weighted selection strategy
        val weightedStrategy = RedisEndpointManagerImpl.WeightedSelectionStrategy()
        manager.setSelectionStrategy(weightedStrategy)

        // Add endpoints
        manager.addEndpoint("localhost", 6379)
        manager.addEndpoint("localhost", 6380)

        // Set weights (lower is better)
        weightedStrategy.updateWeight(Pair("localhost", 6379), 100.0) // Slower
        weightedStrategy.updateWeight(Pair("localhost", 6380), 50.0) // Faster

        // Select an endpoint
        val selected = manager.selectHealthyEndpoint()

        // Verify the faster endpoint was selected
        assertEquals(Pair("localhost", 6380), selected, "Should select the endpoint with lower weight")
    }

    @Test
    fun testPrioritySelectionStrategy() {
        // Create the endpoint manager
        val manager = RedisEndpointManagerImpl()

        // Create priority selection strategy
        val priorityStrategy = RedisEndpointManagerImpl.PrioritySelectionStrategy()
        manager.setSelectionStrategy(priorityStrategy)

        // Add endpoints
        manager.addEndpoint("localhost", 6379)
        manager.addEndpoint("localhost", 6380)
        manager.addEndpoint("localhost", 6381)

        // Set priorities (lower is higher priority)
        priorityStrategy.setPriority(Pair("localhost", 6379), 3) // Lowest priority
        priorityStrategy.setPriority(Pair("localhost", 6380), 1) // Highest priority
        priorityStrategy.setPriority(Pair("localhost", 6381), 2) // Medium priority

        // Select an endpoint
        val selected = manager.selectHealthyEndpoint()

        // Verify the highest priority endpoint was selected
        assertEquals(Pair("localhost", 6380), selected, "Should select the endpoint with highest priority")
    }

    @Test
    fun testNoHealthyEndpoints() {
        // Create the endpoint manager
        val manager = RedisEndpointManagerImpl()

        // Add endpoints and mark them as unhealthy
        manager.addEndpoint("localhost", 6379)
        manager.updateEndpointHealth("localhost", 6379, false)

        // Try to select a healthy endpoint
        val selected = manager.selectHealthyEndpoint()

        // Verify no endpoint was selected
        assertNull(selected, "Should not select any endpoint when none are healthy")
    }
}
