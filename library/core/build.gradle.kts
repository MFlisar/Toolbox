import com.michaelflisar.kmplibrary.BuildFilePlugin
import com.michaelflisar.kmplibrary.setupDependencies
import com.michaelflisar.kmplibrary.Target
import com.michaelflisar.kmplibrary.Targets
import com.michaelflisar.kmplibrary.api
import com.michaelflisar.kmplibrary.implementation

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
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
            implementation(compose.components.resources)

            // Compose + AndroidX
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)

            // mflisar
            api(live = deps.lumberjack.core, project = ":lumberjack:core", plugin = buildFilePlugin)
            implementation(live = deps.composechangelog.core, project = ":composechangelog:core", plugin = buildFilePlugin)

        }

        androidMain.dependencies {

            implementation(androidx.core)
            api(deps.processphoenix)
            implementation(deps.acra.core)
            implementation(deps.acra.mail)
            implementation(deps.acra.dialog)

            // mflisar
            implementation(live = deps.feedback, project = ":feedbackmanager", plugin = buildFilePlugin)
            implementation(live = deps.lumberjack.extension.feedback, project = ":lumberjack:extensions:feedback", plugin = buildFilePlugin)
            implementation(live = deps.lumberjack.extension.notification, project = ":lumberjack:extensions:notification", plugin = buildFilePlugin)
        }

        featureFileSupportMain.dependencies {

            // mflisar
            implementation(live = deps.lumberjack.logger.file, project = ":lumberjack:loggers:lumberjack:file", plugin = buildFilePlugin)

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



