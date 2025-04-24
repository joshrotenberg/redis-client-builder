package com.joshrotenberg.redis.client.builder.failover

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * A health check that tests Redis health via an HTTP endpoint.
 * This is useful for Redis services that expose HTTP health check endpoints,
 * such as Redis Enterprise, Redis Cloud, or custom Redis deployments with HTTP monitoring.
 */
class HttpHealthCheck(
    private val url: URL,
    private val expectedStatusCode: Int = 200,
    private val method: String = "GET"
) : AbstractRedisHealthCheck() {

    /**
     * Executes an HTTP request to the configured URL and checks the response.
     *
     * @return true if the HTTP request returns the expected status code, false otherwise
     */
    override fun doExecute(): Boolean {
        var connection: HttpURLConnection? = null
        return try {
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = method
            connection.connectTimeout = timeoutMs.toInt()
            connection.readTimeout = timeoutMs.toInt()
            connection.instanceFollowRedirects = false

            val statusCode = connection.responseCode
            statusCode == expectedStatusCode
        } catch (e: IOException) {
            // Log the exception for debugging purposes
            System.err.println("HTTP health check failed: ${e.message}")
            // Return false to indicate the health check failed
            false
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Gets the URL being checked.
     *
     * @return the URL
     */
    fun getUrl(): URL {
        return url
    }

    /**
     * Gets the expected HTTP status code.
     *
     * @return the expected status code
     */
    fun getExpectedStatusCode(): Int {
        return expectedStatusCode
    }

    companion object {
        /**
         * Creates a new HttpHealthCheck with default configuration.
         *
         * @param url the URL to check
         * @param expectedStatusCode the expected HTTP status code (default: 200)
         * @param method the HTTP method to use (default: GET)
         * @return a new HttpHealthCheck instance
         */
        fun create(
            url: URL,
            expectedStatusCode: Int = 200,
            method: String = "GET"
        ): HttpHealthCheck {
            return HttpHealthCheck(url, expectedStatusCode, method).apply {
                // Default to checking every minute
                schedulePeriod(1, TimeUnit.MINUTES)
            }
        }

        /**
         * Creates a new HttpHealthCheck with default configuration.
         *
         * @param urlString the URL string to check
         * @param expectedStatusCode the expected HTTP status code (default: 200)
         * @param method the HTTP method to use (default: GET)
         * @return a new HttpHealthCheck instance
         */
        fun create(
            urlString: String,
            expectedStatusCode: Int = 200,
            method: String = "GET"
        ): HttpHealthCheck {
            return create(URL(urlString), expectedStatusCode, method)
        }
    }
}
