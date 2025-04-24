dependencies {
    // Core module dependency
    implementation(project(":redis-client-builder-core"))
    
    // Resilience4j dependencies
    api(libs.resilience4j.core)
    api(libs.resilience4j.circuitbreaker)
    api(libs.resilience4j.retry)
    api(libs.resilience4j.timelimiter)
    api(libs.resilience4j.bulkhead)
    api(libs.resilience4j.ratelimiter)
    
    // Testing
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.mockk)
}