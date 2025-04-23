# Getting Started with Redis Client Builder

This guide will help you get started with Redis Client Builder, a fluent API for building Redis client instances in Kotlin with first-class Java support.

## Prerequisites

- Java 17 or higher
- Kotlin 1.8 or higher (for Kotlin projects)
- A Redis server (for running examples)

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.joshrotenberg:redis-client-builder:1.0-SNAPSHOT")
}
```

### Gradle (Groovy DSL)

```groovy
dependencies {
    implementation 'com.joshrotenberg:redis-client-builder:1.0-SNAPSHOT'
}
```

### Maven

```xml
<dependency>
    <groupId>com.joshrotenberg</groupId>
    <artifactId>redis-client-builder</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Basic Usage

Redis Client Builder provides a consistent API for creating instances of different Redis client libraries. The main entry point is the `RedisClientBuilderFactory` class, which provides methods for creating builders for each supported client library.

### Creating a Jedis Client

```kotlin
// Kotlin
val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .build()

// Use the JedisPool
jedisPool.resource.use { jedis ->
    jedis.set("key", "value")
    val value = jedis.get("key")
    println(value)
}
```

```java
// Java
JedisPool jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .build();

// Use the JedisPool
try (Jedis jedis = jedisPool.getResource()) {
    jedis.set("key", "value");
    String value = jedis.get("key");
    System.out.println(value);
}
```

### Creating a UnifiedJedis Client

```kotlin
// Kotlin
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .build()

// Use the UnifiedJedis instance
unifiedJedis.use { jedis ->
    jedis.set("key", "value")
    val value = jedis.get("key")
    println(value)
}
```

```java
// Java
UnifiedJedis unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .build();

// Use the UnifiedJedis instance
try {
    unifiedJedis.set("key", "value");
    String value = unifiedJedis.get("key");
    System.out.println(value);
} finally {
    unifiedJedis.close();
}
```

### Creating a Lettuce Client

```kotlin
// Kotlin
val redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .password("password")
    .build()

// Use the RedisClient
val connection = redisClient.connect()
val commands = connection.sync()
commands.set("key", "value")
val value = commands.get("key")
println(value)
connection.close()
redisClient.shutdown()
```

```java
// Java
RedisClient redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .password("password")
    .build();

// Use the RedisClient
StatefulRedisConnection<String, String> connection = redisClient.connect();
RedisCommands<String, String> commands = connection.sync();
commands.set("key", "value");
String value = commands.get("key");
System.out.println(value);
connection.close();
redisClient.shutdown();
```

## Next Steps

For more detailed information about each client library, check out the client-specific documentation:

- [Jedis](clients/jedis.md)
- [UnifiedJedis](clients/unified-jedis.md)
- [Lettuce](clients/lettuce.md)

For a complete reference of all available methods and options, see the [API Reference](api-reference.md).