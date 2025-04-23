# JedisPooled Client Builder

The JedisPooledClientBuilder provides a fluent API for creating [JedisPooled](https://github.com/redis/jedis/blob/master/src/main/java/redis/clients/jedis/JedisPooled.java) instances from the Jedis library.

## Overview

JedisPooled is a client interface in Jedis that provides access to Redis commands and features with built-in connection pooling. It simplifies the use of Redis by handling connection management internally.

## Basic Usage

### Kotlin

```kotlin
// Create a JedisPooled instance with default settings
val jedisPooled = RedisClientBuilderFactory.jedisPooled().build()

// Create a JedisPooled instance with custom settings
val customJedisPooled = RedisClientBuilderFactory.jedisPooled()
    .host("redis-server")
    .port(6380)
    .password("secret")
    .database(1)
    .ssl(true)
    .build()
```

### Java

```java
// Create a JedisPooled instance with default settings
JedisPooled jedisPooled = RedisClientBuilderFactory.jedisPooled().build();

// Create a JedisPooled instance with custom settings
JedisPooled customJedisPooled = RedisClientBuilderFactory.jedisPooled()
    .host("redis-server")
    .port(6380)
    .password("secret")
    .database(1)
    .ssl(true)
    .build();
```

## Connection Options

The JedisPooledClientBuilder supports two main connection modes:

1. **Direct Connection**: Connect directly to a Redis server
2. **URI-based Connection**: Connect using a Redis URI

### Direct Connection

```kotlin
val jedisPooled = RedisClientBuilderFactory.jedisPooled()
    .host("localhost")
    .port(6379)
    .password("password")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .build()
```

### URI-based Connection

```kotlin
val jedisPooled = RedisClientBuilderFactory.jedisPooled()
    .uri("redis://user:password@localhost:6379/0")
    .build()
```

## Configuration Options

The JedisPooledClientBuilder supports all the configuration options available in JedisPooled:

### Basic Connection Settings

| Method | Description | Default Value |
|--------|-------------|---------------|
| `host(String)` | Sets the Redis host | "localhost" |
| `port(Int)` | Sets the Redis port | 6379 |
| `password(String)` | Sets the Redis password | null |
| `user(String)` | Sets the Redis user for ACL authentication | null |
| `database(Int)` | Sets the Redis database index | 0 |

### Timeout Settings

| Method | Description | Default Value |
|--------|-------------|---------------|
| `connectionTimeout(Int)` | Sets the connection timeout in milliseconds | 2000 |
| `socketTimeout(Int)` | Sets the socket timeout in milliseconds | 2000 |

### SSL Settings

| Method | Description | Default Value |
|--------|-------------|---------------|
| `ssl(Boolean)` | Enables SSL/TLS for the connection | false |
| `sslSocketFactory(SSLSocketFactory)` | Sets the SSL socket factory | null |
| `sslParameters(SSLParameters)` | Sets the SSL parameters | null |
| `hostnameVerifier(HostnameVerifier)` | Sets the hostname verifier | null |

### Other Settings

| Method | Description | Default Value |
|--------|-------------|---------------|
| `clientName(String)` | Sets the client name | null |
| `uri(URI)` | Sets the Redis URI | null |
| `uri(String)` | Sets the Redis URI as a string | null |

## Instantiation Options

JedisPooled can be instantiated in different ways, all of which are supported by the JedisPooledClientBuilder:

### Direct Connection

```kotlin
// Create a JedisPooled instance with a direct connection
val jedisPooled = RedisClientBuilderFactory.jedisPooled()
    .host("localhost")
    .port(6379)
    .build()
```

### URI-based Connection

```kotlin
// Create a JedisPooled instance with a URI
val jedisPooled = RedisClientBuilderFactory.jedisPooled()
    .uri("redis://localhost:6379/0")
    .build()
```

## Usage Examples

### Basic Operations

```kotlin
// Create a JedisPooled instance
val jedisPooled = RedisClientBuilderFactory.jedisPooled()
    .host("localhost")
    .port(6379)
    .build()

// Use the JedisPooled instance
jedisPooled.use { jedis ->
    // Set a key
    jedis.set("key", "value")

    // Get a key
    val value = jedis.get("key")

    // Delete a key
    jedis.del("key")

    // Check if a key exists
    val exists = jedis.exists("key")

    // Increment a counter
    val newValue = jedis.incr("counter")

    // Set a key with expiration
    jedis.setex("expiring-key", 60, "value")

    // Get the TTL of a key
    val ttl = jedis.ttl("expiring-key")
}
```

### Java Example

```java
import com.joshrotenberg.redis.client.builder.RedisClientBuilderFactory;
import redis.clients.jedis.JedisPooled;

public class JedisPooledExample {
    public static void main(String[] args) {
        // Create a JedisPooled instance
        JedisPooled jedisPooled = RedisClientBuilderFactory.jedisPooled()
            .host("localhost")
            .port(6379)
            .build();

        try {
            // Set a key
            jedisPooled.set("key", "value");

            // Get a key
            String value = jedisPooled.get("key");

            // Delete a key
            jedisPooled.del("key");

            // Check if a key exists
            boolean exists = jedisPooled.exists("key");

            // Increment a counter
            long newValue = jedisPooled.incr("counter");

            // Set a key with expiration
            jedisPooled.setex("expiring-key", 60, "value");

            // Get the TTL of a key
            long ttl = jedisPooled.ttl("expiring-key");
        } finally {
            // Close the connection when done
            jedisPooled.close();
        }
    }
}
```

## Comparison with Other Jedis Clients

### JedisPooled vs JedisPool

JedisPooled provides a simplified API compared to JedisPool. With JedisPool, you need to explicitly get and return Jedis instances from the pool:

```kotlin
// Using JedisPool
val jedisPool = RedisClientBuilderFactory.jedis().build()
jedisPool.resource.use { jedis ->
    jedis.set("key", "value")
}

// Using JedisPooled
val jedisPooled = RedisClientBuilderFactory.jedisPooled().build()
jedisPooled.set("key", "value")
```

### JedisPooled vs UnifiedJedis

JedisPooled and UnifiedJedis provide similar APIs, but JedisPooled includes built-in connection pooling, while UnifiedJedis does not:

```kotlin
// Using UnifiedJedis
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis().build()
unifiedJedis.set("key", "value")

// Using JedisPooled
val jedisPooled = RedisClientBuilderFactory.jedisPooled().build()
jedisPooled.set("key", "value")
```

## When to Use JedisPooled

JedisPooled is a good choice when:

1. You want a simple API for Redis operations
2. You need connection pooling for better performance
3. You don't need to manage connections manually

If you need more control over connection management, consider using JedisPool instead.
