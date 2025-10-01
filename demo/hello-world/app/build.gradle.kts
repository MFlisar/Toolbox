import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.DefaultVersionFormatter
import com.michaelflisar.kmplibrary.BuildFilePlugin
import com.michaelflisar.kmplibrary.DesktopSetup
import com.michaelflisar.kmplibrary.Targets
import com.michaelflisar.kmplibrary.setupLaunch4J
import com.michaelflisar.kmplibrary.setupWindowsApp
import com.michaelflisar.kmplibrary.setupExtractProguardMapFromAAB
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose)
    alias(deps.plugins.composechangelog)
    alias(deps.plugins.kmplibrary.buildplugin)
    alias(deps.plugins.kmplibrary.projectplugin)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.launch4j)
    alias(libs.plugins.easylauncher)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// ----------------
// Version
// ----------------

val appVersionName = "0.0.1"
val appVersionCode = Changelog.buildVersionCode(appVersionName, DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatch))

val appName = "Hello World"
val androidNamespace = "com.michaelflisar.helloworld.app"

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

val desktopSetup = DesktopSetup(
    appName = appName,
    appVersionName = appVersionName,
    mainClass = "com.michaelflisar.helloworld.MainKt",
    author = "Michael Flisar",
    ico = "logo.ico"
)

// -------------------
// Setup
// -------------------

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

easylauncher {
    buildTypes {
        register("debug") {
            filters(chromeLike(label = "DEBUG", ribbonColor = "#AA000000"))
        }
    }
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    //-------------
    // Targets
    //-------------

    buildFilePlugin.setupTargetsApp(
        buildTargets,
        wasmModuleName = "demo",
        wasmOutputFileName = "demo.js"
    )

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        // --

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // resources
            implementation(compose.components.resources)

            // kotlinx
            implementation(kotlinx.coroutines.core)

            // compose
            implementation(compose.runtime)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)

            // modules
            implementation(project(":demo:hello-world:common:core"))
            implementation(project(":demo:hello-world:common:database"))
            implementation(project(":demo:hello-world:feature:page1"))
            implementation(project(":demo:hello-world:feature:page2"))

            // themes
            implementation(deps.composethemer.themes.flatui)
            implementation(deps.composethemer.themes.metro)
            implementation(deps.composethemer.themes.material500)

            // images
            implementation(deps.coil.compose)
            implementation(deps.coil.network.ktor3)

        }

        jvmMain.dependencies {

            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material", module = "material")
            }

            // ktor
            implementation(deps.ktor.client.java)

        }

        androidMain.dependencies {

            implementation(androidx.activity.compose)

            implementation(deps.material)

            // ktor
            implementation(deps.ktor.client.android)

            // toolbox
            implementation(deps.toolbox.proversion)
            implementation(deps.toolbox.ads)

            // ADS:
            // - 1) App ID im Manifest eintragen
            // - 2) AdManager.init(activity) in MainActivity.onCreate aufrufen
            // - 3) FooterAdsBanner im Footer platzieren
            // - 4) AdsManager in Application konfigurieren
            // INFOS: https://github.com/LexiLabs-App/basic-ads
            // PRO VERSION
            // - 1) ProVersionManager in Application konfigurieren
            if (buildFilePlugin.useLiveDependencies()) {
                implementation(deps.toolbox.proversion)
                implementation(deps.toolbox.ads)
            } else {
                implementation(project(":toolbox:modules:ads"))
                implementation(project(":toolbox:modules:proversion"))
            }

        }

        nativeMain.dependencies {

            // ktor
            implementation(deps.ktor.client.darwin)

        }

        wasmJsMain.dependencies {

            // ktor
            // not neccessary!

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

    // res/resources.properties file erstellen!
    androidResources {
        generateLocaleConfig = true
    }

    buildFilePlugin.setupAndroidApp(
        androidNamespace = androidNamespace,
        compileSdk = app.versions.compileSdk,
        minSdk = app.versions.minSdk,
        targetSdk = app.versions.targetSdk,
        versionCode = appVersionCode,
        versionName = appVersionName,
        buildConfig = true,
        checkDebugKeyStoreProperty = true
    )

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
        }
    }
}

// windows configuration
compose.desktop {
    application {

        // BuildFilePlugin Extension
        setupWindowsApp(
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
// TODO: Plugin anpassen, sollte eher wie folgt nutzbar sein:
// oder zumindest project.setupLaunch4J anbieten?
// ---------
/*
projectPlugin {

    setup = desktopSetup
    appName = appName
    appVersionName = appVersionName

    setupLaunch4J = true
    setupExtractProguardMapFromAAB = true
}*/

// ---------
// FAT exe
// ---------

tasks.register<edu.sc.seis.launch4j.tasks.Launch4jLibraryTask>("launch4j") {

    // BuildFilePlugin Extension
    setupLaunch4J(
        setup = desktopSetup
    )
}

// ------------------------
// Proguard Map aus AAB extrahieren + im release Ordner abspeichern
// ------------------------

project.setupExtractProguardMapFromAAB(
    appName,
    appVersionName
)