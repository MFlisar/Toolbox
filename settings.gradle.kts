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
        //mavenLocal()
    }
}

// --------------
// Settings Plugin
// --------------

plugins {
    // version catalogue does not work here!
    // alias(deps.plugins.kmp.gradle.tools.settings.gradle.plugin)
    id("io.github.mflisar.kmp-library.plugins-settings-gradle") version "1.9.2" //apply false
}

settingsFilePlugin {
    logging = true
    includeLibraries = !useLiveDependencies(settings)
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
includeModule("library\\modules\\form",             ":toolbox:modules:form")
includeModule("library\\modules\\ui",               ":toolbox:modules:ui")
includeModule("library\\modules\\zip",              ":toolbox:modules:zip")
includeModule("library\\modules\\backup",           ":toolbox:modules:backup")
includeModule("library\\modules\\service",          ":toolbox:modules:service")
includeModule("library\\modules\\csv",              ":toolbox:modules:csv")
includeModule("library\\modules\\room",             ":toolbox:modules:room")

includeModule("library\\app",                       ":toolbox:app")

// --------------
// App
// --------------

include(":demo")