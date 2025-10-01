import com.michaelflisar.kmplibrary.BuildFilePlugin
import com.michaelflisar.kmplibrary.setupDependencies
import com.michaelflisar.kmplibrary.Target
import com.michaelflisar.kmplibrary.Targets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.helloworld.core"

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

kotlin {

    //-------------
    // Targets
    //-------------

    buildFilePlugin.setupTargetsLibrary(buildTargets)

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
            api(compose.components.resources)

            // kotlinx
            api(kotlinx.coroutines.core)

            // Compose + AndroidX
            api(libs.compose.runtime)
            api(libs.compose.components.resources) // needed for kmp resources
            api(libs.compose.ui)
            api(libs.compose.material3)

            // Compose Icons
            api(libs.compose.material.icons.core)
            api(libs.compose.material.icons.extended)

            // ------------------------
            // Meine + Libraries
            // ------------------------

            if (buildFilePlugin.useLiveDependencies()) {

                // Toolbox
                api(deps.toolbox.core)
                api(deps.toolbox.app)

            } else {

                // Toolbox
                api(":toolbox:core")
                api(":toolbox:app")

            }
        }
    }
}

// -------------------
// Configurations
// -------------------

// android configuration
android {
    buildFilePlugin.setupAndroidLibrary(
        androidNamespace = androidNamespace,
        compileSdk = app.versions.compileSdk,
        minSdk = app.versions.minSdk,
        buildConfig = false
    )
}
