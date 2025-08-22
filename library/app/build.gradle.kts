import com.michaelflisar.kmplibrary.BuildFilePlugin
import com.michaelflisar.kmplibrary.setupDependencies
import com.michaelflisar.kmplibrary.Target
import com.michaelflisar.kmplibrary.Targets
import com.michaelflisar.kmplibrary.api
import com.michaelflisar.kmplibrary.implementation

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.toolbox.app"

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

// -------------------
// Setup
// -------------------

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

    buildFilePlugin.setupTargetsLibrary(buildTargets)

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        val targetsBackupSupport = listOf(Target.ANDROID, Target.WINDOWS)
        val targetsAndroid = listOf(Target.ANDROID)
        val targetsJvm = listOf(Target.WINDOWS)

        val featureFileSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoFileSupportMain by creating { dependsOn(commonMain.get()) }
        val featureBackupSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoBackupSupportMain by creating { dependsOn(commonMain.get()) }
        val notAndroidMain by creating { dependsOn(commonMain.get()) }
        val notJvmMain by creating { dependsOn(commonMain.get()) }

        featureFileSupportMain.setupDependencies(sourceSets, buildTargets, Target.LIST_FILE_SUPPORT)
        featureNoFileSupportMain.setupDependencies(sourceSets, buildTargets, Target.LIST_FILE_SUPPORT, targetsNotSupported = true)

        featureBackupSupportMain.setupDependencies(sourceSets, buildTargets, targetsBackupSupport)
        featureNoBackupSupportMain.setupDependencies(sourceSets, buildTargets, targetsBackupSupport, targetsNotSupported = true)

        notAndroidMain.setupDependencies(sourceSets, buildTargets, targetsAndroid, targetsNotSupported = true)
        notJvmMain.setupDependencies(sourceSets, buildTargets, targetsJvm, targetsNotSupported = true)

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // resources
            implementation(compose.components.resources)

            // kotlinx
            implementation(kotlinx.serialization.core)
            implementation(kotlinx.serialization.json)

            // Compose + AndroidX
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.compose.ui.backhandler)

            // Compose Icons
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)
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

            /* region mflisar dependencies */
            api(live = deps.lumberjack.core, project = ":lumberjack:core", plugin = buildFilePlugin)
            api(live = deps.lumberjack.implementation.lumberjack, project = ":lumberjack:implementations:lumberjack", plugin = buildFilePlugin)
            api(live = deps.lumberjack.logger.console, project = ":lumberjack:loggers:lumberjack:console", plugin = buildFilePlugin)
            api(live = deps.kotpreferences.core, project = ":kotpreferences:core", plugin = buildFilePlugin)
            api(live = deps.composedebugdrawer.core, project = ":composedebugdrawer:core", plugin = buildFilePlugin)
            implementation(live = deps.composedebugdrawer.plugin.kotpreferences, project = ":composedebugdrawer:plugins:kotpreferences", plugin = buildFilePlugin)
            api(live = deps.composechangelog.core, project = ":composechangelog:core", plugin = buildFilePlugin)
            implementation(live = deps.composechangelog.renderer.header, project = ":composechangelog:modules:renderer:header", plugin = buildFilePlugin)
            implementation(live = deps.composechangelog.statesaver.kotpreferences, project = ":composechangelog:modules:statesaver:kotpreferences", plugin = buildFilePlugin)
            api(live = deps.composedialogs.core, project = ":composedialogs:core", plugin = buildFilePlugin)
            implementation(live = deps.composedialogs.dialog.info, project = ":composedialogs:modules:info", plugin = buildFilePlugin)
            api(live = deps.composepreferences.core, project = ":composepreferences:core", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.screen.bool, project = ":composepreferences:modules:screen:bool", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.screen.list, project = ":composepreferences:modules:screen:list", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.screen.button, project = ":composepreferences:modules:screen:button", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.kotpreferences, project = ":composepreferences:modules:kotpreferences", plugin = buildFilePlugin)
            api(live = deps.kotpreferences.extension.compose, project = ":kotpreferences:modules:compose", plugin = buildFilePlugin)
            api(live = deps.composethemer.core, project = ":composethemer:core", plugin = buildFilePlugin)
            implementation(live = deps.composethemer.modules.picker, project = ":composethemer:modules:picker", plugin = buildFilePlugin)
            implementation(live = deps.composethemer.modules.defaultpicker, project = ":composethemer:modules:defaultpicker", plugin = buildFilePlugin)
            /* endregion */

            implementation(deps.filekit.dialogs.compose)

        }

        featureFileSupportMain.dependencies {

            // mflisar dependencies
            api(live = deps.kotpreferences.storage.datastore, project = ":kotpreferences:modules:storage:datastore", plugin = buildFilePlugin)
            api(live = deps.lumberjack.logger.file, project = ":lumberjack:loggers:lumberjack:file", plugin = buildFilePlugin)
            implementation(live = deps.lumberjack.extension.composeviewer, project = ":lumberjack:extensions:composeviewer", plugin = buildFilePlugin)
            implementation(live = deps.composedebugdrawer.plugin.lumberjack, project = ":composedebugdrawer:plugins:lumberjack", plugin = buildFilePlugin)

        }

        featureNoFileSupportMain.dependencies {

            // mflisar dependencies
            api(live = deps.kotpreferences.storage.keyvalue, project = ":kotpreferences:modules:storage:keyvalue", plugin = buildFilePlugin)
        }


        jvmMain.dependencies {

            // Jewel
            api(deps.jewel.int.ui.standalone)
            api(deps.jewel.int.ui.decorated.window)
            api(deps.jewel.foundation)
            api(deps.jewel.ui)
            api(deps.jewel.decorated.window)
            api(deps.intellij.platform.icons)
            api(deps.skiko)

            api(deps.htmlconverter)

        }

        androidMain.dependencies {
            implementation(live = deps.composedebugdrawer.infos.build, project = ":composedebugdrawer:modules:buildinfos", plugin = buildFilePlugin)
            implementation(live = deps.composedebugdrawer.infos.device, project = ":composedebugdrawer:modules:deviceinfos", plugin = buildFilePlugin)
        }

        wasmJsMain.dependencies {
        }
    }
}

// -------------------
// Configurations
// -------------------

// android configuration
android {

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

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



