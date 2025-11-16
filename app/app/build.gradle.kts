import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "com.denchic45.financetracker"
    compileSdk = 36

    defaultConfig {
        multiDexEnabled = true
        applicationId = "com.denchic45.financetracker"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //load the values from .properties file
        val keystoreFile = project.rootProject.file("local.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())
        val apiKey = properties["BASE_URL"].toString()

        buildConfigField(
            type = "String",
            name = "BASE_URL",
            value = apiKey
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

configurations.implementation {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {
    implementation(libs.financetracker.common)

    // Android Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose BOM & UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.compose.resources)

    // Compose Material & Adaptive Layout
    implementation(libs.bundles.compose.material)
    implementation(libs.bundles.compose.adaptive)

    // Navigation & Paging
    implementation(libs.bundles.navigation)

    implementation(libs.bundles.paging)

    // Data Persistence (Room & Datastore)
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)

    // Networking & Image Loading
    implementation(libs.ktor.client.okhttp)
    implementation(libs.bundles.coil)

    // Dependency Injection
    implementation(libs.koin.compose)

    // Desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.bundles.testing)
}