plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

// Common configuration for all projects
allprojects {
    group = "com.joshrotenberg"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// Configuration for all subprojects
subprojects {
    // Apply common plugins to all subprojects
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jlleitschuh.gradle.ktlint")
        plugin("io.gitlab.arturbosch.detekt")
    }

    // Common dependencies for all subprojects
    dependencies {
        // Add common dependencies here if needed
    }

    // Configure Java compatibility for better Java interoperability
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        withJavadocJar()
        withSourcesJar()
    }

    // Configure Kotlin for all subprojects
    configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
        jvmToolchain(17)
    }

    // Configure test tasks for all subprojects
    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // Configure ktlint for all subprojects
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
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
    }

    // Configure detekt for all subprojects
    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true // use the default detekt configuration as a baseline
        allRules = false // activate all available (even unstable) rules
        config.setFrom(files("$rootDir/config/detekt/detekt.yml")) // point to your custom config defining rules to run
        baseline = file("$rootDir/config/detekt/baseline.xml") // a way of suppressing issues before introducing detekt

        reports {
            html.required.set(true) // observe findings in your browser with structure and code snippets
            xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
            txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
            sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
        }
    }
}

// Task to create a detekt baseline
tasks.register<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>("detektCreateBaseline") {
    description = "Creates a detekt baseline file to track and suppress existing issues"
    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(files("src/main/kotlin", "src/test/kotlin"))
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline.set(file("$rootDir/config/detekt/baseline.xml"))
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
