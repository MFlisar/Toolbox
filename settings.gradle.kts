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
    id("io.github.mflisar.kmp-library.plugins-settings-gradle") version "2.2.5" //apply false
}

val settingsPlugin = plugins.getPlugin(com.michaelflisar.kmplibrary.SettingsFilePlugin::class.java)

// --------------
// Modules
// --------------

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
}

// --------------
// App
// --------------

if (System.getenv("CI") != "true") {
    // demo app
    include(":demo:toolbox")
    // hello world app
    include(":demo:hello-world:app")
    include(":demo:hello-world:common")
    include(":demo:hello-world:common:core")
    include(":demo:hello-world:common:database")
    include(":demo:hello-world:feature")
    include(":demo:hello-world:feature:page1")
    include(":demo:hello-world:feature:page2")
}