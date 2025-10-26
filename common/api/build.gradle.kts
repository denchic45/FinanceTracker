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

    // arrow-kt
    api("io.arrow-kt:arrow-core:$arrowVersion")
    api("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}