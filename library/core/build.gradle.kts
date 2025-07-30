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

val androidNamespace = "com.michaelflisar.toolbox.core"

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

compose.resources {
    packageOfResClass = "$androidNamespace.resources"
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

    buildFilePlugin.setupTargetsLibrary(
        targets = buildTargets,
        configMacOS = {
            compilations["main"].cinterops {
                create("macos") {
                    defFile("src/nativeInterop/cinterop/macos.def")
                }
            }
        }
    )

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
        val macosMain by creating { dependsOn(commonMain.get()) }
        val featureFileSupportedMain by creating { dependsOn(commonMain.get()) }

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
                    groupMain.dependsOn(featureFileSupportedMain)
                }
                Target.WINDOWS -> {
                    groupMain.dependsOn(featureFileSupportedMain)
                }
                Target.IOS-> {
                    groupMain.dependsOn(featureFileSupportedMain)
                }
                Target.MACOS -> {
                    groupMain.dependsOn(macosMain)
                    groupMain.dependsOn(featureFileSupportedMain)
                }
                Target.WASM -> {

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
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)

            // mflisar
            api(deps.lumberjack.core)
            implementation(deps.composechangelog.core)

        }

        androidMain.dependencies {

            implementation(androidx.core)
            api(deps.processphoenix)
            implementation(deps.acra.core)
            implementation(deps.acra.mail)
            implementation(deps.acra.dialog)

            // mflisar
            implementation(deps.feedback)
            implementation(deps.lumberjack.extension.feedback)
            implementation(deps.lumberjack.extension.notification)
        }

        featureFileSupportedMain.dependencies {

            // mflisar
            implementation(deps.lumberjack.logger.file)

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
buildFilePlugin.setupMavenPublish()