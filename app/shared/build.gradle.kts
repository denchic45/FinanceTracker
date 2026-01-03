import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

// Helper function to read from secrets.properties
fun getSecret(propertyName: String): String {
    val secretsFile = rootProject.file("local.properties")
    if (!secretsFile.exists()) {
        println("Warning: secrets.properties file not found.")
        return ""
    }
    val properties = Properties()
    secretsFile.inputStream().use { properties.load(it) }
    return properties.getProperty(propertyName) ?: ""
}

// Define the output directory inside the 'build' folder
val generatedSrcDir = layout.buildDirectory.dir("generated/secrets/commonMain/kotlin")

tasks.register("generateSecrets") {
    // Tell Gradle the output directory
    outputs.dir(generatedSrcDir)

    doLast {
        // Ensure the task writes the file to the correct GENERATED directory
        val secretsFile =
            generatedSrcDir.get().file("com/denchic45/financetracker/Secrets.kt").asFile
        secretsFile.parentFile.mkdirs()
        secretsFile.writeText(
            """
            package com.denchic45.financetracker

            // This is an auto-generated file. Do not edit.
            object Secrets {
                const val BASE_URL: String = "${getSecret("BASE_URL")}"
            }
        """.trimIndent()
        )
    }
}

kotlin {
    androidLibrary {
        namespace = "com.denchic45.financetracker"
        compileSdk = 36

        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonMain.get().kotlin.srcDir(tasks.named("generateSecrets"))

        commonMain.dependencies {
            implementation(libs.compose.resources)
            implementation(libs.androidx.datastore.preferences)

            // Networking & Image Loading
            implementation(libs.ktor.client.okhttp)
            implementation(libs.bundles.coil)

            // Dependency Injection
            implementation(libs.koin.compose)
        }

        androidMain.dependencies {
            implementation(libs.financetracker.common)

            // Android Core & Lifecycle
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)

            // Compose BOM & UI
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.bundles.compose.ui)
//            implementation(libs.compose.resources)

            // Compose Material & Adaptive Layout
            implementation(libs.bundles.compose.material)
            implementation(libs.bundles.compose.adaptive)

            // Navigation & Paging
            implementation(libs.bundles.navigation)

            implementation(libs.bundles.paging)

            // Data Persistence (Room & Datastore)
            implementation(libs.bundles.room)
            implementation(libs.bundles.testing)
        }
    }

}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
}

compose.resources { }

room {
    schemaDirectory("$projectDir/schemas")
}