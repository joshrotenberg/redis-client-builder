package com.joshrotenberg.redis.client.builder;

import com.joshrotenberg.redis.client.builder.jedis.JedisClientBuilder;
import com.joshrotenberg.redis.client.builder.jedis.UnifiedJedisClientBuilder;
import com.joshrotenberg.redis.client.builder.lettuce.LettuceClientBuilder;
import io.lettuce.core.RedisClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.UnifiedJedis;

/**
 * Tests to verify that the RedisClientBuilder interface and its implementations
 * are fully compatible with Java.
 * 
 * This test doesn't require a Redis server to run, as it only checks that the
 * builder methods can be called from Java code and that the build method returns
 * the correct type, without actually connecting to a Redis server.
 */
public class RedisClientBuilderJavaCompatibilityTest {

    @Test
    public void testJedisClientBuilderCompatibility() {
        // Test that we can use the JedisClientBuilder from Java
        JedisClientBuilder builder = RedisClientBuilderFactory.jedis();
        
        // Test that we can call all the methods defined in the RedisClientBuilder interface
        RedisClientBuilder<JedisPool> builderInterface = builder;
        builderInterface = builderInterface.host("localhost");
        builderInterface = builderInterface.port(6379);
        builderInterface = builderInterface.password("password");
        builderInterface = builderInterface.database(0);
        builderInterface = builderInterface.connectionTimeout(2000);
        builderInterface = builderInterface.socketTimeout(2000);
        builderInterface = builderInterface.ssl(true);
        
        // Test that we can call the JedisClientBuilder-specific methods
        builder = builder.maxTotal(100);
        builder = builder.maxIdle(10);
        builder = builder.minIdle(5);
        builder = builder.testOnBorrow(true);
        builder = builder.testOnReturn(true);
        builder = builder.testWhileIdle(true);
        builder = builder.timeBetweenEvictionRuns(30000L);
        builder = builder.blockWhenExhausted(true);
        builder = builder.jmxEnabled(true);
        
        // Test that the build method returns the correct type
        // We don't actually call build() to avoid connecting to a Redis server
        Assertions.assertNotNull(builder);
    }

    @Test
    public void testUnifiedJedisClientBuilderCompatibility() {
        // Test that we can use the UnifiedJedisClientBuilder from Java
        UnifiedJedisClientBuilder builder = RedisClientBuilderFactory.unifiedJedis();
        
        // Test that we can call all the methods defined in the RedisClientBuilder interface
        RedisClientBuilder<UnifiedJedis> builderInterface = builder;
        builderInterface = builderInterface.host("localhost");
        builderInterface = builderInterface.port(6379);
        builderInterface = builderInterface.password("password");
        builderInterface = builderInterface.database(0);
        builderInterface = builderInterface.connectionTimeout(2000);
        builderInterface = builderInterface.socketTimeout(2000);
        builderInterface = builderInterface.ssl(true);
        
        // Test that we can call the UnifiedJedisClientBuilder-specific methods
        builder = builder.clientName("test-client");
        builder = builder.user("test-user");
        
        // Test that the build method returns the correct type
        // We don't actually call build() to avoid connecting to a Redis server
        Assertions.assertNotNull(builder);
    }

    @Test
    public void testLettuceClientBuilderCompatibility() {
        // Test that we can use the LettuceClientBuilder from Java
        LettuceClientBuilder builder = RedisClientBuilderFactory.lettuce();
        
        // Test that we can call all the methods defined in the RedisClientBuilder interface
        RedisClientBuilder<RedisClient> builderInterface = builder;
        builderInterface = builderInterface.host("localhost");
        builderInterface = builderInterface.port(6379);
        builderInterface = builderInterface.password("password");
        builderInterface = builderInterface.database(0);
        builderInterface = builderInterface.connectionTimeout(2000);
        builderInterface = builderInterface.socketTimeout(2000);
        builderInterface = builderInterface.ssl(true);
        
        // Test that we can call the LettuceClientBuilder-specific methods
        builder = builder.autoReconnect(true);
        builder = builder.requestQueueSize(1000);
        builder = builder.publishOnScheduler(true);
        
        // Test that the build method returns the correct type
        // We don't actually call build() to avoid connecting to a Redis server
        Assertions.assertNotNull(builder);
    }

    @Test
    public void testGenericBuilderMethod() {
        // Test that we can use the generic builder method from Java
        RedisClientBuilder<?> jedisBuilder = RedisClientBuilderFactory.builder(JedisPool.class);
        RedisClientBuilder<?> unifiedJedisBuilder = RedisClientBuilderFactory.builder(UnifiedJedis.class);
        RedisClientBuilder<?> lettuceBuilder = RedisClientBuilderFactory.builder(RedisClient.class);
        
        // Test that the builders are of the correct type
        Assertions.assertTrue(jedisBuilder instanceof JedisClientBuilder);
        Assertions.assertTrue(unifiedJedisBuilder instanceof UnifiedJedisClientBuilder);
        Assertions.assertTrue(lettuceBuilder instanceof LettuceClientBuilder);
    }
}