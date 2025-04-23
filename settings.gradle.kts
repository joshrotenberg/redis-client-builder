plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "redis-client-builder"

// Include all modules
include(
    "redis-client-builder-core",
    "redis-client-builder-jedis",
    "redis-client-builder-lettuce",
    "redis-client-builder-resilience4j",
    "redis-client-builder-failover"
)
