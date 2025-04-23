# Redis Client Builder

A fluent API for building Redis client instances in Kotlin with first-class Java support.

## Overview

Redis Client Builder is a library that simplifies the instantiation of Redis clients by providing a fluent, builder-style API. It supports multiple Redis client libraries:

- [Jedis](https://github.com/redis/jedis)
- [Lettuce](https://github.com/lettuce-io/lettuce-core)

The library provides a consistent API across all supported Redis client libraries, making it easy to switch between them without changing your configuration code.

## Features

- **Fluent API**: Build Redis client instances with a clean, readable syntax
- **Type Safety**: Leverage Kotlin's type system for safer code
- **Consistent Interface**: Use the same API pattern across different Redis client libraries
- **Java Interoperability**: First-class support for Java developers
- **Comprehensive Configuration**: Access all configuration options of the underlying client libraries
- **High Availability**: Support for Redis Cluster and Sentinel configurations

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

```kotlin
// Create a Jedis client
val jedisPool = RedisClientBuilderFactory.jedis()
    .host("localhost")
    .port(6379)
    .password("password")
    .build()

// Create a Lettuce client
val redisClient = RedisClientBuilderFactory.lettuce()
    .host("localhost")
    .port(6379)
    .password("password")
    .build()
```

## Documentation

For detailed documentation, examples, and API reference, visit our [documentation site](https://joshrotenberg.github.io/redis-client-builder/):

- [Getting Started](https://joshrotenberg.github.io/redis-client-builder/getting-started/)
- [Jedis Client](https://joshrotenberg.github.io/redis-client-builder/clients/jedis/)
- [UnifiedJedis Client](https://joshrotenberg.github.io/redis-client-builder/clients/unified-jedis/)
- [Lettuce Client](https://joshrotenberg.github.io/redis-client-builder/clients/lettuce/)
- [Redis Cluster](https://joshrotenberg.github.io/redis-client-builder/clients/cluster/)
- [Redis Sentinel](https://joshrotenberg.github.io/redis-client-builder/clients/sentinel/)
- [API Reference](https://joshrotenberg.github.io/redis-client-builder/api-reference/)

To build and run the documentation site locally:

```bash
# Install mkdocs and the material theme
pip install mkdocs mkdocs-material

# Serve the documentation site locally
mkdocs serve
```

## Contributing

If you're interested in contributing to this project, please check out the [TASKS.md](TASKS.md) file for a list of tasks that need to be implemented.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
