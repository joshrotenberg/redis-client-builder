# Redis Client Builder

A fluent API for building Redis client instances in Kotlin with first-class Java support.

## Overview

Redis Client Builder is a library that simplifies the instantiation of Redis clients by providing a fluent, builder-style API. It supports multiple Redis client libraries:

- [Jedis](https://github.com/redis/jedis)
- [Lettuce](https://github.com/lettuce-io/lettuce-core)
- [Redisson](https://github.com/redisson/redisson)

The library provides a consistent API across all supported Redis client libraries, making it easy to switch between them without changing your configuration code.

## Features

- **Fluent API**: Build Redis client instances with a clean, readable syntax
- **Type Safety**: Leverage Kotlin's type system for safer code
- **Consistent Interface**: Use the same API pattern across different Redis client libraries
- **Java Interoperability**: First-class support for Java developers
- **Comprehensive Configuration**: Access all configuration options of the underlying client libraries

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

## Quick Start

### Jedis

```kotlin
// Kotlin
val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .build()
```

```java
// Java
JedisPool jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .build();
```

### UnifiedJedis

```kotlin
// Kotlin
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .build()
```

```java
// Java
UnifiedJedis unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .build();
```

### Lettuce

```kotlin
// Kotlin
val redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .password("password")
    .build()
```

```java
// Java
RedisClient redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .password("password")
    .build();
```

## Documentation

For more detailed information, check out the [Getting Started](getting-started.md) guide or the client-specific documentation:

- [Jedis](clients/jedis.md)
- [UnifiedJedis](clients/unified-jedis.md)
- [Lettuce](clients/lettuce.md)