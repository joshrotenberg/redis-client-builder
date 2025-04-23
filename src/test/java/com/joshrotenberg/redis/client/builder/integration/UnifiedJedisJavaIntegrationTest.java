package com.joshrotenberg.redis.client.builder.integration;

import com.joshrotenberg.redis.client.builder.RedisClientBuilderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.UnifiedJedis;

/**
 * Integration tests for the UnifiedJedisClientBuilder using Java.
 * This test verifies that the Java examples in the documentation work correctly.
 * 
 * This test extends JavaRedisContainerTest to use its Redis container.
 */
@Testcontainers
public class UnifiedJedisJavaIntegrationTest extends JavaRedisContainerTest {

    @Test
    public void testUnifiedJedisConnection() {
        // Create a UnifiedJedis instance using the builder with the container's host and port
        UnifiedJedis unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
                .host(JavaRedisContainerTest.getRedisHost())
                .port(JavaRedisContainerTest.getRedisPort())
                .build();

        // Use the UnifiedJedis instance to perform operations
        try (unifiedJedis) {
            // Test basic Redis operations

            // Test SET and GET
            String key = "test:unified:key:java";
            String value = "Hello, UnifiedJedis from Java!";
            unifiedJedis.set(key, value);
            String retrievedValue = unifiedJedis.get(key);
            Assertions.assertEquals(value, retrievedValue, "Retrieved value should match the set value");

            // Test EXISTS
            Assertions.assertTrue(unifiedJedis.exists(key), "Key should exist after setting a value");

            // Test DEL
            unifiedJedis.del(key);
            Assertions.assertFalse(unifiedJedis.exists(key), "Key should not exist after deletion");

            // Test INCR
            String counterKey = "test:unified:counter:java";
            unifiedJedis.set(counterKey, "0");
            long newValue = unifiedJedis.incr(counterKey);
            Assertions.assertEquals(1, newValue, "Counter should be incremented to 1");

            // Test EXPIRE and TTL
            unifiedJedis.expire(counterKey, 10);
            long ttl = unifiedJedis.ttl(counterKey);
            Assertions.assertTrue(ttl > 0 && ttl <= 10, "TTL should be between 0 and 10 seconds");

            // Clean up
            unifiedJedis.del(counterKey);
        }
    }

    @Test
    public void testUnifiedJedisWithCustomConfiguration() {
        // Create a UnifiedJedis instance with custom configuration
        UnifiedJedis unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
                .host(JavaRedisContainerTest.getRedisHost())
                .port(JavaRedisContainerTest.getRedisPort())
                .connectionTimeout(5000)
                .socketTimeout(3000)
                .clientName("test-client")
                .build();

        // Verify that we can use the UnifiedJedis instance
        try (unifiedJedis) {
            String key = "test:unified:custom:config:java";
            String value = "Custom configuration works with UnifiedJedis from Java!";
            unifiedJedis.set(key, value);
            String retrievedValue = unifiedJedis.get(key);
            Assertions.assertEquals(value, retrievedValue);

            // Clean up
            unifiedJedis.del(key);
        }
    }
}
