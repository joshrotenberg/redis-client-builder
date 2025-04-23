plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

group = "com.joshrotenberg"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Redis client libraries
    implementation(libs.jedis)
    implementation(libs.lettuce.core)

    // Testing
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// Configure Java compatibility for better Java interoperability
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

// Configure ktlint
ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
    // Disable some rules that might be too strict for existing code
    disabledRules.set(
        listOf(
            "no-wildcard-imports",
            "filename",
            "final-newline",
            "max-line-length",
            "import-ordering"
        )
    )
    // Only apply to new code
    baseline.set(file("$projectDir/config/ktlint/baseline.xml"))
}

// Configure detekt
detekt {
    buildUponDefaultConfig = true // use the default detekt configuration as a baseline
    allRules = false // activate all available (even unstable) rules
    config.setFrom(files("$projectDir/config/detekt/detekt.yml")) // point to your custom config defining rules to run
    baseline = file("$projectDir/config/detekt/baseline.xml") // a way of suppressing issues before introducing detekt

    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
    }
}

// Task to create a detekt baseline
tasks.register<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>("detektCreateBaseline") {
    description = "Creates a detekt baseline file to track and suppress existing issues"
    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(files("src/main/kotlin", "src/test/kotlin"))
    config.setFrom(files("$projectDir/config/detekt/detekt.yml"))
    baseline.set(file("$projectDir/config/detekt/baseline.xml"))
}

// Combined code quality check task
tasks.register("codeQualityCheck") {
    description = "Runs all code quality checks"
    group = "verification"

    // For now, only include detekt since ktlint needs more configuration
    dependsOn(tasks.named("detekt"))

    doLast {
        println("Code quality check completed successfully.")
        println("Note: ktlint checks are configured but not included in this task yet.")
        println("Run './gradlew ktlintCheck' separately to run ktlint checks.")
    }
}
