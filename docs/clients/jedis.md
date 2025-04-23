# Jedis Client Builder

The JedisClientBuilder provides a fluent API for creating [JedisPool](https://github.com/redis/jedis/blob/master/src/main/java/redis/clients/jedis/JedisPool.java) instances from the Jedis library.

## Overview

JedisPool is a connection pool for Jedis clients that manages a pool of connections to Redis servers. It provides thread-safe access to Redis commands and features.

## Basic Usage

### Kotlin

```kotlin
// Create a JedisPool instance with default settings
val jedisPool = RedisClientBuilderFactory.jedis().build()

// Create a JedisPool instance with custom settings
val customJedisPool = RedisClientBuilderFactory.jedis()
    .host("redis-server")
    .port(6380)
    .password("secret")
    .database(1)
    .ssl(true)
    .build()
```

### Java

```java
// Create a JedisPool instance with default settings
JedisPool jedisPool = RedisClientBuilderFactory.jedis().build();

// Create a JedisPool instance with custom settings
JedisPool customJedisPool = RedisClientBuilderFactory.jedis()
    .host("redis-server")
    .port(6380)
    .password("secret")
    .database(1)
    .ssl(true)
    .build();
```

## Connection Options

The JedisClientBuilder supports two main connection modes:

1. **Direct Connection**: Connect directly to a Redis server
2. **Pool Configuration**: Configure the connection pool settings

### Direct Connection

```kotlin
val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .build()
```

```java
JedisPool jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .build();
```

### Pool Configuration

```kotlin
val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .maxTotal(100)
    .maxIdle(10)
    .minIdle(5)
    .testOnBorrow(true)
    .testOnReturn(true)
    .testWhileIdle(true)
    .timeBetweenEvictionRuns(30000)
    .blockWhenExhausted(true)
    .jmxEnabled(true)
    .build()
```

```java
JedisPool jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .maxTotal(100)
    .maxIdle(10)
    .minIdle(5)
    .testOnBorrow(true)
    .testOnReturn(true)
    .testWhileIdle(true)
    .timeBetweenEvictionRuns(30000)
    .blockWhenExhausted(true)
    .jmxEnabled(true)
    .build();
```

## Configuration Options

The JedisClientBuilder supports all the configuration options available in JedisPool:

### Basic Connection Settings

| Method | Description | Default Value |
|--------|-------------|---------------|
| `host(String)` | Sets the Redis host | "localhost" |
| `port(Int)` | Sets the Redis port | 6379 |
| `password(String)` | Sets the Redis password | null |
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

### Pool Settings

| Method | Description | Default Value |
|--------|-------------|---------------|
| `maxTotal(Int)` | Sets the maximum number of connections that can be allocated by the pool at a given time | 8 |
| `maxIdle(Int)` | Sets the maximum number of idle connections that can be maintained by the pool without being closed | 8 |
| `minIdle(Int)` | Sets the minimum number of idle connections to maintain in the pool | 0 |
| `testOnBorrow(Boolean)` | Sets whether connections should be validated before being borrowed from the pool | false |
| `testOnReturn(Boolean)` | Sets whether connections should be validated before being returned to the pool | false |
| `testWhileIdle(Boolean)` | Sets whether idle connections should be validated by the idle connection evictor | false |
| `timeBetweenEvictionRuns(Long)` | Sets the time between runs of the idle connection evictor thread in milliseconds | -1 |
| `blockWhenExhausted(Boolean)` | Sets whether clients should block when the pool is exhausted | true |
| `jmxEnabled(Boolean)` | Sets whether JMX should be enabled for the pool | true |

## Examples

### Basic Connection

#### Kotlin

```kotlin
val jedisPool = RedisClientBuilderFactory.jedis().build()
jedisPool.resource.use { jedis ->
    jedis.set("key", "value")
    val value = jedis.get("key")
    println(value) // Outputs: value
}
```

#### Java

```java
JedisPool jedisPool = RedisClientBuilderFactory.jedis().build();
try (Jedis jedis = jedisPool.getResource()) {
    jedis.set("key", "value");
    String value = jedis.get("key");
    System.out.println(value); // Outputs: value
}
```

### Connection with Authentication

#### Kotlin

```kotlin
val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .build()
jedisPool.resource.use { jedis ->
    jedis.set("key", "value")
    val value = jedis.get("key")
    println(value) // Outputs: value
}
```

#### Java

```java
JedisPool jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .build();
try (Jedis jedis = jedisPool.getResource()) {
    jedis.set("key", "value");
    String value = jedis.get("key");
    System.out.println(value); // Outputs: value
}
```

### Connection with SSL

#### Kotlin

```kotlin
val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .ssl(true)
    .build()
jedisPool.resource.use { jedis ->
    jedis.set("key", "value")
    val value = jedis.get("key")
    println(value) // Outputs: value
}
```

#### Java

```java
JedisPool jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .ssl(true)
    .build();
try (Jedis jedis = jedisPool.getResource()) {
    jedis.set("key", "value");
    String value = jedis.get("key");
    System.out.println(value); // Outputs: value
}
```

### Connection with Custom Pool Configuration

#### Kotlin

```kotlin
val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .maxTotal(50)
    .maxIdle(10)
    .minIdle(5)
    .testOnBorrow(true)
    .build()
jedisPool.resource.use { jedis ->
    jedis.set("key", "value")
    val value = jedis.get("key")
    println(value) // Outputs: value
}
```

#### Java

```java
JedisPool jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .maxTotal(50)
    .maxIdle(10)
    .minIdle(5)
    .testOnBorrow(true)
    .build();
try (Jedis jedis = jedisPool.getResource()) {
    jedis.set("key", "value");
    String value = jedis.get("key");
    System.out.println(value); // Outputs: value
}
```