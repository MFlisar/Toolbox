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
    macOS = false, // dialogs unterstützen dass nicht wegen compose unstyled
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
    }

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        val targetsBackupSupport = listOf(Platform.ANDROID, Platform.WINDOWS)
        val targetsAndroid = listOf(Platform.ANDROID)
        val targetsJvm = listOf(Platform.WINDOWS)
        val targetsMobile = listOf(Platform.ANDROID, Platform.IOS)

        val featureFileSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoFileSupportMain by creating { dependsOn(commonMain.get()) }
        val featureBackupSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoBackupSupportMain by creating { dependsOn(commonMain.get()) }
        val notAndroidMain by creating { dependsOn(commonMain.get()) }
        val notJvmMain by creating { dependsOn(commonMain.get()) }

        val mobileMain by creating { dependsOn(commonMain.get()) }
        val iosMain by creating { dependsOn(commonMain.get()) }

        buildTargets.setupDependencies(featureFileSupportMain, sourceSets, Platform.LIST_FILE_SUPPORT)
        buildTargets.setupDependencies(featureNoFileSupportMain, sourceSets, Platform.LIST_FILE_SUPPORT, platformsNotSupported = true)

        buildTargets.setupDependencies(featureBackupSupportMain, sourceSets, targetsBackupSupport)
        buildTargets.setupDependencies(featureNoBackupSupportMain, sourceSets, targetsBackupSupport, platformsNotSupported = true)

        buildTargets.setupDependencies(notAndroidMain, sourceSets, targetsAndroid, platformsNotSupported = true)
        buildTargets.setupDependencies(notJvmMain, sourceSets, targetsJvm, platformsNotSupported = true)

        buildTargets.setupDependencies(mobileMain, sourceSets, targetsMobile)
        buildTargets.setupDependencies(iosMain, sourceSets, listOf(Platform.IOS))

        if (buildTargets.macOS) {
            val macosMain by creating { dependsOn(commonMain.get()) }
            buildTargets.setupDependencies(macosMain, sourceSets, listOf(Platform.MACOS))
        }

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {
            
            // kotlinx         
            implementation(libs.jetbrains.kotlinx.coroutines.core)
            implementation(libs.jetbrains.kotlinx.serialization.json)
            implementation(libs.jetbrains.kotlinx.datetime)

            // Compose + AndroidX
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.material3.adaptive)
            implementation(libs.jetbrains.compose.ui.backhandler)
            api(libs.jetbrains.androidx.lifecycle.runtime.compose)

            // Compose Icons
            implementation(libs.jetbrains.compose.material.icons.core)
            implementation(libs.jetbrains.compose.material.icons.extended)
            implementation(deps.icons.fontawesome)

            // Navigation
            api(deps.voyager.navigator)
            api(deps.voyager.transitions)
            api(deps.voyager.screenmodel)

            // Library
            api(project(":toolbox:core"))
            api(project(":toolbox:modules:ui"))
            api(project(":toolbox:modules:zip"))
            api(project(":toolbox:modules:backup"))

            // Lumberjack
            api(deps.lumberjack.core)
            api(deps.lumberjack.implementation)
            api(deps.lumberjack.logger.console)

            // KotPreferences
            api(deps.kotpreferences.core)
            api(deps.kotpreferences.extension.compose)

            // Compose Debug Drawer
            api(deps.composedebugdrawer.core)
            implementation(deps.composedebugdrawer.plugin.kotpreferences)

            // Compose Changelog
            api(deps.composechangelog.core)
            implementation(deps.composechangelog.renderer.header)
            implementation(deps.composechangelog.statesaver.kotpreferences)

            // Compose Dialogs
            api(deps.composedialogs.core)
            implementation(deps.composedialogs.dialog.info)
            implementation(deps.composedialogs.dialog.time)
            implementation(deps.composedialogs.dialog.frequency)

            // Compose Preferences
            api(deps.composepreferences.core)
            implementation(deps.composepreferences.screen.bool)
            implementation(deps.composepreferences.screen.list)
            implementation(deps.composepreferences.screen.button)
            implementation(deps.composepreferences.kotpreferences)

            // Compose Colors
            implementation(deps.composecolors.material)

            // Theming
            api(deps.composethemer.core)
            implementation(deps.composethemer.modules.picker)
            implementation(deps.composethemer.modules.defaultpicker)

            implementation(deps.filekit.dialogs.compose)

        }

        featureFileSupportMain.dependencies {

            // Lumberjack
            api(deps.lumberjack.logger.file)
            implementation(deps.lumberjack.extension.composeviewer)

            // KotPreferences
            api(deps.kotpreferences.storage.datastore)

            // Compose Debug Drawer
            implementation(deps.composedebugdrawer.plugin.lumberjack )

        }

        featureNoFileSupportMain.dependencies {

            // KotPreferences
            api(deps.kotpreferences.storage.keyvalue)

        }

        jvmMain.dependencies {

            // Jewel
            implementation(deps.jewel.int.ui.standalone)
            api(deps.jewel.int.ui.decorated.window)
            implementation(deps.jewel.foundation)
            implementation(deps.jewel.ui)
            implementation(deps.jewel.decorated.window)
            implementation(deps.intellij.platform.icons)
            implementation(deps.skiko)

            api(deps.htmlconverter)

        }

        androidMain.dependencies {

            // KMPPlatformContext
            // implementation(deps.kmp.platformcontext.initializer)

            // Compose Debug Drawer
            implementation( deps.composedebugdrawer.infos.build)
            implementation(deps.composedebugdrawer.infos.device )

        }

        wasmJsMain.dependencies {
        }
    }
}

// -------------------
// Publish
// -------------------

// maven publish configuration
if (BuildFileUtil.checkGradleProperty(project, "publishToMaven") != false)
    BuildFileUtil.setupMavenPublish(project, config, libraryConfig)