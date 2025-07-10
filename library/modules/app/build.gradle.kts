import com.michaelflisar.kmpgradletools.BuildFilePlugin
import com.michaelflisar.kmpgradletools.Target
import com.michaelflisar.kmpgradletools.Targets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(deps.plugins.kmp.gradle.tools.gradle.plugin)
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


compose.resources {
    packageOfResClass = "com.michaelflisar.toolbox.app.resources"
    publicResClass = true
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    //-------------
    // Targets
    //-------------

    buildFilePlugin.setupTargetsApp(buildTargets)

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom shared sources
        // ---------------------

        // --
        // e.g.:
        // val nativeMain by creating { dependsOn(commonMain.get()) }

        val featureFileSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoFileSupportMain by creating { dependsOn(commonMain.get()) }

        val iosMain by creating { dependsOn(commonMain.get()) }
        val macosMain by creating { dependsOn(commonMain.get()) }

        val notAndroidMain by creating { dependsOn(commonMain.get()) }
        val notJvmMain by creating { dependsOn(commonMain.get()) }

        // ---------------------
        // target sources
        // ---------------------

        // --
        // e.g.:
        // buildTargets.updateSourceSetDependencies(sourceSets) { groupMain, target ->
        //     when (target) {
        //         Target.ANDROID, Target.WINDOWS -> {
        //             groupMain.dependsOn(nativeMain)
        //         }
        //         Target.IOS, Target.MACOS, Target.WASM -> {
        //             // --
        //         }
        //         Target.LINUX,
        //         Target.JS -> {
        //             // not enabled
        //         }
        //     }
        // }

        buildTargets.updateSourceSetDependencies(sourceSets) { groupMain, target ->
            when (target) {
                Target.ANDROID -> {
                    groupMain.dependsOn(featureFileSupportMain)
                    groupMain.dependsOn(notJvmMain)
                }
                Target.WINDOWS -> {
                    groupMain.dependsOn(featureFileSupportMain)
                    groupMain.dependsOn(notAndroidMain)
                }
                Target.IOS -> {
                    groupMain.dependsOn(featureFileSupportMain)
                    groupMain.dependsOn(iosMain)
                    groupMain.dependsOn(notAndroidMain)
                    groupMain.dependsOn(notJvmMain)
                }
                Target.MACOS -> {
                    groupMain.dependsOn(featureFileSupportMain)
                    groupMain.dependsOn(macosMain)
                    groupMain.dependsOn(notAndroidMain)
                    groupMain.dependsOn(notJvmMain)
                }
                Target.WASM -> {
                    groupMain.dependsOn(featureNoFileSupportMain)
                    groupMain.dependsOn(notAndroidMain)
                    groupMain.dependsOn(notJvmMain)
                }
                Target.LINUX,
                Target.JS -> {
                    // not enabled
                }
            }
        }

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // resources
            implementation(compose.components.resources)

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

            // Library
            api(project(":toolbox:core"))
            api(project(":toolbox:modules:ui"))

            // mflisar dependencies
            api(deps.lumberjack.core)
            api(deps.lumberjack.implementation.lumberjack)
            api(deps.lumberjack.logger.console)
            api(deps.kotpreferences.core)
            api(deps.composedebugdrawer.core)
            implementation(deps.composedebugdrawer.plugin.lumberjack)
            implementation(deps.composedebugdrawer.plugin.kotpreferences)
            api(deps.composechangelog.core)
            implementation(deps.composechangelog.renderer.header)
            implementation(deps.composechangelog.statesaver.kotpreferences)
            api(deps.composedialogs.core)
            implementation(deps.composedialogs.dialog.info)
            api(deps.composepreferences.core)
            implementation(deps.composepreferences.screen.bool)
            implementation(deps.composepreferences.screen.list)
            implementation(deps.composepreferences.screen.button)
            implementation(deps.composepreferences.kotpreferences)
            api(deps.kotpreferences.extension.compose)
            api(deps.composethemer.core)
            implementation(deps.composethemer.modules.picker)

        }

        featureFileSupportMain.dependencies {
            // mflisar dependencies
            api(deps.kotpreferences.storage.datastore)
            api(deps.lumberjack.logger.file)
            implementation(deps.lumberjack.extension.composeviewer)
        }

        featureNoFileSupportMain.dependencies {
            // mflisar dependencies
            api(deps.kotpreferences.storage.keyvalue)
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
            implementation(deps.composedebugdrawer.infos.build)
            implementation(deps.composedebugdrawer.infos.device)
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
    buildFilePlugin.setupAndroidLibrary(
        androidNamespace = androidNamespace,
        compileSdk = app.versions.compileSdk,
        minSdk = app.versions.minSdk,
        buildConfig = false
    )
}

// maven publish configuration
buildFilePlugin.setupMavenPublish()