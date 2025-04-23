# Comparing Redis Client Builder vs Direct Instantiation

This document compares the usage of Redis Client Builder with direct instantiation of Redis clients, focusing on Jedis as an example.

## Complex Jedis Instantiation Example

### Direct Jedis Instantiation

When using Jedis directly, you need to manually configure the JedisPool and its underlying JedisPoolConfig. This requires multiple steps and detailed knowledge of the Jedis API:

```java
// Java
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Jedis;
import java.time.Duration;

// Create and configure JedisPoolConfig
JedisPoolConfig poolConfig = new JedisPoolConfig();
poolConfig.setMaxTotal(100);
poolConfig.setMaxIdle(20);
poolConfig.setMinIdle(10);
poolConfig.setTestOnBorrow(true);
poolConfig.setTestOnReturn(true);
poolConfig.setTestWhileIdle(true);
poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(30000));
poolConfig.setBlockWhenExhausted(true);
poolConfig.setJmxEnabled(true);

// Create JedisPool with the configured poolConfig
JedisPool jedisPool = new JedisPool(
    poolConfig,
    "redis-server.example.com",
    6379,
    2000, // connection timeout
    2000, // socket timeout
    "password123",
    0,    // database
    null, // client name
    true, // SSL
    null, // SSL socket factory
    null, // SSL parameters
    null  // hostname verifier
);

// Use the JedisPool
try (Jedis jedis = jedisPool.getResource()) {
    jedis.set("key", "value");
    String value = jedis.get("key");
    System.out.println(value);
}
```

```kotlin
// Kotlin
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.time.Duration

// Create and configure JedisPoolConfig
val poolConfig = JedisPoolConfig().apply {
    maxTotal = 100
    maxIdle = 20
    minIdle = 10
    testOnBorrow = true
    testOnReturn = true
    testWhileIdle = true
    timeBetweenEvictionRuns = Duration.ofMillis(30000)
    blockWhenExhausted = true
    jmxEnabled = true
}

// Create JedisPool with the configured poolConfig
val jedisPool = JedisPool(
    poolConfig,
    "redis-server.example.com",
    6379,
    2000, // connection timeout
    2000, // socket timeout
    "password123",
    0,    // database
    null, // client name
    true, // SSL
    null, // SSL socket factory
    null, // SSL parameters
    null  // hostname verifier
)

// Use the JedisPool
jedisPool.resource.use { jedis ->
    jedis.set("key", "value")
    val value = jedis.get("key")
    println(value)
}
```

### Using Redis Client Builder

With Redis Client Builder, the same configuration can be achieved with a more concise, readable, and fluent API:

```java
// Java
import com.joshrotenberg.redis.client.builder.RedisClientBuilderFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;

// Create JedisPool with the same configuration using the builder
JedisPool jedisPool = RedisClientBuilderFactory.jedis()
    .host("redis-server.example.com")
    .port(6379)
    .password("password123")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
    .maxTotal(100)
    .maxIdle(20)
    .minIdle(10)
    .testOnBorrow(true)
    .testOnReturn(true)
    .testWhileIdle(true)
    .timeBetweenEvictionRuns(30000)
    .blockWhenExhausted(true)
    .jmxEnabled(true)
    .build();

// Use the JedisPool
try (Jedis jedis = jedisPool.getResource()) {
    jedis.set("key", "value");
    String value = jedis.get("key");
    System.out.println(value);
}
```

```kotlin
// Kotlin
import com.joshrotenberg.redis.client.builder.RedisClientBuilderFactory
import redis.clients.jedis.JedisPool

// Create JedisPool with the same configuration using the builder
val jedisPool = RedisClientBuilderFactory.jedis()
    .host("redis-server.example.com")
    .port(6379)
    .password("password123")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
    .maxTotal(100)
    .maxIdle(20)
    .minIdle(10)
    .testOnBorrow(true)
    .testOnReturn(true)
    .testWhileIdle(true)
    .timeBetweenEvictionRuns(30000)
    .blockWhenExhausted(true)
    .jmxEnabled(true)
    .build()

// Use the JedisPool
jedisPool.resource.use { jedis ->
    jedis.set("key", "value")
    val value = jedis.get("key")
    println(value)
}
```

## Benefits of Using Redis Client Builder

1. **Simplified API**: The builder pattern provides a more intuitive and readable way to configure Redis clients.

2. **Reduced Boilerplate**: No need to create and configure separate objects like JedisPoolConfig.

3. **Type Safety**: The builder methods provide type safety and IDE auto-completion.

4. **Consistent API**: The same builder pattern is used across different Redis client libraries (Jedis, Lettuce), making it easier to switch between them.

5. **Default Values**: Sensible defaults are provided for all configuration options, so you only need to specify the values you want to change.

6. **Encapsulation**: The builder encapsulates the complexity of creating and configuring Redis clients, making your code more maintainable.

7. **Fluent Interface**: The fluent interface allows for method chaining, making the code more concise and readable.

## When to Use Redis Client Builder

Redis Client Builder is particularly useful in the following scenarios:

1. **Complex Configurations**: When you need to configure many options for your Redis client.

2. **Multiple Redis Clients**: When you need to create multiple Redis clients with similar configurations.

3. **Switching Between Client Libraries**: When you want to easily switch between different Redis client libraries.

4. **Readability and Maintainability**: When you want to make your code more readable and maintainable.

5. **Standardization**: When you want to standardize how Redis clients are created across your codebase or organization.

## Conclusion

Redis Client Builder provides a more elegant and maintainable way to create and configure Redis clients compared to direct instantiation. It simplifies the API, reduces boilerplate code, and provides a consistent interface across different Redis client libraries.