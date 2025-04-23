package com.joshrotenberg.redis.client.builder.integration

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
abstract class RedisContainerTest {
    companion object {
        // Define the Redis container
        @Container
        @JvmStatic
        val redisContainer: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)

        @JvmStatic
        lateinit var redisHost: String
            private set

            @JvmStatic
            get

        @JvmStatic
        var redisPort: Int = 0
            private set

            @JvmStatic
            get

        @BeforeAll
        @JvmStatic
        fun startContainer() {
            redisContainer.start()
            redisHost = redisContainer.host
            redisPort = redisContainer.getMappedPort(6379)
            println("Redis container started at $redisHost:$redisPort")
        }

        @AfterAll
        @JvmStatic
        fun stopContainer() {
            redisContainer.stop()
        }
    }
}
