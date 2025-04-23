# Lettuce Client Builder

The LettuceClientBuilder provides a fluent API for creating [RedisClient](https://github.com/lettuce-io/lettuce-core/blob/master/src/main/java/io/lettuce/core/RedisClient.java) instances from the Lettuce library.

## Overview

Lettuce is a scalable Redis client for Java and Kotlin. It provides synchronous, asynchronous, and reactive APIs for interacting with Redis servers. The LettuceClientBuilder simplifies the creation and configuration of Lettuce's RedisClient instances.

## Basic Usage

### Kotlin

```kotlin
// Create a RedisClient instance with default settings
val redisClient = RedisClientBuilderFactory.lettuce().build()

// Create a RedisClient instance with custom settings
val customRedisClient = RedisClientBuilderFactory.lettuce()
    .host("redis-server")
    .port(6380)
    .password("secret")
    .database(1)
    .ssl(true)
    .build()
```

### Java

```java
// Create a RedisClient instance with default settings
RedisClient redisClient = RedisClientBuilderFactory.lettuce().build();

// Create a RedisClient instance with custom settings
RedisClient customRedisClient = RedisClientBuilderFactory.lettuce()
    .host("redis-server")
    .port(6380)
    .password("secret")
    .database(1)
    .ssl(true)
    .build();
```

## Connection Options

The LettuceClientBuilder supports various connection options:

### Basic Connection

```kotlin
val redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .password("password")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
    .build()
```

```java
RedisClient redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .password("password")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .ssl(true)
    .build();
```

### Advanced Options

```kotlin
val redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .autoReconnect(true)
    .requestQueueSize(1000)
    .publishOnScheduler(true)
    .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
    .build()
```

```java
RedisClient redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .autoReconnect(true)
    .requestQueueSize(1000)
    .publishOnScheduler(true)
    .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
    .build();
```

## Configuration Options

The LettuceClientBuilder supports all the configuration options available in Lettuce's RedisClient:

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
| `connectionTimeout(Int)` | Sets the connection timeout in milliseconds | 60000 |
| `socketTimeout(Int)` | Sets the socket timeout in milliseconds | 60000 |

### SSL Settings

| Method | Description | Default Value |
|--------|-------------|---------------|
| `ssl(Boolean)` | Enables SSL/TLS for the connection | false |

### Lettuce-specific Settings

| Method | Description | Default Value |
|--------|-------------|---------------|
| `autoReconnect(Boolean)` | Sets whether the client should automatically reconnect | true |
| `requestQueueSize(Int)` | Sets the request queue size | Integer.MAX_VALUE |
| `publishOnScheduler(Boolean)` | Sets whether to publish on the scheduler | false |
| `disconnectedBehavior(ClientOptions.DisconnectedBehavior)` | Sets the disconnected behavior | DEFAULT |

## Examples

### Basic Connection

#### Kotlin

```kotlin
val redisClient = RedisClientBuilderFactory.lettuce().build()
val connection = redisClient.connect()
val commands = connection.sync()
commands.set("key", "value")
val value = commands.get("key")
println(value) // Outputs: value
connection.close()
redisClient.shutdown()
```

#### Java

```java
RedisClient redisClient = RedisClientBuilderFactory.lettuce().build();
StatefulRedisConnection<String, String> connection = redisClient.connect();
RedisCommands<String, String> commands = connection.sync();
commands.set("key", "value");
String value = commands.get("key");
System.out.println(value); // Outputs: value
connection.close();
redisClient.shutdown();
```

### Connection with Authentication

#### Kotlin

```kotlin
val redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .password("password")
    .build()
val connection = redisClient.connect()
val commands = connection.sync()
commands.set("key", "value")
val value = commands.get("key")
println(value) // Outputs: value
connection.close()
redisClient.shutdown()
```

#### Java

```java
RedisClient redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .password("password")
    .build();
StatefulRedisConnection<String, String> connection = redisClient.connect();
RedisCommands<String, String> commands = connection.sync();
commands.set("key", "value");
String value = commands.get("key");
System.out.println(value); // Outputs: value
connection.close();
redisClient.shutdown();
```

### Connection with SSL

#### Kotlin

```kotlin
val redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .ssl(true)
    .build()
val connection = redisClient.connect()
val commands = connection.sync()
commands.set("key", "value")
val value = commands.get("key")
println(value) // Outputs: value
connection.close()
redisClient.shutdown()
```

#### Java

```java
RedisClient redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .ssl(true)
    .build();
StatefulRedisConnection<String, String> connection = redisClient.connect();
RedisCommands<String, String> commands = connection.sync();
commands.set("key", "value");
String value = commands.get("key");
System.out.println(value); // Outputs: value
connection.close();
redisClient.shutdown();
```

### Using Asynchronous API

#### Kotlin

```kotlin
val redisClient = RedisClientBuilderFactory.lettuce().build()
val connection = redisClient.connect()
val asyncCommands = connection.async()
val setFuture = asyncCommands.set("key", "value")
setFuture.thenAccept { println("Set operation completed") }
val getFuture = asyncCommands.get("key")
getFuture.thenAccept { value -> println("Value: $value") }
// Wait for operations to complete
Thread.sleep(1000)
connection.close()
redisClient.shutdown()
```

#### Java

```java
RedisClient redisClient = RedisClientBuilderFactory.lettuce().build();
StatefulRedisConnection<String, String> connection = redisClient.connect();
RedisAsyncCommands<String, String> asyncCommands = connection.async();
RedisFuture<String> setFuture = asyncCommands.set("key", "value");
setFuture.thenAccept(result -> System.out.println("Set operation completed"));
RedisFuture<String> getFuture = asyncCommands.get("key");
getFuture.thenAccept(value -> System.out.println("Value: " + value));
// Wait for operations to complete
Thread.sleep(1000);
connection.close();
redisClient.shutdown();
```

### Using Reactive API

#### Kotlin

```kotlin
val redisClient = RedisClientBuilderFactory.lettuce().build()
val connection = redisClient.connect()
val reactiveCommands = connection.reactive()
reactiveCommands.set("key", "value").subscribe { println("Set operation completed") }
reactiveCommands.get("key").subscribe { value -> println("Value: $value") }
// Wait for operations to complete
Thread.sleep(1000)
connection.close()
redisClient.shutdown()
```

#### Java

```java
RedisClient redisClient = RedisClientBuilderFactory.lettuce().build();
StatefulRedisConnection<String, String> connection = redisClient.connect();
RedisReactiveCommands<String, String> reactiveCommands = connection.reactive();
reactiveCommands.set("key", "value").subscribe(result -> System.out.println("Set operation completed"));
reactiveCommands.get("key").subscribe(value -> System.out.println("Value: " + value));
// Wait for operations to complete
Thread.sleep(1000);
connection.close();
redisClient.shutdown();
```