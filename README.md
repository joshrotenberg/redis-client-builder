# Redis Client Builder

A fluent API for building Redis client instances in Kotlin with first-class Java support.

## Overview

Redis Client Builder is a library that simplifies the instantiation of Redis clients by providing a fluent, builder-style API. It supports multiple Redis client libraries:

- [Jedis](https://github.com/redis/jedis)
- [Lettuce](https://github.com/lettuce-io/lettuce-core)
- [Redisson](https://github.com/redisson/redisson)

The library provides a consistent API across all supported Redis client libraries, making it easy to switch between them without changing your configuration code.

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

## Usage

### Jedis

```kotlin
// Kotlin
val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
    .maxTotal(100)
    .maxIdle(10)
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
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
    .maxTotal(100)
    .maxIdle(10)
    .build();

// Use the JedisPool
try (Jedis jedis = jedisPool.getResource()) {
    jedis.set("key", "value");
    String value = jedis.get("key");
    System.out.println(value);
}
```

### UnifiedJedis

```kotlin
// Kotlin
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
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
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
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

### Lettuce

```kotlin
// Kotlin
val redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .password("password")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
    .autoReconnect(true)
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
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
    .autoReconnect(true)
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

### Redisson

```kotlin
// Kotlin
val redissonClient = RedisClientBuilderFactory.redisson()
    .host("localhost")
    .port(6379)
    .password("password")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
    .retryAttempts(3)
    .retryInterval(1500)
    .build()

// Use the RedissonClient
val bucket = redissonClient.getBucket<String>("key")
bucket.set("value")
val value = bucket.get()
println(value)
redissonClient.shutdown()
```

```java
// Java
RedissonClient redissonClient = RedisClientBuilderFactory.redisson()
    .host("localhost")
    .port(6379)
    .password("password")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
    .retryAttempts(3)
    .retryInterval(1500)
    .build();

// Use the RedissonClient
RBucket<String> bucket = redissonClient.getBucket("key");
bucket.set("value");
String value = bucket.get();
System.out.println(value);
redissonClient.shutdown();
```

### Generic Builder

You can also use the generic builder method to create a builder for a specific Redis client type:

```kotlin
// Kotlin
val jedisBuilder = RedisClientBuilderFactory.builder(JedisPool::class.java) as JedisClientBuilder
val lettuceBuilder = RedisClientBuilderFactory.builder(RedisClient::class.java) as LettuceClientBuilder
val redissonBuilder = RedisClientBuilderFactory.builder(RedissonClient::class.java) as RedissonClientBuilder
```

```java
// Java
JedisClientBuilder jedisBuilder = (JedisClientBuilder) RedisClientBuilderFactory.builder(JedisPool.class);
LettuceClientBuilder lettuceBuilder = (LettuceClientBuilder) RedisClientBuilderFactory.builder(RedisClient.class);
RedissonClientBuilder redissonBuilder = (RedissonClientBuilder) RedisClientBuilderFactory.builder(RedissonClient.class);
```

## Documentation

For more detailed information, check out the [documentation site](https://joshrotenberg.github.io/redis-client-builder/):

- [Getting Started](https://joshrotenberg.github.io/redis-client-builder/getting-started/)
- [Jedis Client](https://joshrotenberg.github.io/redis-client-builder/clients/jedis/)
- [UnifiedJedis Client](https://joshrotenberg.github.io/redis-client-builder/clients/unified-jedis/)
- [Lettuce Client](https://joshrotenberg.github.io/redis-client-builder/clients/lettuce/)
- [API Reference](https://joshrotenberg.github.io/redis-client-builder/api-reference/)

To build and run the documentation site locally:

```bash
# Install mkdocs and the material theme
pip install mkdocs mkdocs-material

# Serve the documentation site locally
mkdocs serve
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
