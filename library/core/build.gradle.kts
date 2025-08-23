import com.michaelflisar.kmplibrary.BuildFilePlugin
import com.michaelflisar.kmplibrary.setupDependencies
import com.michaelflisar.kmplibrary.Target
import com.michaelflisar.kmplibrary.Targets

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

            if (buildFilePlugin.useLiveDependencies()) {
                api(deps.lumberjack.core)
                implementation(deps.composechangelog.core)
            } else {
                api(project( ":lumberjack:core"))
                implementation(project(":composechangelog:core"))
            }

        }

        androidMain.dependencies {

            implementation(androidx.core)
            implementation(androidx.activity.compose)

            api(deps.processphoenix)
            implementation(deps.acra.core)
            implementation(deps.acra.mail)
            implementation(deps.acra.dialog)

            if (buildFilePlugin.useLiveDependencies()) {
                implementation(deps.feedback)
                implementation(deps.lumberjack.extension.feedback)
                implementation(deps.lumberjack.extension.notification)
            } else {
                implementation(project(":feedbackmanager"))
                implementation(project(":lumberjack:extensions:feedback"))
                implementation(project(":lumberjack:extensions:notification"))
            }

        }

        featureFileSupportMain.dependencies {

            if (buildFilePlugin.useLiveDependencies()) {
                implementation(deps.lumberjack.logger.file )
            } else {
                implementation(project(":lumberjack:loggers:lumberjack:file"))
            }

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




