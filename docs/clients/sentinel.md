# Redis Sentinel Support

Redis Client Builder provides support for Redis Sentinel configuration, allowing you to build Redis clients that connect through Redis Sentinel for high availability.

## What is Redis Sentinel?

Redis Sentinel provides high availability for Redis. It monitors your Redis instances, notifies you about changes in their state, and automatically performs failover if a master is not working as expected.

## Jedis Sentinel

Redis Client Builder supports building Jedis clients with Sentinel configuration:

### Kotlin Example

```kotlin
val jedisSentinelPool = RedisClientBuilderFactory.jedisSentinel()
    .masterName("mymaster")
    .addSentinel("sentinel1", 26379)
    .addSentinel("sentinel2", 26380)
    .addSentinel("sentinel3", 26381)
    .password("password")
    .database(0)
    .connectionTimeout(2000)
    .socketTimeout(2000)
    .clientName("myclient")
    .maxTotal(100)
    .maxIdle(10)
    .build()

// Use the JedisSentinelPool
jedisSentinelPool.resource.use { jedis ->
    jedis.set("key", "value")
    val value = jedis.get("key")
    println(value)
}
```

### Generic Sentinel Builder

You can also use the generic sentinel builder method to create a builder for a specific Redis sentinel client type:

```kotlin
val jedisSentinelBuilder = RedisClientBuilderFactory.sentinelBuilder(JedisSentinelPool::class.java) as JedisSentinelClientBuilder
```

## Configuration Options

### Common Options

These options are available for all Redis Sentinel client builders:

- `masterName(String)`: Sets the name of the Redis master that the sentinels are monitoring
- `addSentinel(String, Int)`: Adds a Redis sentinel node to the configuration
- `password(String)`: Sets the password for authentication with the Redis sentinel
- `database(Int)`: Sets the Redis database index
- `connectionTimeout(Int)`: Sets the connection timeout in milliseconds
- `socketTimeout(Int)`: Sets the socket timeout in milliseconds
- `ssl(Boolean)`: Enables SSL/TLS for the connection

### Jedis Sentinel Options

These options are specific to the Jedis Sentinel client builder:

- `clientName(String)`: Sets the client name
- `maxTotal(Int)`: Sets the maximum number of connections that can be allocated by the pool at a given time
- `maxIdle(Int)`: Sets the maximum number of idle connections that can be maintained by the pool without being closed
- `minIdle(Int)`: Sets the minimum number of idle connections to maintain in the pool
- `testOnBorrow(Boolean)`: Sets whether connections should be validated before being borrowed from the pool
- `testOnReturn(Boolean)`: Sets whether connections should be validated before being returned to the pool
- `testWhileIdle(Boolean)`: Sets whether idle connections should be validated by the idle connection evictor
- `timeBetweenEvictionRuns(Long)`: Sets the time between runs of the idle connection evictor thread in milliseconds
- `blockWhenExhausted(Boolean)`: Sets whether clients should block when the pool is exhausted
- `jmxEnabled(Boolean)`: Sets whether JMX should be enabled for the pool