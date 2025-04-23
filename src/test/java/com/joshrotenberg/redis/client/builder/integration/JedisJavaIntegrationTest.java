package com.joshrotenberg.redis.client.builder.integration;

import com.joshrotenberg.redis.client.builder.RedisClientBuilderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Integration tests for the JedisClientBuilder using Java.
 * This test verifies that the Java examples in the documentation work correctly.
 * 
 * This test extends JavaRedisContainerTest to use its Redis container.
 */
@Testcontainers
public class JedisJavaIntegrationTest extends JavaRedisContainerTest {

    @Test
    public void testJedisConnection() {
        // Create a JedisPool using the builder with the container's host and port
        JedisPool jedisPool = RedisClientBuilderFactory.jedis()
                .host(JavaRedisContainerTest.getRedisHost())
                .port(JavaRedisContainerTest.getRedisPort())
                .build();

        // Use the JedisPool to get a Jedis instance
        try (Jedis jedis = jedisPool.getResource()) {
            // Test basic Redis operations

            // Test SET and GET
            String key = "test:key:java";
            String value = "Hello, Redis from Java!";
            jedis.set(key, value);
            String retrievedValue = jedis.get(key);
            Assertions.assertEquals(value, retrievedValue, "Retrieved value should match the set value");

            // Test EXISTS
            Assertions.assertTrue(jedis.exists(key), "Key should exist after setting a value");

            // Test DEL
            jedis.del(key);
            Assertions.assertFalse(jedis.exists(key), "Key should not exist after deletion");

            // Test INCR
            String counterKey = "test:counter:java";
            jedis.set(counterKey, "0");
            long newValue = jedis.incr(counterKey);
            Assertions.assertEquals(1, newValue, "Counter should be incremented to 1");

            // Test EXPIRE and TTL
            jedis.expire(counterKey, 10);
            long ttl = jedis.ttl(counterKey);
            Assertions.assertTrue(ttl > 0 && ttl <= 10, "TTL should be between 0 and 10 seconds");

            // Clean up
            jedis.del(counterKey);
        }
    }

    @Test
    public void testJedisPoolWithCustomConfiguration() {
        // Create a JedisPool with custom configuration
        JedisPool jedisPool = RedisClientBuilderFactory.jedis()
                .host(JavaRedisContainerTest.getRedisHost())
                .port(JavaRedisContainerTest.getRedisPort())
                .connectionTimeout(5000)
                .socketTimeout(3000)
                .maxTotal(50)
                .maxIdle(10)
                .minIdle(5)
                .build();

        // Verify that we can get a resource from the pool and use it
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "test:custom:config:java";
            String value = "Custom configuration works from Java!";
            jedis.set(key, value);
            String retrievedValue = jedis.get(key);
            Assertions.assertEquals(value, retrievedValue);

            // Clean up
            jedis.del(key);
        }
    }
}
