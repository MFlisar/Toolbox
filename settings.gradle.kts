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

includeModule("library\\app",                       ":toolbox:app")

//includeModule("library\\todo\\android-demo-app",    ":toolbox:todo:androiddemoapp")
//includeModule("library\\todo\\open-source-demo",    ":toolbox:todo:opensourcedemo")
//includeModule("library\\todo\\windows",             ":toolbox:todo:windows")

// --------------
// App
// --------------

include(":demo")