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
// Library
// --------------

include(":Toolbox:Core")
project(":Toolbox:Core").projectDir = file("library/core")

include(":Toolbox:Modules:UI")
project(":Toolbox:Modules:UI").projectDir = file("library/modules/ui")

include(":Toolbox:Modules:Table")
project(":Toolbox:Modules:Table").projectDir = file("library/modules/table")

include(":Toolbox:Modules:AndroidDemoApp")
project(":Toolbox:Modules:AndroidDemoApp").projectDir = file("library/modules/android-demo-app")

include(":Toolbox:Modules:WindowsApp")
project(":Toolbox:Modules:WindowsApp").projectDir = file("library/modules/windows-app")

// --------------
// App
// --------------

include(":demo:android")
include(":demo:windows")
include(":demo:web")
