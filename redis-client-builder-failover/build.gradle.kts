dependencies {
    // Core module dependency
    implementation(project(":redis-client-builder-core"))

    // Guava dependency for AbstractScheduledService
    api(libs.guava)

    // Testing
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.junit)
}

// Make this module optional
// This ensures that applications don't need to include this module's dependencies
// unless they explicitly use the failover functionality
tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "com.joshrotenberg.redis.client.builder.failover"
        )
    }
}
