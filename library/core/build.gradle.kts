import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.configs.*
import com.michaelflisar.kmpdevtools.core.Platform
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
    alias(mflisar.plugins.kmpdevtools.buildplugin)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val module = LibraryModuleConfig.read(project)

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
    libraryModuleConfig = module,
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = true
)

// ------------------------
// Kotlin
// ------------------------

compose.resources {
    packageOfResClass = "${module.projectNamespace}.core.resources"
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

    buildTargets.setupTargetsLibrary(module)
    android {
        buildTargets.setupTargetsAndroidLibrary(module, androidConfig, this)
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

        setupDependencies(module, buildTargets, sourceSets) {

            Platform.IOS addSourceSet iosMain

            featureFileSupportMain supportedBy Platform.LIST_FILE_SUPPORT
            javaMain supportedBy Platform.LIST_JAVA
            feedbackSupportedMain supportedBy Platform.LIST_MOBILE

        }

        if (buildTargets.macOS) {
            val macosMain by creating { dependsOn(commonMain.get()) }
            setupDependencies(module, buildTargets, sourceSets) {
                Platform.MACOS addSourceSet macosMain
            }
        }

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {


            api(libs.jetbrains.kotlinx.coroutines.core)
            implementation(libs.jetbrains.kotlinx.serialization.json)
            implementation(libs.jetbrains.kotlinx.datetime)

            // Compose + AndroidX
            api(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.material.icons.core)
            implementation(libs.jetbrains.compose.material.icons.extended)

            // KMPPlatformContext
            api(mflisar.kmp.platformcontext.core)

            // Lumberjack
            api(mflisar.lumberjack.core)

            // Compose Changelog
            implementation(mflisar.composechangelog.core)
            implementation(mflisar.composedialogs.core)

            implementation(deps.filekit.dialogs.compose)

            api(mflisar.kmp.parcelize)
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
            implementation(mflisar.kmp.mail)
            implementation(mflisar.lumberjack.extension.feedback)
        }
    }
}

// -------------------
// Publish
// -------------------

// maven publish configuration
if (BuildFileUtil.checkGradleProperty(project, "publishToMaven") != false)
    BuildFileUtil.setupMavenPublish(module)