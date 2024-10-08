dependencyResolutionManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
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

include(":PublicUtilities:Core")
project(":PublicUtilities:Core").projectDir = file("library/core")

include(":PublicUtilities:Modules:AndroidApp")
project(":PublicUtilities:Modules:AndroidApp").projectDir = file("library/modules/android-app")

include(":PublicUtilities:Modules:WindowsApp")
project(":PublicUtilities:Modules:WindowsApp").projectDir = file("library/modules/windows-app")

// --------------
// App
// --------------

include(":demo:android")
include(":demo:windows")
