rootProject.name = "financetracker-backend"

include(":api")
project(":api").projectDir = file("../common/api")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}