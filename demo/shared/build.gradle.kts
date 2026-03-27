import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.configs.library.AndroidLibraryConfig
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.core.configs.AppConfig
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
    alias(libs.plugins.buildkonfig)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val config = Config.read(rootProject)
val libraryConfig = LibraryConfig.read(rootProject)
val appConfig = AppConfig.read(rootProject)

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

val androidConfig = AndroidLibraryConfig.createManualNamespace(
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = true,
    namespaceAddon = "demo.shared"
)

// ------------------------
// Kotlin
// ------------------------

buildkonfig {
    packageName = appConfig.packageName
    exposeObjectWithName = "BuildKonfig"
    defaultConfigs {
        buildConfigField(Type.STRING, "versionName", appConfig.versionName)
        buildConfigField(Type.INT, "versionCode", appConfig.versionCode.toString())
        buildConfigField(Type.STRING, "packageName", appConfig.packageName)
        buildConfigField(Type.STRING, "appName", appConfig.name)
    }
}

compose.resources {
    packageOfResClass = "${libraryConfig.library.namespace}.demo.shared.resources"
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

        val featureFileSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoFileSupportMain by creating { dependsOn(commonMain.get()) }
        val mobileMain by creating { dependsOn(commonMain.get()) }

        setupDependencies(buildTargets, sourceSets) {

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
            api(deps.kotpreferences.core)
            api(deps.kotpreferences.extension.compose)

            // Compose Dialogs
            api(deps.composedialogs.core)
            api(deps.composedialogs.dialog.color)
            api(deps.composedialogs.dialog.date)
            api(deps.composedialogs.dialog.time)
            api(deps.composedialogs.dialog.info)
            api(deps.composedialogs.dialog.progress)
            api(deps.composedialogs.dialog.input)
            api(deps.composedialogs.dialog.number)
            api(deps.composedialogs.dialog.list)
            api(deps.composedialogs.dialog.menu)
            api(deps.composedialogs.dialog.frequency)

            // Compose Preferences
            api(deps.composepreferences.core)
            api(deps.composepreferences.screen.bool)
            api(deps.composepreferences.screen.button)
            api(deps.composepreferences.screen.input)
            api(deps.composepreferences.screen.color)
            api(deps.composepreferences.screen.date)
            api(deps.composepreferences.screen.time)
            api(deps.composepreferences.screen.list)
            api(deps.composepreferences.screen.number)
            api(deps.composepreferences.kotpreferences)

            // Compose Themer
            api(deps.composethemer.themes.flatui)
            api(deps.composethemer.themes.metro)
            api(deps.composethemer.themes.material500)

        }

        featureFileSupportMain.dependencies {

            api(deps.kotpreferences.storage.datastore)

        }

        featureNoFileSupportMain.dependencies {

            api(deps.kotpreferences.storage.keyvalue)

        }

        jvmMain.dependencies {

            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material", module = "material")
            }

        }

        androidMain.dependencies {

            api(libs.androidx.activity.compose)
            api(libs.jetbrains.compose.material3)

            //implementation(deps.material)

            // PRO VERSION
            // - 1) ProVersionManager in Application konfigurieren
            api(project(":toolbox:modules:proversion"))

        }

        mobileMain.dependencies {

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