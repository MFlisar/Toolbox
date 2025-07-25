import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.DefaultVersionFormatter
import com.michaelflisar.kmpgradletools.BuildFilePlugin
import com.michaelflisar.kmpgradletools.Targets
import com.michaelflisar.kmpgradletools.Target
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import kotlin.jvm.java
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose)
    alias(deps.plugins.composechangelog)
    alias(deps.plugins.kmp.gradle.tools.gradle.plugin)
    alias(libs.plugins.buildkonfig)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val versionName = "0.0.1"
val versionCode = Changelog.buildVersionCode(versionName, DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatch))

val appName = "Toolbox Demo"
val androidNamespace = "com.michaelflisar.toolbox.demo"

val buildTargets = Targets(
    // mobile
    android = true,
    //iOS = true,
    // desktop
    windows = true,
    //macOS = true,
    // web
    wasm = true
)

// -------------------
// Setup
// -------------------

compose.resources {
    packageOfResClass = "com.michaelflisar.toolbox.demo.resources"
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

buildkonfig {
    packageName = androidNamespace
    defaultConfigs {
        buildConfigField(Type.STRING, "versionName", versionName)
        buildConfigField(Type.INT, "versionCode", versionCode.toString())
        buildConfigField(Type.STRING, "packageName", androidNamespace)
    }
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    //-------------
    // Targets
    //-------------

    buildFilePlugin.setupTargetsApp(buildTargets, wasmModuleName = "demo", wasmOutputFileName = "demo.js")

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
                Target.ANDROID, Target.WINDOWS, Target.IOS, Target.MACOS, -> {
                    groupMain.dependsOn(featureFileSupportMain)
                }

                Target.WASM -> {
                    groupMain.dependsOn(featureNoFileSupportMain)
                }

                Target.LINUX,
                Target.JS
                    -> {
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

            // compose
            implementation(compose.runtime)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)

            // modules
            implementation(project(":toolbox:core"))
            implementation(project(":toolbox:app"))
            implementation(project(":toolbox:modules:table"))

            // themes
            implementation(deps.composethemer.themes.flatui)
            implementation(deps.composethemer.themes.metro)
            implementation(deps.composethemer.themes.material500)
        }

        //featureFileSupportMain.dependencies {
        //    // libraries
        //    implementation(deps.composechangelog.core)
        //}

        //featureNoFileSupportMain.dependencies {
        //}

        jvmMain.dependencies {

            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material", module = "material")
            }

        }

        androidMain.dependencies {

            implementation(androidx.activity.compose)

            implementation(deps.material)

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

    buildFilePlugin.setupAndroidApp(
        androidNamespace = androidNamespace,
        compileSdk = app.versions.compileSdk,
        minSdk = app.versions.minSdk,
        targetSdk = app.versions.targetSdk,
        versionCode = versionCode,
        versionName = versionName,
        buildConfig = true
    )

    // eventually use local custom signing
    val debugKeyStore = providers.gradleProperty("debugKeyStore").orNull
    if (debugKeyStore != null) {
        signingConfigs {
            getByName("debug") {
                keyAlias = "androiddebugkey"
                keyPassword = "android"
                storeFile = File(debugKeyStore)
                storePassword = "android"
            }
        }
    }
}

// windows configuration
compose.desktop {
    application {
        mainClass = "com.michaelflisar.toolbox.demo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = appName
            packageVersion = versionName
        }
    }
}