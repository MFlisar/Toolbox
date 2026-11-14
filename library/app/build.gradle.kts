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
    alias(libs.plugins.jetbrains.kotlin.serialization)
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
    Target.WASM
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
    }

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        val featureFileSupportMain = create("featureFileSupportMain") { dependsOn(commonMain.get()) }
        val featureNoFileSupportMain = create("featureNoFileSupportMain") { dependsOn(commonMain.get()) }

        val iosMain = create("iosMain") { dependsOn(commonMain.get()) }
        val notAndroidMain = create("notAndroidMain") { dependsOn(commonMain.get()) }
        val notJvmMain = create("notJvmMain") { dependsOn(commonMain.get()) }

        setupDependencies(module, buildTargets, sourceSets) {

            Target.IOS addSourceSet iosMain

            featureFileSupportMain supportedBy Target.LIST_FILE_SUPPORT
            featureNoFileSupportMain supportedBy !Target.LIST_FILE_SUPPORT

            notAndroidMain supportedBy !Target.ANDROID
            notJvmMain supportedBy !Target.JVM

        }

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {
            
            // kotlinx         
            implementation(libs.jetbrains.kotlinx.coroutines.core)
            implementation(libs.jetbrains.kotlinx.serialization.json)
            implementation(libs.jetbrains.kotlinx.datetime)

            // Compose + AndroidX
            implementation(libs.jetbrains.compose.ui.backhandler)
            api(libs.jetbrains.androidx.lifecycle.runtime.compose)
            api(libs.jetbrains.androidx.navigation3.ui)

            // Compose Icons - ÜBERLEGEN! eher entfernen!
            implementation(libs.jetbrains.compose.material.icons.core)
            implementation(libs.jetbrains.compose.material.icons.extended)
            implementation(deps.icons.fontawesome)

            api(project(":toolbox:core"))
            api(project(":toolbox:modules:zip"))
            api(project(":toolbox:modules:backup"))

            // Lumberjack
            api(mflisar.lumberjack.core)
            api(mflisar.lumberjack.logger.console)

            // KotPreferences
            api(mflisar.kotpreferences.core)
            api(mflisar.kotpreferences.extension.compose)

            // Compose Styled (inkl. dialogs/preferences)
            api(mflisar.composestyled.core)
            api(mflisar.composestyled.theme.pastel)
            api(mflisar.composestyled.theme.nature)
            api(mflisar.composestyled.libraries.dialogs)
            api(mflisar.composestyled.libraries.preferences)
            api(mflisar.composestyled.libraries.changelog)

            // Compose Colors
            //implementation(mflisar.composecolors.material)

            implementation(deps.filekit.dialogs.compose)

        }

        featureFileSupportMain.dependencies {

            // Lumberjack
            api(mflisar.lumberjack.logger.file)
            implementation(mflisar.lumberjack.extension.composeviewer)

            // KotPreferences
            api(mflisar.kotpreferences.storage.datastore)
        }

        featureNoFileSupportMain.dependencies {

            // KotPreferences
            api(mflisar.kotpreferences.storage.keyvalue)

        }

        androidMain.dependencies {

            // KMPPlatformContext
            // implementation(deps.kmp.platformcontext.initializer)

            // Compose Styled
            implementation(mflisar.composestyled.material3)

        }

        iosMain.dependencies {

            // Compose Styled
            implementation(mflisar.composestyled.cupertino.flat)

        }

        jvmMain.dependencies {

            // application => do not add => on maven this adds all supported platforms as dependencies, which is not what we want, to the -jvm artifacts!
            //implementation(compose.desktop.currentOs) {
            //    exclude(group = "org.jetbrains.compose.material", module = "material")
            //}

            // Compose Styled
            implementation(mflisar.composestyled.fluent2)

        }

        wasmJsMain.dependencies {

            // Compose Styled
            implementation(mflisar.composestyled.fluent2)

        }
    }
}

// -------------------
// Publish
// -------------------

BuildFileUtil.setupMavenPublish(module)