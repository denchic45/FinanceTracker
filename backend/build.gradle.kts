import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.denchic45"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.financetracker.common)

    // Ktor & Server
    implementation(libs.bundles.ktor.server)

    // Exposed ORM
    implementation(libs.bundles.exposed)

    // Database Drivers & Pool
    implementation(libs.hikari.cp)
    implementation(libs.postgresql)
    testImplementation(libs.h2)

    // Dependency Injection
    implementation(libs.bundles.koin)

    // Utilities & Logging
    implementation(libs.logback.classic)
    implementation(libs.jbcrypt)

    // Testing
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.koin.test.junit5)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.junit.platform.launcher)
}
