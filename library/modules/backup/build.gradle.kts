import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.configs.*
import com.michaelflisar.kmpdevtools.core.Target
import com.michaelflisar.kmpdevtools.setupDependencies

plugins {
    // kmp + app/library
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library.kmp)
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
    alias(mflisar.plugins.kmpdevtools.buildplugin)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val module = LibraryModuleConfig.read(project)

val buildTargets = Targets(
    Target.ANDROID,
    Target.IOS,
    Target.JVM,
    Target.WASM,
    Target.MACOS
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
        androidResources {
            enable = true
        }
    }

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        val targetsBackupSupport = listOf(Target.ANDROID, Target.JVM)

        val notAndroidMain = create("notAndroidMain") { dependsOn(commonMain.get()) }
        val featureBackupSupportMain = create("featureBackupSupportMain") { dependsOn(commonMain.get()) }
        val featureNoBackupSupportMain = create("featureNoBackupSupportMain") { dependsOn(commonMain.get()) }

        setupDependencies(module, buildTargets, sourceSets) {

            notAndroidMain supportedBy !Target.ANDROID
            featureBackupSupportMain supportedBy targetsBackupSupport
            featureNoBackupSupportMain supportedBy !targetsBackupSupport

        }

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // kotlinx
            implementation(libs.jetbrains.kotlinx.serialization.json)
            implementation(libs.jetbrains.kotlinx.datetime)

            // compose
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.ui)

            // Toolbox
            implementation(project(":toolbox:core"))
            implementation(project(":toolbox:modules:zip"))

            api(mflisar.kmp.parcelize)

            implementation(mflisar.lumberjack.core)
            //implementation(mflisar.composedialogs.core)
            //implementation(mflisar.composedialogs.dialog.info)
            //api(mflisar.composedialogs.dialog.frequency)

            api(mflisar.composestyled.libraries.dialogs)

            // libraries
            implementation(deps.filekit.dialogs.compose)

        }

        androidMain.dependencies {

            // androidX
            implementation(libs.androidx.core)
            implementation(libs.androidx.work.runtime)
            implementation(libs.androidx.activity.compose)

            // libraries
            implementation(deps.accompanist.permission)

            // toolbox
            implementation(project(":toolbox:modules:service"))
        }
    }
}

// -------------------
// Publish
// -------------------

BuildFileUtil.setupMavenPublish(module)