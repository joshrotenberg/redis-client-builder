package com.joshrotenberg.redis.client.builder.failover

import java.util.concurrent.TimeUnit

/**
 * A health check that tests Redis connectivity by sending a PING command.
 * This is the most basic health check that verifies the Redis server is responsive.
 * 
 * This implementation is client-agnostic and uses a provided function to execute the PING command.
 */
class PingHealthCheck(
    private val pingFunction: () -> Boolean
) : AbstractRedisHealthCheck() {

    /**
     * Executes the PING command against the Redis server using the provided function.
     * 
     * @return true if the server responds successfully, false otherwise
     */
    override fun doExecute(): Boolean {
        return try {
            pingFunction()
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        /**
         * Creates a new PingHealthCheck with default configuration.
         * 
         * @param pingFunction a function that executes a PING command and returns true if successful
         * @return a new PingHealthCheck instance
         */
        fun create(pingFunction: () -> Boolean): PingHealthCheck {
            return PingHealthCheck(pingFunction).apply {
                // Default to checking every 10 seconds
                schedulePeriod(10, TimeUnit.SECONDS)
            }
        }
    }
}
