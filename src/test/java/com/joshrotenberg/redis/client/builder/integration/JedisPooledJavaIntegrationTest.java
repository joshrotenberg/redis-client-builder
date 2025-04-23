package com.joshrotenberg.redis.client.builder.integration;

import com.joshrotenberg.redis.client.builder.RedisClientBuilderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.JedisPooled;

/**
 * Integration tests for the JedisPooledClientBuilder using Java.
 * This test verifies that the Java examples in the documentation work correctly.
 * 
 * This test extends JavaRedisContainerTest to use its Redis container.
 */
@Testcontainers
public class JedisPooledJavaIntegrationTest extends JavaRedisContainerTest {

    @Test
    public void testJedisPooledConnection() {
        // Create a JedisPooled instance using the builder with the container's host and port
        JedisPooled jedisPooled = RedisClientBuilderFactory.jedisPooled()
                .host(JavaRedisContainerTest.getRedisHost())
                .port(JavaRedisContainerTest.getRedisPort())
                .build();

        // Use the JedisPooled instance to perform operations
        try (jedisPooled) {
            // Test basic Redis operations

            // Test SET and GET
            String key = "test:jedispooled:key:java";
            String value = "Hello, JedisPooled from Java!";
            jedisPooled.set(key, value);
            String retrievedValue = jedisPooled.get(key);
            Assertions.assertEquals(value, retrievedValue, "Retrieved value should match the set value");

            // Test EXISTS
            Assertions.assertTrue(jedisPooled.exists(key), "Key should exist after setting a value");

            // Test DEL
            jedisPooled.del(key);
            Assertions.assertFalse(jedisPooled.exists(key), "Key should not exist after deletion");

            // Test INCR
            String counterKey = "test:jedispooled:counter:java";
            jedisPooled.set(counterKey, "0");
            long newValue = jedisPooled.incr(counterKey);
            Assertions.assertEquals(1, newValue, "Counter should be incremented to 1");

            // Test EXPIRE and TTL
            jedisPooled.expire(counterKey, 10);
            long ttl = jedisPooled.ttl(counterKey);
            Assertions.assertTrue(ttl > 0 && ttl <= 10, "TTL should be between 0 and 10 seconds");

            // Clean up
            jedisPooled.del(counterKey);
        }
    }

    @Test
    public void testJedisPooledWithCustomConfiguration() {
        // Create a JedisPooled instance with custom configuration
        JedisPooled jedisPooled = RedisClientBuilderFactory.jedisPooled()
                .host(JavaRedisContainerTest.getRedisHost())
                .port(JavaRedisContainerTest.getRedisPort())
                .connectionTimeout(5000)
                .socketTimeout(3000)
                .clientName("test-client")
                .build();

        // Verify that we can use the JedisPooled instance
        try (jedisPooled) {
            String key = "test:jedispooled:custom:config:java";
            String value = "Custom configuration works with JedisPooled from Java!";
            jedisPooled.set(key, value);
            String retrievedValue = jedisPooled.get(key);
            Assertions.assertEquals(value, retrievedValue);

            // Clean up
            jedisPooled.del(key);
        }
    }
}