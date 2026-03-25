import com.michaelflisar.kmpdevtools.core.configs.LibraryConfig

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
    id("io.github.mflisar.kmpdevtools.plugins-settings-gradle") version "7.1.9"
}
val settingsPlugin = plugins.getPlugin(com.michaelflisar.kmpdevtools.SettingsFilePlugin::class.java)

// --------------
// Library
// --------------

val libraryConfig = LibraryConfig.read(rootProject)
val libraryId = libraryConfig.libraryId()

// Library Modules
settingsPlugin.includeModules(libraryId, libraryConfig, includeDokka = true)

/*
with(settingsPlugin) {

    // empty folders
    includeModule("library", ":toolbox")
    includeModule("library\\modules", ":toolbox:modules")

    // modules
    includeModule("library\\core", ":toolbox:core")
    includeModule("library\\modules\\table", ":toolbox:modules:table")
    includeModule("library\\modules\\form", ":toolbox:modules:form")
    includeModule("library\\modules\\ui", ":toolbox:modules:ui")
    includeModule("library\\modules\\zip", ":toolbox:modules:zip")
    includeModule("library\\modules\\backup", ":toolbox:modules:backup")
    includeModule("library\\modules\\service", ":toolbox:modules:service")
    includeModule("library\\modules\\csv", ":toolbox:modules:csv")
    includeModule("library\\modules\\room", ":toolbox:modules:room")
    includeModule("library\\modules\\ads", ":toolbox:modules:ads")
    includeModule("library\\modules\\proversion", ":toolbox:modules:proversion")

    includeModule("library\\app", ":toolbox:app")
}*/

// --------------
// App
// --------------

if (System.getenv("CI") != "true") {
    // demo app
    include(":demo:shared")
    include(":demo:app:android")
    include(":demo:app:compose")
//    // hello world app
//    include(":demo:hello-world:app")
//    include(":demo:hello-world:common")
//    include(":demo:hello-world:common:core")
//    include(":demo:hello-world:common:database")
//    include(":demo:hello-world:feature")
//    include(":demo:hello-world:feature:page1")
//    include(":demo:hello-world:feature:page2")
}