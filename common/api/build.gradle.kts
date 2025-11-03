plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.denchic45"
version = "1.0-SNAPSHOT"
val ktorVersion = "3.2.3"
val arrowVersion = "2.1.2"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    api("io.ktor:ktor-client-core:$ktorVersion")
    api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    api("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    api("io.ktor:ktor-client-logging:$ktorVersion")
    api("io.ktor:ktor-client-auth:$ktorVersion")
    api("io.ktor:ktor-client-logging:${ktorVersion}")

    // arrow-kt
    api("io.arrow-kt:arrow-core:$arrowVersion")
    api("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

    api("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
    api("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1-0.6.x-compat")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}