dependencies {
    // Core module dependency
    implementation(project(":redis-client-builder-core"))

    // Lettuce dependency
    api(libs.lettuce.core)

    // Testing
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.junit)
}
