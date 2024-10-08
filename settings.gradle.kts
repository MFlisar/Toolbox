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
    }
}

pluginManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}

// --------------
// Library
// --------------

include(":DemoUtilities:Core")
project(":DemoUtilities:Core").projectDir = file("library/core")

include(":DemoUtilities:Modules:App")
project(":DemoUtilities:Modules:App").projectDir = file("library/modules/app")
