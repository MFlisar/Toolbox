import com.michaelflisar.kmplibrary.BuildFilePlugin
import com.michaelflisar.kmplibrary.setupDependencies
import com.michaelflisar.kmplibrary.Target
import com.michaelflisar.kmplibrary.Targets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.toolbox.diff"

val buildTargets = Targets(
    // mobile
    android = true,
    iOS = true,
    // desktop
    windows = true,
    macOS = false, // because of compose unstyled dialogs
    // web
    wasm = true
)

// -------------------
// Setup
// -------------------

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    //-------------
    // Targets
    //-------------

    buildFilePlugin.setupTargetsLibrary(buildTargets)

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------


        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // Compose + AndroidX
            implementation(libs.compose.material3)

            // Library
            api(project(":toolbox:core"))
            implementation(project(":toolbox:modules:ui"))

        }
    }
}

// -------------------
// Configurations
// -------------------

// android configuration
android {

    buildFilePlugin.setupAndroidLibrary(
        androidNamespace = androidNamespace,
        compileSdk = app.versions.compileSdk,
        minSdk = app.versions.minSdk,
        buildConfig = false
    )
}

// maven publish configuration
if (buildFilePlugin.checkGradleProperty("publishToMaven") != false)
    buildFilePlugin.setupMavenPublish()



