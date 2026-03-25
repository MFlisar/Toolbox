import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.configs.library.AndroidLibraryConfig
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.core.configs.Config
import com.michaelflisar.kmpdevtools.core.configs.LibraryConfig

plugins {
    // kmp + app/library
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    // org.jetbrains.kotlin
    // --
    // org.jetbrains.compose
    // --
    // docs, publishing, validation
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.maven.publish.base)
    alias(libs.plugins.binary.compatibility.validator)
    // build tools
    alias(deps.plugins.kmpdevtools.buildplugin)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val config = Config.read(rootProject)
val libraryConfig = LibraryConfig.read(rootProject)

val buildTargets = Targets(
    // mobile
    android = true,
    iOS = false,
    // desktop
    windows = true,
    macOS = false,
    // web
    wasm = false
)

val androidConfig = AndroidLibraryConfig.create(
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = false
)

// ------------------------
// Kotlin
// ------------------------

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    //-------------
    // Targets
    //-------------

    buildTargets.setupTargetsLibrary(project)
    android {
        buildTargets.setupTargetsAndroidLibrary(project, config, libraryConfig, androidConfig, this)
    }

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        val targetsJava = listOf(Platform.ANDROID, Platform.WINDOWS)

        val javaMain by creating { dependsOn(commonMain.get()) }

        buildTargets.setupDependencies(javaMain, sourceSets, targetsJava)

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            implementation(deps.csv)

            // Library
            api(project(":toolbox:core"))

        }
    }
}

// -------------------
// Publish
// -------------------

// maven publish configuration
if (BuildFileUtil.checkGradleProperty(project, "publishToMaven") != false)
    BuildFileUtil.setupMavenPublish(project, config, libraryConfig)