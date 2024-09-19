pluginManagement {

    // repositories for build
    repositories {
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    versionCatalogs {
        create("app") {
            from(files("gradle/app.versions.toml"))
        }
        create("mflisar") {
            from(files("gradle/mflisar.versions.toml"))
        }
    }
}

// --------------
// Library
// --------------

include(":DemoUtilities:Core")
project(":DemoUtilities:Core").projectDir = file("library/core")

include(":DemoUtilities:Modules:App")
project(":DemoUtilities:Modules:App").projectDir = file("library/modules/app")
