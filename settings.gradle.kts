enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "qa-automation-accelerator"

include("core")
include("plugins:http-emulator")
include("plugins:fileio-emulator")
include("plugins:database-manager-h2")
include("plugins:database-manager-jdbc")
include("plugins:database-manager-sqlserver")
include("app")

project(":plugins:http-emulator").projectDir = file("plugins/http-emulator")
project(":plugins:fileio-emulator").projectDir = file("plugins/fileio-emulator")
project(":plugins:database-manager-h2").projectDir = file("plugins/database-manager-h2")
project(":plugins:database-manager-jdbc").projectDir = file("plugins/database-manager-jdbc")
project(":plugins:database-manager-sqlserver").projectDir = file("plugins/database-manager-sqlserver")
