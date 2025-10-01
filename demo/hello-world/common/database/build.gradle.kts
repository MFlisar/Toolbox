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
    //alias(androidx.plugins.room)
    //alias(libs.plugins.ksp)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.helloworld.core.database"

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

        val featureLocalDatabaseMain by creating { dependsOn(commonMain.get()) }
        val featureNoLocalDatabaseMain by creating { dependsOn(commonMain.get()) }

        featureLocalDatabaseMain.setupDependencies(sourceSets, buildTargets, Target.LIST_FILE_SUPPORT)
        featureNoLocalDatabaseMain.setupDependencies(sourceSets, buildTargets, Target.LIST_FILE_SUPPORT, targetsNotSupported = true)

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            implementation(compose.components.resources)

            // kotlinx
            implementation(kotlinx.coroutines.core)

            // compose
            implementation(libs.compose.runtime)
            api(libs.compose.components.resources) // needed for kmp resources

            // ------------------------
            // Modules
            // ------------------------

            implementation(project(":demo:hello-world:common:core"))

            // ------------------------
            // Meine Libraries
            // ------------------------

            //implementation(live = deps.composedialogs.core,     project = ":composedialogs:core", plugin = buildFilePlugin)
        }

        featureLocalDatabaseMain.dependencies {
            // room
            //implementation(androidx.room)
            //implementation(androidx.room.runtime)
            //implementation(androidx.sqlite.bundled)
        }
    }
}

//room {
//    schemaDirectory("$projectDir/schemas")
//}
//
//dependencies {
//    // nicht mehr so nutzen wegen Performance Issues
//    //ksp(libs.androidx.room.compiler)
//    add("kspJvm", androidx.room.compiler)
//}

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
