import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.configs.*
import com.michaelflisar.kmpdevtools.setupDependencies
import com.michaelflisar.kmpdevtools.setupBuildKonfig

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
    // --
    // build tools
    alias(mflisar.plugins.kmpicon.plugin)
    alias(mflisar.plugins.kmpdevtools.buildplugin)
    alias(libs.plugins.buildkonfig)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val module = LibraryModuleConfig.readManual(project)

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

val androidConfig = AndroidLibraryConfig.createFromPath(
    libraryModuleConfig = module,
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = true
)

kmpIcon {

    setup {
        sourceModule = "demo/app/android" // default: app/app/android"
        sourceFile = "src/main/ic_launcher-playstore.png"
    }

    generateCommonIcon {
        file = "src/commonMain/composeResources/drawable/icon.png"
    }
}

// ------------------------
// Kotlin
// ------------------------

buildkonfig {
    setupBuildKonfig(module.appConfig)
}

compose.resources {
    packageOfResClass = "${module.projectNamespace}.demo.shared.resources"
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
        val featureNoFileSupportMain by creating { dependsOn(commonMain.get()) }
        val mobileMain by creating { dependsOn(commonMain.get()) }

        setupDependencies(module, buildTargets, sourceSets) {

            featureFileSupportMain supportedBy Platform.LIST_FILE_SUPPORT
            featureNoFileSupportMain supportedBy !Platform.LIST_FILE_SUPPORT

            mobileMain supportedBy Platform.LIST_MOBILE

        }

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // compose
            api(libs.jetbrains.compose.components.resources)
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.material.icons.core)
            api(libs.jetbrains.compose.material.icons.extended)

            // modules
            api(project(":toolbox:core"))
            api(project(":toolbox:app"))
            api(project(":toolbox:modules:table"))
            api(project(":toolbox:modules:form"))

            // ------------------------
            // tests
            // ------------------------

            // KotPreferences
            api(mflisar.kotpreferences.core)
            api(mflisar.kotpreferences.extension.compose)

            // Compose Dialogs
            api(mflisar.composedialogs.core)
            api(mflisar.composedialogs.dialog.color)
            api(mflisar.composedialogs.dialog.date)
            api(mflisar.composedialogs.dialog.time)
            api(mflisar.composedialogs.dialog.info)
            api(mflisar.composedialogs.dialog.progress)
            api(mflisar.composedialogs.dialog.input)
            api(mflisar.composedialogs.dialog.number)
            api(mflisar.composedialogs.dialog.list)
            api(mflisar.composedialogs.dialog.menu)
            api(mflisar.composedialogs.dialog.frequency)

            // Compose Preferences
            api(mflisar.composepreferences.core)
            api(mflisar.composepreferences.screen.bool)
            api(mflisar.composepreferences.screen.button)
            api(mflisar.composepreferences.screen.input)
            api(mflisar.composepreferences.screen.color)
            api(mflisar.composepreferences.screen.date)
            api(mflisar.composepreferences.screen.time)
            api(mflisar.composepreferences.screen.list)
            api(mflisar.composepreferences.screen.number)
            api(mflisar.composepreferences.kotpreferences)

            // Compose Themer
            api(mflisar.composethemer.themes.flatui)
            api(mflisar.composethemer.themes.metro)
            api(mflisar.composethemer.themes.material500)

        }

        featureFileSupportMain.dependencies {

            api(mflisar.kotpreferences.storage.datastore)

        }

        featureNoFileSupportMain.dependencies {

            api(mflisar.kotpreferences.storage.keyvalue)

        }

        androidMain.dependencies {

            api(libs.androidx.activity.compose)
            api(libs.jetbrains.compose.material3)

        }

        mobileMain.dependencies {

            // PRO VERSION
            // - 1) ProVersionManager in Application konfigurieren
            api(project(":toolbox:modules:proversion"))

            // ADS:
            // - 1) App ID im Manifest eintragen
            // - 2) AdManager.init(activity) in MainActivity.onCreate aufrufen
            // - 3) FooterAdsBanner im Footer platzieren
            // - 4) AdsManager in Application konfigurieren
            // INFOS: https://github.com/LexiLabs-App/basic-ads
            api(project(":toolbox:modules:ads"))

        }
    }
}