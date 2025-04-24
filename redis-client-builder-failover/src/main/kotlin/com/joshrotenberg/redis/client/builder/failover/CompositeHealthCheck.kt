package com.joshrotenberg.redis.client.builder.failover

import java.util.concurrent.TimeUnit

/**
 * A health check that combines multiple health checks into a single check.
 * This allows for complex health checking scenarios where multiple conditions need to be satisfied.
 */
class CompositeHealthCheck(
    private val healthChecks: List<RedisHealthCheck>,
    private val mode: Mode = Mode.ALL
) : AbstractRedisHealthCheck() {

    /**
     * Executes all the health checks according to the configured mode.
     *
     * @return true if the checks pass according to the mode, false otherwise
     */
    override fun doExecute(): Boolean {
        return when (mode) {
            Mode.ALL -> executeAll()
            Mode.ANY -> executeAny()
            Mode.MAJORITY -> executeMajority()
        }
    }

    /**
     * Executes all health checks and returns true only if all pass.
     *
     * @return true if all health checks pass, false otherwise
     */
    private fun executeAll(): Boolean {
        return healthChecks.all { it.execute() }
    }

    /**
     * Executes all health checks and returns true if any pass.
     *
     * @return true if at least one health check passes, false if all fail
     */
    private fun executeAny(): Boolean {
        return healthChecks.any { it.execute() }
    }

    /**
     * Executes all health checks and returns true if a majority pass.
     *
     * @return true if more than half of the health checks pass, false otherwise
     */
    private fun executeMajority(): Boolean {
        if (healthChecks.isEmpty()) {
            return false
        }

        val passCount = healthChecks.count { it.execute() }
        return passCount > healthChecks.size / 2
    }

    /**
     * Gets the list of health checks in this composite.
     *
     * @return the list of health checks
     */
    fun getHealthChecks(): List<RedisHealthCheck> {
        return healthChecks
    }

    /**
     * Gets the mode of this composite health check.
     *
     * @return the mode
     */
    fun getMode(): Mode {
        return mode
    }

    /**
     * Starts all the health checks in this composite.
     */
    override fun startUp() {
        super.startUp()
        healthChecks.forEach {
            if (it is AbstractRedisHealthCheck) {
                it.startAsync()
            }
        }
    }

    /**
     * Stops all the health checks in this composite.
     */
    override fun shutDown() {
        super.shutDown()
        healthChecks.forEach {
            if (it is AbstractRedisHealthCheck) {
                it.stopAsync()
            }
        }
    }

    /**
     * The mode of operation for the composite health check.
     */
    enum class Mode {
        /**
         * All health checks must pass for the composite to pass.
         */
        ALL,

        /**
         * At least one health check must pass for the composite to pass.
         */
        ANY,

        /**
         * More than half of the health checks must pass for the composite to pass.
         */
        MAJORITY
    }

    companion object {
        /**
         * Creates a new CompositeHealthCheck with default configuration.
         *
         * @param healthChecks the list of health checks to combine
         * @param mode the mode of operation (default: ALL)
         * @return a new CompositeHealthCheck instance
         */
        fun create(
            healthChecks: List<RedisHealthCheck>,
            mode: Mode = Mode.ALL
        ): CompositeHealthCheck {
            return CompositeHealthCheck(healthChecks, mode).apply {
                // Default to checking every 30 seconds
                schedulePeriod(30, TimeUnit.SECONDS)
            }
        }

        /**
         * Creates a new CompositeHealthCheck with default configuration.
         *
         * @param vararg healthChecks the health checks to combine
         * @param mode the mode of operation (default: ALL)
         * @return a new CompositeHealthCheck instance
         */
        fun create(
            vararg healthChecks: RedisHealthCheck,
            mode: Mode = Mode.ALL
        ): CompositeHealthCheck {
            return create(healthChecks.toList(), mode)
        }
    }
}
