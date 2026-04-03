import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.configs.*

plugins {
    // kmp + app/library
    alias(libs.plugins.android.application)
    // org.jetbrains.kotlin
    alias(libs.plugins.jetbrains.kotlin.compose)
    // org.jetbrains.compose
    // --
    // docs, publishing, validation
    // --
    // build tools
    alias(deps.plugins.kmpdevtools.buildplugin)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val module = AppModuleConfig.readManual(project)

val androidConfig = AndroidAppConfig(
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    targetSdk = app.versions.targetSdk
)

// -------------------
// Configurations
// -------------------

// android configuration
android {

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    BuildFileUtil.setupAndroidApp(
        appModuleConfig = module,
        androidAppConfig = androidConfig,
        generateResAppName = true,
        buildConfig = true,
        checkDebugKeyStoreProperty = true,
        setupBuildTypesDebugAndRelease = true
    )
}

dependencies {

    // desugar
    coreLibraryDesugaring(libs.desugar)

    // Library
    implementation(project(":demo:app:compose"))
}

// ------------------------
// Proguard Map aus AAB extrahieren + im release Ordner abspeichern
// ------------------------

BuildFileUtil.registerExtractProguardMapFromAABTask(
    appModuleConfig = module
)