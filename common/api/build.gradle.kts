plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.denchic45"
version = "1.0-SNAPSHOT"
val ktorVersion = "3.2.3"

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

    // kotlin-result
    api("com.michael-bull.kotlin-result:kotlin-result:1.1.17")
    api("com.michael-bull.kotlin-result:kotlin-result-coroutines:1.1.17")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}