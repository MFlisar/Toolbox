import com.michaelflisar.kmplibrary.BuildFilePlugin
import com.michaelflisar.kmplibrary.setupDependencies
import com.michaelflisar.kmplibrary.Target
import com.michaelflisar.kmplibrary.Targets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.toolbox.core"

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

    buildFilePlugin.setupTargetsLibrary(
        targets = buildTargets,
        configMacOS = {
            compilations["main"].cinterops {
                create("macos") {
                    defFile("src/nativeInterop/cinterop/macos.def")
                }
            }
        }
    )

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        val targetsMac = listOf(Target.MACOS)

        val macosMain by creating { dependsOn(commonMain.get()) }
        val featureFileSupportMain by creating { dependsOn(commonMain.get()) }

        macosMain.setupDependencies(sourceSets, buildTargets, targetsMac)
        featureFileSupportMain.setupDependencies(sourceSets, buildTargets, Target.LIST_FILE_SUPPORT)

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // resources
            api(compose.components.resources)

            // kotlinx
            implementation(kotlinx.serialization.core)
            implementation(kotlinx.serialization.json)
            implementation(kotlinx.datetime)

            // Compose + AndroidX
            api(libs.compose.components.resources) // needed for kmp resources
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)

            api(deps.lumberjack.core)
            implementation(deps.composechangelog.core)
            implementation(deps.composedialogs.core)

            implementation(deps.filekit.dialogs.compose)
        }

        androidMain.dependencies {

            implementation(androidx.core)
            api(androidx.activity.compose)
            implementation(androidx.browser)

            api(deps.processphoenix)
            implementation(deps.acra.core)
            implementation(deps.acra.mail)
            implementation(deps.acra.dialog)

            implementation(deps.feedback)
            implementation(deps.lumberjack.extension.feedback)
            implementation(deps.lumberjack.extension.notification)

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

// maven publish configuration
if (buildFilePlugin.checkGradleProperty("publishToMaven") != false)
    buildFilePlugin.setupMavenPublish()




