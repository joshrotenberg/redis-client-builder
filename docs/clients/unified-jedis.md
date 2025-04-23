# UnifiedJedis Client Builder

The UnifiedJedisClientBuilder provides a fluent API for creating [UnifiedJedis](https://github.com/redis/jedis/blob/master/src/main/java/redis/clients/jedis/UnifiedJedis.java) instances from the Jedis library.

## Overview

UnifiedJedis is a unified client interface in Jedis that provides access to all Redis commands and features. It supports different connection modes and configuration options.

## Basic Usage

### Kotlin

```kotlin
// Create a UnifiedJedis instance with default settings
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis().build()

// Create a UnifiedJedis instance with custom settings
val customUnifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .host("redis-server")
    .port(6380)
    .password("secret")
    .database(1)
    .ssl(true)
    .build()
```

### Java

```java
// Create a UnifiedJedis instance with default settings
UnifiedJedis unifiedJedis = RedisClientBuilderFactory.unifiedJedis().build();

// Create a UnifiedJedis instance with custom settings
UnifiedJedis customUnifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .host("redis-server")
    .port(6380)
    .password("secret")
    .database(1)
    .ssl(true)
    .build();
```

## Connection Options

The UnifiedJedisClientBuilder supports two main connection modes:

1. **Direct Connection**: Connect directly to a Redis server
2. **URI-based Connection**: Connect using a Redis URI

### Direct Connection

```kotlin
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
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
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .uri("redis://user:password@localhost:6379/0")
    .build()
```

## Configuration Options

The UnifiedJedisClientBuilder supports all the configuration options available in UnifiedJedis:

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

UnifiedJedis can be instantiated in different ways, all of which are supported by the UnifiedJedisClientBuilder:

### 1. Direct Connection with Host and Port

```kotlin
// Using host and port
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .host("localhost")
    .port(6379)
    .build()
```

### 2. Connection with Client Configuration

```kotlin
// Using host, port, and client configuration
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .database(1)
    .connectionTimeout(3000)
    .socketTimeout(3000)
    .ssl(true)
    .build()
```

### 3. Connection with URI

```kotlin
// Using URI
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .uri("redis://user:password@localhost:6379/0")
    .build()
```

```kotlin
// Using URI with SSL
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .uri("rediss://user:password@localhost:6379/0")
    .build()
```

## Examples

### Basic Connection

```kotlin
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis().build()
unifiedJedis.set("key", "value")
val value = unifiedJedis.get("key")
println(value) // Outputs: value
unifiedJedis.close()
```

### Connection with Authentication

```kotlin
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .host("localhost")
    .port(6379)
    .user("default")
    .password("password")
    .build()
unifiedJedis.set("key", "value")
val value = unifiedJedis.get("key")
println(value) // Outputs: value
unifiedJedis.close()
```

### Connection with SSL

```kotlin
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .host("localhost")
    .port(6379)
    .ssl(true)
    .build()
unifiedJedis.set("key", "value")
val value = unifiedJedis.get("key")
println(value) // Outputs: value
unifiedJedis.close()
```

### Connection with URI

```kotlin
val unifiedJedis = RedisClientBuilderFactory.unifiedJedis()
    .uri("redis://localhost:6379/0")
    .build()
unifiedJedis.set("key", "value")
val value = unifiedJedis.get("key")
println(value) // Outputs: value
unifiedJedis.close()
```