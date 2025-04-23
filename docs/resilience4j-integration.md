# Resilience4j Integration (Proposed)

This document outlines the proposed integration of [Resilience4j](https://github.com/resilience4j/resilience4j) with Redis Client Builder. Resilience4j is a lightweight fault tolerance library designed for Java 8 and functional programming.

## Overview

The proposed integration would allow Redis clients created by Redis Client Builder to be wrapped with one or more Resilience4j modules, providing fault tolerance capabilities such as:

- **Circuit Breaker**: Prevents calling Redis when it's failing
- **Retry**: Automatically retries failed Redis operations
- **Time Limiter**: Sets timeout for Redis operations
- **Bulkhead**: Limits concurrent Redis operations
- **Rate Limiter**: Limits the rate of Redis operations

## Proposed API

The integration would add new methods to the Redis Client Builder API that allow wrapping the built Redis client with Resilience4j modules.

### Basic Usage

```kotlin
// Create a Redis client with a circuit breaker
val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .withCircuitBreaker(CircuitBreakerConfig.custom()
        .failureRateThreshold(50f)
        .waitDurationInOpenState(Duration.ofMillis(1000))
        .build())
    .build()

// Create a Redis client with retry
val redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .withRetry(RetryConfig.custom()
        .maxAttempts(3)
        .waitDuration(Duration.ofMillis(100))
        .build())
    .build()

// Create a Redis client with multiple resilience modules
val resilientRedisClient = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .withCircuitBreaker(circuitBreakerConfig)
    .withRetry(retryConfig)
    .withTimeLimiter(timeLimiterConfig)
    .build()
```

### Java Usage

```java
// Create a Redis client with a circuit breaker
JedisPool jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .withCircuitBreaker(CircuitBreakerConfig.custom()
        .failureRateThreshold(50f)
        .waitDurationInOpenState(Duration.ofMillis(1000))
        .build())
    .build();

// Create a Redis client with retry
RedisClient redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .withRetry(RetryConfig.custom()
        .maxAttempts(3)
        .waitDuration(Duration.ofMillis(100))
        .build())
    .build();
```

## Supported Resilience4j Modules

### Circuit Breaker

The Circuit Breaker pattern prevents a Redis client from repeatedly trying to execute an operation that's likely to fail.

```kotlin
val circuitBreakerConfig = CircuitBreakerConfig.custom()
    .failureRateThreshold(50f)
    .waitDurationInOpenState(Duration.ofMillis(1000))
    .permittedNumberOfCallsInHalfOpenState(2)
    .slidingWindowSize(10)
    .build()

val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .withCircuitBreaker(circuitBreakerConfig)
    .build()
```

### Retry

The Retry pattern enables automatic retrying of failed Redis operations.

```kotlin
val retryConfig = RetryConfig.custom()
    .maxAttempts(3)
    .waitDuration(Duration.ofMillis(100))
    .retryExceptions(IOException::class.java, TimeoutException::class.java)
    .build()

val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .withRetry(retryConfig)
    .build()
```

### Time Limiter

The Time Limiter pattern limits the duration of Redis operations.

```kotlin
val timeLimiterConfig = TimeLimiterConfig.custom()
    .timeoutDuration(Duration.ofSeconds(1))
    .build()

val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .withTimeLimiter(timeLimiterConfig)
    .build()
```

### Bulkhead

The Bulkhead pattern limits the number of concurrent Redis operations.

```kotlin
val bulkheadConfig = BulkheadConfig.custom()
    .maxConcurrentCalls(10)
    .maxWaitDuration(Duration.ofMillis(500))
    .build()

val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .withBulkhead(bulkheadConfig)
    .build()
```

### Rate Limiter

The Rate Limiter pattern limits the rate of Redis operations.

```kotlin
val rateLimiterConfig = RateLimiterConfig.custom()
    .limitRefreshPeriod(Duration.ofSeconds(1))
    .limitForPeriod(10)
    .timeoutDuration(Duration.ofMillis(100))
    .build()

val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .withRateLimiter(rateLimiterConfig)
    .build()
```

## Implementation Considerations

The implementation would need to:

1. Add resilience4j as a dependency (possibly as an optional dependency)
2. Create wrapper classes for each Redis client type that apply the resilience4j decorators
3. Extend the builder interfaces to support configuring resilience4j modules
4. Update the build() methods to wrap the Redis clients with the configured resilience4j modules

## Benefits

- **Fault Tolerance**: Makes Redis clients more resilient to failures
- **Consistent API**: Provides a consistent way to add resilience across different Redis client libraries
- **Flexibility**: Allows users to choose which resilience patterns to apply
- **Configurability**: Provides full access to resilience4j configuration options
