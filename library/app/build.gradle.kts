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
    macOS = false, // dialogs unterstützen dass nicht wegen compose unstyled
    // web
    wasm = true
)

val androidConfig = AndroidLibraryConfig.create(
    libraryModuleConfig = module,
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

        val targetsBackupSupport = listOf(Platform.ANDROID, Platform.WINDOWS)

        val featureFileSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoFileSupportMain by creating { dependsOn(commonMain.get()) }
        val featureBackupSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoBackupSupportMain by creating { dependsOn(commonMain.get()) }
        val notAndroidMain by creating { dependsOn(commonMain.get()) }
        val notJvmMain by creating { dependsOn(commonMain.get()) }
        val mobileMain by creating { dependsOn(commonMain.get()) }
        val notMobileMain by creating { dependsOn(commonMain.get()) }
        val iosMain by creating { dependsOn(commonMain.get()) }

        setupDependencies(module, buildTargets, sourceSets) {

            Platform.IOS addSourceSet iosMain

            featureFileSupportMain supportedBy Platform.LIST_FILE_SUPPORT
            featureNoFileSupportMain supportedBy !Platform.LIST_FILE_SUPPORT

            featureBackupSupportMain supportedBy targetsBackupSupport
            featureNoBackupSupportMain supportedBy !targetsBackupSupport

            mobileMain supportedBy Platform.LIST_MOBILE
            notMobileMain supportedBy !Platform.LIST_MOBILE

            notAndroidMain supportedBy !Platform.ANDROID

            notJvmMain supportedBy !Platform.WINDOWS

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
            api(project(":toolbox:modules:ui-adaptive"))
            api(project(":toolbox:modules:zip"))
            api(project(":toolbox:modules:backup"))

            // Lumberjack
            api(mflisar.lumberjack.core)
            api(mflisar.lumberjack.implementation)
            api(mflisar.lumberjack.logger.console)

            // KotPreferences
            api(mflisar.kotpreferences.core)
            api(mflisar.kotpreferences.extension.compose)

            // Compose Debug Drawer
            api(mflisar.composedebugdrawer.core)
            implementation(mflisar.composedebugdrawer.plugin.kotpreferences)

            // Compose Changelog
            api(mflisar.composechangelog.core)
            implementation(mflisar.composechangelog.renderer.header)
            implementation(mflisar.composechangelog.statesaver.kotpreferences)

            // Compose Dialogs
            api(mflisar.composedialogs.core)
            implementation(mflisar.composedialogs.dialog.info)
            implementation(mflisar.composedialogs.dialog.time)
            implementation(mflisar.composedialogs.dialog.frequency)

            // Compose Preferences
            api(mflisar.composepreferences.core)
            implementation(mflisar.composepreferences.screen.bool)
            implementation(mflisar.composepreferences.screen.list)
            implementation(mflisar.composepreferences.screen.button)
            implementation(mflisar.composepreferences.kotpreferences)

            // Compose Colors
            implementation(mflisar.composecolors.material)

            // Theming
            api(mflisar.composethemer.core)
            implementation(mflisar.composethemer.modules.picker)
            implementation(mflisar.composethemer.modules.defaultpicker)

            implementation(deps.filekit.dialogs.compose)

        }

        featureFileSupportMain.dependencies {

            // Lumberjack
            api(mflisar.lumberjack.logger.file)
            implementation(mflisar.lumberjack.extension.composeviewer)

            // KotPreferences
            api(mflisar.kotpreferences.storage.datastore)

            // Compose Debug Drawer
            implementation(mflisar.composedebugdrawer.plugin.lumberjack )

        }

        featureNoFileSupportMain.dependencies {

            // KotPreferences
            api(mflisar.kotpreferences.storage.keyvalue)

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
            implementation( mflisar.composedebugdrawer.infos.build)
            implementation(mflisar.composedebugdrawer.infos.device )

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
    BuildFileUtil.setupMavenPublish(module)