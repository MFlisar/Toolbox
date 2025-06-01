dependencyResolutionManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        // jewel + skiko
        maven("https://www.jetbrains.com/intellij-repository/releases")
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
    }

    versionCatalogs {
        create("app") {
            from(files("gradle/app.versions.toml"))
        }
        create("androidx") {
            from(files("gradle/androidx.versions.toml"))
        }
        create("kotlinx") {
            from(files("gradle/kotlinx.versions.toml"))
        }
        create("deps") {
            from(files("gradle/deps.versions.toml"))
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
// Functions
// --------------

fun includeModule(path: String, name: String) {
    include(name)
    project(name).projectDir = file(path)
}

// --------------
// Modules
// --------------

includeModule("library\\core",                      ":toolbox:core")
includeModule("library\\modules\\table",            ":toolbox:modules:table")
includeModule("library\\modules\\ui",               ":toolbox:modules:ui")
includeModule("library\\modules\\windows",          ":toolbox:modules:windows")
includeModule("library\\modules\\android-demo-app", ":toolbox:modules:androiddemoapp")

// --------------
// App
// --------------

include(":demo:android")
include(":demo:windows")
include(":demo:web")