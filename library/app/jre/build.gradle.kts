import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.configs.*
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.setupDependencies

plugins {
    // kmp + app/library
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    //alias(libs.plugins.android.library)
    // org.jetbrains.kotlin
    alias(libs.plugins.jetbrains.kotlin.compose)
    //alias(libs.plugins.jetbrains.kotlin.parcelize)
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
    windows = true
)

// ------------------------
// Kotlin
// ------------------------

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    //-------------
    // Targets
    //-------------

    buildTargets.setupTargetsLibrary(module)

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

            // kotlinx
            implementation(libs.jetbrains.kotlinx.coroutines.core)
            implementation(libs.jetbrains.kotlinx.serialization.json)
            implementation(libs.jetbrains.kotlinx.datetime)

            // Compose + AndroidX
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.material3.adaptive)
            implementation(libs.jetbrains.compose.ui.backhandler)
            api(libs.jetbrains.androidx.lifecycle.runtime.compose)

            // Compose Icons
            implementation(libs.jetbrains.compose.material.icons.core)
            implementation(libs.jetbrains.compose.material.icons.extended)
            implementation(deps.icons.fontawesome)

            // Navigation
            api(deps.voyager.navigator)
            api(deps.voyager.transitions)
            api(deps.voyager.screenmodel)

            // Library
            api(project(":toolbox:app:shared"))

            implementation(deps.filekit.dialogs.compose)

        }

        jvmMain.dependencies {

            // application => do not add => on maven this adds all supported platforms as dependencies, which is not what we want, to the -jvm artifacts!
            //implementation(compose.desktop.currentOs) {
            //    exclude(group = "org.jetbrains.compose.material", module = "material")
            //}

        }
    }
}

// -------------------
// Publish
// -------------------

// maven publish configuration
if (BuildFileUtil.checkGradleProperty(project, "publishToMaven") != false)
    BuildFileUtil.setupMavenPublish(module)