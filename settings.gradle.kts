dependencyResolutionManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        // jewel
        maven("https://packages.jetbrains.team/maven/p/kpm/public/")
        maven("https://www.jetbrains.com/intellij-repository/releases")
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

includeModule("library\\core",                      ":Toolbox:Core")
includeModule("library\\modules\\table",            ":Toolbox:Modules:Table")
includeModule("library\\modules\\ui",               ":Toolbox:Modules:Ui")
includeModule("library\\modules\\windows",          ":Toolbox:Modules:Windows")
includeModule("library\\modules\\android-demo-app", ":Toolbox:Modules:AndroidDemoApp")

// --------------
// App
// --------------

include(":demo:android")
include(":demo:windows")
include(":demo:web")