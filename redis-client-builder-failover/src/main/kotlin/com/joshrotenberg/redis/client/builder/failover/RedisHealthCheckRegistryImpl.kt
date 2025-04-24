package com.joshrotenberg.redis.client.builder.failover

import java.util.concurrent.ConcurrentHashMap

/**
 * Implementation of the RedisHealthCheckRegistry interface.
 * This class manages multiple Redis health checks for different endpoints.
 */
class RedisHealthCheckRegistryImpl : RedisHealthCheckRegistry {
    // Map of endpoint to list of health checks
    private val healthChecks = ConcurrentHashMap<Pair<String, Int>, MutableList<RedisHealthCheck>>()

    override fun registerHealthCheck(host: String, port: Int, healthCheck: RedisHealthCheck): RedisHealthCheckRegistry {
        val endpoint = Pair(host, port)
        healthChecks.computeIfAbsent(endpoint) { mutableListOf() }.add(healthCheck)
        return this
    }

    override fun unregisterHealthChecks(host: String, port: Int): RedisHealthCheckRegistry {
        val endpoint = Pair(host, port)
        healthChecks.remove(endpoint)
        return this
    }

    override fun unregisterHealthCheck(host: String, port: Int, healthCheck: RedisHealthCheck): RedisHealthCheckRegistry {
        val endpoint = Pair(host, port)
        healthChecks[endpoint]?.remove(healthCheck)
        return this
    }

    override fun getHealthChecks(host: String, port: Int): List<RedisHealthCheck> {
        val endpoint = Pair(host, port)
        return healthChecks[endpoint]?.toList() ?: emptyList()
    }

    override fun getAllHealthChecks(): Map<Pair<String, Int>, List<RedisHealthCheck>> {
        return healthChecks.mapValues { it.value.toList() }
    }

    override fun executeHealthChecks(host: String, port: Int): Boolean {
        val endpoint = Pair(host, port)
        val checks = healthChecks[endpoint] ?: return true // No checks means healthy by default

        // Execute all health checks and return true only if all pass
        return checks.all { it.execute() }
    }

    override fun executeAllHealthChecks(): Map<Pair<String, Int>, Boolean> {
        return healthChecks.keys.associateWith { executeHealthChecks(it.first, it.second) }
    }

    override fun isEndpointHealthy(host: String, port: Int): Boolean {
        val endpoint = Pair(host, port)
        val checks = healthChecks[endpoint] ?: return true // No checks means healthy by default

        // An endpoint is healthy only if all its health checks report healthy
        return checks.all { it.isHealthy() }
    }

    override fun getHealthyEndpoints(): List<Pair<String, Int>> {
        return healthChecks.keys.filter { isEndpointHealthy(it.first, it.second) }
    }
}
