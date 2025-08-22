import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.DefaultVersionFormatter
import com.michaelflisar.kmplibrary.BuildFilePlugin
import com.michaelflisar.kmplibrary.setupDependencies
import com.michaelflisar.kmplibrary.Targets
import com.michaelflisar.kmplibrary.Target
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import kotlin.jvm.java
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import com.michaelflisar.kmplibrary.DesktopSetup
import com.michaelflisar.kmplibrary.api
import com.michaelflisar.kmplibrary.implementation
import com.michaelflisar.kmplibrary.setupLaunch4J
import com.michaelflisar.kmplibrary.setupWindowApp

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose)
    alias(deps.plugins.composechangelog)
    alias(deps.plugins.kmplibrary.buildplugin)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.launch4j)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val appVersionName = "0.0.1"
val appVersionCode = Changelog.buildVersionCode(appVersionName, DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatch))

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

val desktopSetup = DesktopSetup(
    appName = appName,
    appVersionName = appVersionName,
    mainClass = "com.michaelflisar.toolbox.demo.MainKt",
    author = "Michael Flisar",
    ico = "logo.ico"
)

// -------------------
// Setup
// -------------------

compose.resources {
    packageOfResClass = "$androidNamespace.resources"
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

buildkonfig {
    packageName = androidNamespace
    defaultConfigs {
        buildConfigField(Type.STRING, "versionName", appVersionName)
        buildConfigField(Type.INT, "versionCode", appVersionCode.toString())
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
        // custom source sets
        // ---------------------

        val featureFileSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoFileSupportMain by creating { dependsOn(commonMain.get()) }

        featureFileSupportMain.setupDependencies(sourceSets, buildTargets, Target.LIST_FILE_SUPPORT)
        featureNoFileSupportMain.setupDependencies(sourceSets, buildTargets, Target.LIST_FILE_SUPPORT, targetsNotSupported = true)

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

            // ------------------------
            // tests
            // ------------------------

            // kotpreferences
            implementation(live = deps.kotpreferences.core, project = ":kotpreferences:core", plugin = buildFilePlugin)
            implementation(live = deps.kotpreferences.extension.compose, project = ":kotpreferences:modules:compose", plugin = buildFilePlugin)

            // composedialogs
            implementation(live = deps.composedialogs.core, project = ":composedialogs:core", plugin = buildFilePlugin)
            implementation(live = deps.composedialogs.dialog.color, project = ":composedialogs:modules:color", plugin = buildFilePlugin)
            implementation(live = deps.composedialogs.dialog.date, project = ":composedialogs:modules:date", plugin = buildFilePlugin)
            implementation(live = deps.composedialogs.dialog.time, project = ":composedialogs:modules:time", plugin = buildFilePlugin)
            implementation(live = deps.composedialogs.dialog.info, project = ":composedialogs:modules:info", plugin = buildFilePlugin)
            implementation(live = deps.composedialogs.dialog.progress, project = ":composedialogs:modules:progress", plugin = buildFilePlugin)
            implementation(live = deps.composedialogs.dialog.input, project = ":composedialogs:modules:input", plugin = buildFilePlugin)
            implementation(live = deps.composedialogs.dialog.number, project = ":composedialogs:modules:number", plugin = buildFilePlugin)
            implementation(live = deps.composedialogs.dialog.list, project = ":composedialogs:modules:list", plugin = buildFilePlugin)
            implementation(live = deps.composedialogs.dialog.menu, project = ":composedialogs:modules:menu", plugin = buildFilePlugin)

            // composepreferences
            implementation(live = deps.composepreferences.core, project = ":composepreferences:core", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.screen.bool, project = ":composepreferences:modules:screen:bool", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.screen.button, project = ":composepreferences:modules:screen:button", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.screen.input, project = ":composepreferences:modules:screen:input", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.screen.color, project = ":composepreferences:modules:screen:color", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.screen.date, project = ":composepreferences:modules:screen:date", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.screen.time, project = ":composepreferences:modules:screen:time", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.screen.list, project = ":composepreferences:modules:screen:list", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.screen.number, project = ":composepreferences:modules:screen:number", plugin = buildFilePlugin)
            implementation(live = deps.composepreferences.kotpreferences, project = ":composepreferences:modules:kotpreferences", plugin = buildFilePlugin)
        }

        featureFileSupportMain.dependencies {
            implementation(live = deps.kotpreferences.storage.datastore, project = ":kotpreferences:modules:storage:datastore", plugin = buildFilePlugin)
        }
        featureNoFileSupportMain.dependencies {
            implementation(deps.kotpreferences.storage.keyvalue, project = ":kotpreferences:modules:storage:keyvalue", plugin = buildFilePlugin)
        }

        jvmMain.dependencies {

            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material", module = "material")
            }

        }

        androidMain.dependencies {

            implementation(androidx.activity.compose)

            implementation(deps.material)

        }

        featureFileSupportMain.dependencies {
            //implementation(live = deps.kotpreferences.storage.datastore, project = ":kotpreferences:modules:storage:datastore", plugin = buildFilePlugin)
        }
        featureNoFileSupportMain.dependencies {
            //implementation(deps.kotpreferences.storage.keyvalue, project = ":kotpreferences:modules:storage:keyvalue", plugin = buildFilePlugin)
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
        versionCode = appVersionCode,
        versionName = appVersionName,
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

        // BuildFilePlugin Extension
        setupWindowApp(
            project = project,
            setup = desktopSetup,
            configNativeDistribution = {

                // targets
                targetFormats(TargetFormat.Exe)

                // proguard
                buildTypes.release.proguard {
                    version.set("7.7.0")
                    // geht noch nicht, die config ist nicht korrekt
                    isEnabled.set(false)
                    configurationFiles.from(project.file("proguard-rules.pro"))
                }

            }
        )
    }
}

// ---------
// FAT exe
// ---------

tasks.register<edu.sc.seis.launch4j.tasks.Launch4jLibraryTask>("launch4j") {
    // BuildFilePlugin Extension
    setupLaunch4J(
        setup = desktopSetup
    )
}





