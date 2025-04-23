package com.joshrotenberg.redis.client.builder.integration;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Base class for Java integration tests that need a Redis container.
 * This class provides a Redis container for testing.
 */
@Testcontainers
public class JavaRedisContainerTest {

    // Define the Redis container
    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    private static String redisHost;
    private static int redisPort;

    @BeforeAll
    public static void startContainer() {
        redisContainer.start();
        redisHost = redisContainer.getHost();
        redisPort = redisContainer.getMappedPort(6379);
        System.out.println("Redis container started at " + redisHost + ":" + redisPort);
    }

    /**
     * Get the Redis host from the container.
     * 
     * @return The Redis host
     */
    public static String getRedisHost() {
        return redisHost;
    }

    /**
     * Get the Redis port from the container.
     * 
     * @return The Redis port
     */
    public static int getRedisPort() {
        return redisPort;
    }
}
