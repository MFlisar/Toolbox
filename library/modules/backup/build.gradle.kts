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
    enableAndroidResources = false
)

// ------------------------
// Kotlin
// ------------------------

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
        androidResources {
            enable = true
        }
    }

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        val targetsBackupSupport = listOf(Platform.ANDROID, Platform.WINDOWS)

        val notAndroidMain by creating { dependsOn(commonMain.get()) }
        val featureBackupSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoBackupSupportMain by creating { dependsOn(commonMain.get()) }

        setupDependencies(buildTargets, sourceSets) {

            notAndroidMain supportedBy !Platform.ANDROID
            featureBackupSupportMain supportedBy targetsBackupSupport
            featureNoBackupSupportMain supportedBy !targetsBackupSupport

        }

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // kotlinx
            implementation(libs.jetbrains.kotlinx.serialization.json)
            implementation(libs.jetbrains.kotlinx.datetime)

            // compose
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.material3)

            // Toolbox
            implementation(project(":toolbox:core"))
            implementation(project(":toolbox:modules:zip"))

            api(deps.kmp.parcelize)

            implementation(deps.lumberjack.core)
            implementation(deps.composedialogs.core)
            implementation(deps.composedialogs.dialog.info)
            api(deps.composedialogs.dialog.frequency)

            // libraries
            implementation(deps.filekit.dialogs.compose)

        }

        androidMain.dependencies {

            // androidX
            implementation(libs.androidx.core)
            implementation(libs.androidx.work.runtime)
            implementation(libs.androidx.activity.compose)

            // libraries
            implementation(deps.accompanist.permission)

            // toolbox
            implementation(project(":toolbox:modules:service"))
        }
    }
}

// -------------------
// Publish
// -------------------

// maven publish configuration
if (BuildFileUtil.checkGradleProperty(project, "publishToMaven") != false)
    BuildFileUtil.setupMavenPublish(project, config, libraryConfig)