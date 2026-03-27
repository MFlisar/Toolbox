import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.configs.library.AndroidLibraryConfig
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.core.configs.Config
import com.michaelflisar.kmpdevtools.core.configs.LibraryConfig
import com.michaelflisar.kmpdevtools.setupDependencies

plugins {
    // kmp + app/library
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    // org.jetbrains.kotlin
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    // org.jetbrains.compose
    alias(libs.plugins.jetbrains.compose)
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
    iOS = true,
    // desktop
    windows = true,
    macOS = false, // because of compose unstyled dialogs
    // web
    wasm = true
)

val androidConfig = AndroidLibraryConfig.create(
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = true
)

// ------------------------
// Kotlin
// ------------------------

compose.resources {
    packageOfResClass = "${libraryConfig.library.namespace}.core.resources"
    publicResClass = true
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

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

        val featureFileSupportMain by creating { dependsOn(commonMain.get()) }
        val javaMain by creating { dependsOn(commonMain.get()) }
        val feedbackSupportedMain by creating { dependsOn(commonMain.get()) }
        val iosMain by creating { dependsOn(commonMain.get()) }

        setupDependencies(buildTargets, sourceSets) {

            Platform.IOS addSourceSet iosMain

            featureFileSupportMain supportedBy Platform.LIST_FILE_SUPPORT
            javaMain supportedBy Platform.LIST_JAVA
            feedbackSupportedMain supportedBy Platform.LIST_MOBILE

        }

        if (buildTargets.macOS) {
            val macosMain by creating { dependsOn(commonMain.get()) }
            setupDependencies(buildTargets, sourceSets) {
                Platform.MACOS addSourceSet macosMain
            }
        }

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // kotlinx
            api(libs.jetbrains.kotlinx.coroutines.core)
            implementation(libs.jetbrains.kotlinx.serialization.json)
            implementation(libs.jetbrains.kotlinx.datetime)

            // Compose + AndroidX
            api(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.material.icons.core)
            implementation(libs.jetbrains.compose.material.icons.extended)

            // KMPPlatformContext
            api(deps.kmp.platformcontext.core)

            // Lumberjack
            api(deps.lumberjack.core)

            // Compose Changelog
            implementation(deps.composechangelog.core)
            implementation(deps.composedialogs.core)

            implementation(deps.filekit.dialogs.compose)

            api(deps.kmp.parcelize)
        }

        androidMain.dependencies {

            implementation(libs.androidx.core)
            api(libs.androidx.activity.compose)
            implementation(libs.androidx.browser)

            api(deps.processphoenix)
            implementation(deps.acra.core)
            implementation(deps.acra.mail)
            implementation(deps.acra.dialog)

        }

        feedbackSupportedMain.dependencies {
            implementation(deps.kmp.mail)
            implementation(deps.lumberjack.extension.feedback)
        }
    }
}

// -------------------
// Publish
// -------------------

// maven publish configuration
if (BuildFileUtil.checkGradleProperty(project, "publishToMaven") != false)
    BuildFileUtil.setupMavenPublish(project, config, libraryConfig)