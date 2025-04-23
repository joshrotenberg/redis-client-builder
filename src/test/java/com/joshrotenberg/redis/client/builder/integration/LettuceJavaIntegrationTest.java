package com.joshrotenberg.redis.client.builder.integration;

import com.joshrotenberg.redis.client.builder.RedisClientBuilderFactory;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests for the LettuceClientBuilder using Java.
 * This test verifies that the Java examples in the documentation work correctly.
 * 
 * This test extends JavaRedisContainerTest to use its Redis container.
 */
@Testcontainers
public class LettuceJavaIntegrationTest extends JavaRedisContainerTest {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> syncCommands;

    @BeforeEach
    public void setup() {
        // Create a RedisClient using the builder with the container's host and port
        redisClient = RedisClientBuilderFactory.lettuce()
                .host(JavaRedisContainerTest.getRedisHost())
                .port(JavaRedisContainerTest.getRedisPort())
                .build();

        // Get a connection and commands
        connection = redisClient.connect();
        syncCommands = connection.sync();
    }

    @AfterEach
    public void tearDown() {
        connection.close();
        redisClient.shutdown();
    }

    @Test
    public void testLettuceConnection() {
        // Test basic Redis operations

        // Test SET and GET
        String key = "test:lettuce:key:java";
        String value = "Hello, Lettuce from Java!";
        syncCommands.set(key, value);
        String retrievedValue = syncCommands.get(key);
        Assertions.assertEquals(value, retrievedValue, "Retrieved value should match the set value");

        // Test EXISTS
        Assertions.assertTrue(syncCommands.exists(key) > 0, "Key should exist after setting a value");

        // Test DEL
        syncCommands.del(key);
        Assertions.assertEquals(0, syncCommands.exists(key), "Key should not exist after deletion");

        // Test INCR
        String counterKey = "test:lettuce:counter:java";
        syncCommands.set(counterKey, "0");
        long newValue = syncCommands.incr(counterKey);
        Assertions.assertEquals(1, newValue, "Counter should be incremented to 1");

        // Test EXPIRE and TTL
        syncCommands.expire(counterKey, 10);
        long ttl = syncCommands.ttl(counterKey);
        Assertions.assertTrue(ttl > 0 && ttl <= 10, "TTL should be between 0 and 10 seconds");

        // Clean up
        syncCommands.del(counterKey);
    }

    @Test
    public void testLettuceClientWithCustomConfiguration() {
        // Close the default connection and client
        connection.close();
        redisClient.shutdown();

        // Create a RedisClient with custom configuration
        redisClient = RedisClientBuilderFactory.lettuce()
                .host(JavaRedisContainerTest.getRedisHost())
                .port(JavaRedisContainerTest.getRedisPort())
                .connectionTimeout(5000)
                .socketTimeout(3000)
                .autoReconnect(false)
                .requestQueueSize(1000)
                .publishOnScheduler(true)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .build();

        // Get a new connection and commands
        connection = redisClient.connect();
        syncCommands = connection.sync();

        // Verify that we can use the connection
        String key = "test:lettuce:custom:config:java";
        String value = "Custom configuration works from Java!";
        syncCommands.set(key, value);
        String retrievedValue = syncCommands.get(key);
        Assertions.assertEquals(value, retrievedValue);

        // Clean up
        syncCommands.del(key);
    }
}