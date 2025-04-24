package com.joshrotenberg.redis.client.builder.failover

import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * A health check that tests Redis functionality by executing a specific command.
 * This allows for more complex health checks beyond simple connectivity testing.
 *
 * This implementation is client-agnostic and uses a provided function to execute the command.
 */
class CommandHealthCheck(
    private val commandFunction: () -> Boolean,
    private val commandName: String
) : AbstractRedisHealthCheck() {

    /**
     * Executes the Redis command using the provided function.
     *
     * @return true if the command executes successfully, false otherwise
     */
    override fun doExecute(): Boolean {
        return try {
            commandFunction()
        } catch (e: IOException) {
            // Log the exception for debugging purposes
            System.err.println("Command health check '$commandName' failed with I/O error: ${e.message}")
            false
        } catch (e: IllegalArgumentException) {
            // Log the exception for debugging purposes
            System.err.println("Command health check '$commandName' failed with illegal argument: ${e.message}")
            false
        } catch (e: IllegalStateException) {
            // Log the exception for debugging purposes
            System.err.println("Command health check '$commandName' failed with illegal state: ${e.message}")
            false
        } catch (e: UnsupportedOperationException) {
            // Log the exception for debugging purposes
            System.err.println("Command health check '$commandName' failed with unsupported operation: ${e.message}")
            false
        } catch (e: InterruptedException) {
            // Log the exception for debugging purposes
            System.err.println("Command health check '$commandName' failed with interruption: ${e.message}")
            false
        }
    }

    /**
     * Gets the name of the command being executed.
     * This is used for logging and debugging purposes.
     *
     * @return the command name
     */
    fun getCommandName(): String {
        return commandName
    }

    companion object {
        /**
         * Creates a new CommandHealthCheck with default configuration.
         *
         * @param commandFunction a function that executes a Redis command and returns true if successful
         * @param commandName the name of the command being executed (for logging and debugging)
         * @return a new CommandHealthCheck instance
         */
        fun create(commandFunction: () -> Boolean, commandName: String): CommandHealthCheck {
            return CommandHealthCheck(commandFunction, commandName).apply {
                // Default to checking every 30 seconds
                schedulePeriod(30, TimeUnit.SECONDS)
            }
        }
    }
}
