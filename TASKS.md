# Redis Client Builder - Tasks

## Core Functionality
- [x] Add support for Redis Cluster configuration
  - [x] Extend builders to support cluster configuration
  - [x] Add documentation for cluster configuration
- [x] Add support for Redis Sentinel configuration
  - [x] Extend builders to support sentinel configuration
  - [x] Add documentation for sentinel configuration
- [x] Implement connection pooling options for all client libraries
- [x] Add support for SSL/TLS configuration with custom certificates

## Testing
- [x] Add unit tests for all builder classes
- [x] Add integration tests for all Redis client types
- [ ] Add tests for cluster and sentinel configurations
- [ ] Implement test coverage reporting
- [ ] Add performance benchmarks for different client libraries

## Documentation
- [x] Complete API documentation with examples
- [x] Add more detailed usage examples for each client type
- [ ] Document best practices for Redis client configuration
- [ ] Add migration guide for switching between client libraries
- [ ] Add a GitHub Action to lint and report all Markdown

## Build & CI/CD
- [x] Set up continuous integration with GitHub Actions
- [ ] Configure automated testing on multiple JDK versions
- [x] Set up code quality checks (e.g., ktlint, detekt)
- [ ] Configure automated release process
- [ ] Set up dependency vulnerability scanning

## Distribution
- [ ] Publish library to Maven Central
- [ ] Create release notes for each version
- [x] Set up automated documentation deployment
- [ ] Create demo applications showcasing library usage
- [ ] Evaluate the benefits of splitting the project into separate Gradle modules such as core, jedis, lettuce, r4j, etc.

## Project Modularization
The following steps outline how to modularize the project into separate Gradle modules:

1. **Create Module Structure**
   - [x] Create a `redis-client-builder-core` module for base interfaces and common code
   - [x] Create a `redis-client-builder-jedis` module for Jedis implementation
   - [x] Create a `redis-client-builder-lettuce` module for Lettuce implementation
   - [x] Create a `redis-client-builder-resilience4j` module for resilience4j integration

2. **Update Gradle Configuration**
   - [x] Modify `settings.gradle.kts` to include all modules
   - [x] Create individual `build.gradle.kts` files for each module
   - [ ] Set up proper dependencies between modules
   - [ ] Configure common build logic in a convention plugin or buildSrc

3. **Refactor Code**
   - [ ] Move core interfaces to the core module
   - [ ] Move Jedis implementation to the jedis module
   - [ ] Move Lettuce implementation to the lettuce module
   - [ ] Move resilience4j integration to the resilience4j module
   - [ ] Update imports across all modules

4. **Update Tests**
   - [ ] Reorganize tests to match the new module structure
   - [ ] Create shared test utilities in a test-common module if needed
   - [ ] Ensure all tests pass with the new structure

5. **Update Documentation and Examples**
   - [ ] Update README.md to reflect the new modular structure
   - [ ] Update usage examples to show how to include specific modules
   - [ ] Document the dependency relationships between modules

6. **Configure Publishing**
   - [ ] Set up Maven publishing for each module
   - [ ] Configure proper POM metadata for each module
   - [ ] Ensure artifacts include sources and javadoc

## Future Enhancements
- [ ] Add support for additional Redis client libraries
- [ ] Implement reactive client support
- [ ] Add support for Redis Stack modules (RedisJSON, RediSearch, etc.)
- [ ] Create Spring Boot starter for easy integration
- [ ] Develop Kotlin coroutines support for asynchronous operations
- [ ] Add resilience4j integration for fault tolerance
  - [x] Add resilience4j as an optional dependency
  - [x] Create a resilience module interface for wrapping Redis clients
  - [x] Implement circuit breaker integration
  - [x] Implement retry integration
  - [x] Implement time limiter integration
  - [x] Implement bulkhead integration
  - [x] Implement rate limiter integration
  - [x] Update builder interfaces to support resilience4j configuration
  - [x] Create comprehensive documentation with examples
