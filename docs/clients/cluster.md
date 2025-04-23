# Redis Cluster Support

Redis Client Builder provides support for Redis Cluster configuration, allowing you to build Redis clients that connect to Redis Cluster for high availability and sharding.

## What is Redis Cluster?

Redis Cluster is a distributed implementation of Redis that provides:

- Data sharding across multiple Redis nodes
- High availability through automatic failover
- No single point of failure

## Jedis Cluster

Redis Client Builder supports building Jedis clients with Cluster configuration:

### Kotlin Example

```kotlin
val jedisCluster = RedisClientBuilderFactory.jedisCluster()
    .addNode("localhost", 7000)
    .addNode("localhost", 7001)
    .addNode("localhost", 7002)
    .password("password")
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .maxRedirections(5)
    .ssl(true)
    .maxTotal(100)
    .maxIdle(10)
    .build()

// Use the JedisCluster
jedisCluster.set("key", "value")
val value = jedisCluster.get("key")
println(value)
jedisCluster.close()
```

### Java Example

```java
JedisCluster jedisCluster = RedisClientBuilderFactory.jedisCluster()
    .addNode("localhost", 7000)
    .addNode("localhost", 7001)
    .addNode("localhost", 7002)
    .password("password")
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .maxRedirections(5)
    .ssl(true)
    .maxTotal(100)
    .maxIdle(10)
    .build();

// Use the JedisCluster
jedisCluster.set("key", "value");
String value = jedisCluster.get("key");
System.out.println(value);
jedisCluster.close();
```

## Lettuce Cluster

Redis Client Builder also supports building Lettuce clients with Cluster configuration:

### Kotlin Example

```kotlin
val redisClusterClient = RedisClientBuilderFactory.lettuceCluster()
    .addNode("localhost", 7000)
    .addNode("localhost", 7001)
    .addNode("localhost", 7002)
    .password("password")
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .maxRedirections(5)
    .ssl(true)
    .autoReconnect(true)
    .build()

// Use the RedisClusterClient
val connection = redisClusterClient.connect()
val commands = connection.sync()
commands.set("key", "value")
val value = commands.get("key")
println(value)
connection.close()
redisClusterClient.shutdown()
```

### Java Example

```java
RedisClusterClient redisClusterClient = RedisClientBuilderFactory.lettuceCluster()
    .addNode("localhost", 7000)
    .addNode("localhost", 7001)
    .addNode("localhost", 7002)
    .password("password")
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .maxRedirections(5)
    .ssl(true)
    .autoReconnect(true)
    .build();

// Use the RedisClusterClient
StatefulRedisClusterConnection<String, String> connection = redisClusterClient.connect();
RedisAdvancedClusterCommands<String, String> commands = connection.sync();
commands.set("key", "value");
String value = commands.get("key");
System.out.println(value);
connection.close();
redisClusterClient.shutdown();
```

## Generic Cluster Builder

You can also use the generic cluster builder method to create a builder for a specific Redis cluster client type:

### Kotlin Example

```kotlin
val jedisClusterBuilder = RedisClientBuilderFactory.clusterBuilder(JedisCluster::class.java) as JedisClusterClientBuilder
val lettuceClusterBuilder = RedisClientBuilderFactory.clusterBuilder(RedisClusterClient::class.java) as LettuceClusterClientBuilder
```

### Java Example

```java
JedisClusterClientBuilder jedisClusterBuilder = (JedisClusterClientBuilder) RedisClientBuilderFactory.clusterBuilder(JedisCluster.class);
LettuceClusterClientBuilder lettuceClusterBuilder = (LettuceClusterClientBuilder) RedisClientBuilderFactory.clusterBuilder(RedisClusterClient.class);
```

## Configuration Options

### Common Options

These options are available for all Redis Cluster client builders:

- `addNode(String, Int)`: Adds a Redis cluster node to the configuration
- `password(String)`: Sets the password for authentication with the Redis cluster
- `connectionTimeout(Int)`: Sets the connection timeout in milliseconds
- `socketTimeout(Int)`: Sets the socket timeout in milliseconds
- `maxRedirections(Int)`: Sets the maximum number of redirections to follow during command execution
- `ssl(Boolean)`: Enables SSL/TLS for the connection

### Jedis Cluster Options

These options are specific to the Jedis Cluster client builder:

- `maxTotal(Int)`: Sets the maximum number of connections that can be allocated by the pool at a given time
- `maxIdle(Int)`: Sets the maximum number of idle connections that can be maintained by the pool without being closed
- `minIdle(Int)`: Sets the minimum number of idle connections to maintain in the pool
- `testOnBorrow(Boolean)`: Sets whether connections should be validated before being borrowed from the pool
- `testOnReturn(Boolean)`: Sets whether connections should be validated before being returned to the pool
- `testWhileIdle(Boolean)`: Sets whether idle connections should be validated by the idle connection evictor

### Lettuce Cluster Options

These options are specific to the Lettuce Cluster client builder:

- `autoReconnect(Boolean)`: Sets whether the client should automatically reconnect
- `requestQueueSize(Int)`: Sets the request queue size
- `publishOnScheduler(Boolean)`: Sets whether to publish on the scheduler
- `disconnectedBehavior(ClientOptions.DisconnectedBehavior)`: Sets the disconnected behavior