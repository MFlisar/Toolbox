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
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.toolbox.backup"

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
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

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

        val targetsBackupSupport = listOf(Target.ANDROID, Target.WINDOWS)
        val targetsAndroid = listOf(Target.ANDROID)

        val notAndroidMain by creating { dependsOn(commonMain.get()) }
        val featureNoBackupSupportMain by creating { dependsOn(commonMain.get()) }

        notAndroidMain.setupDependencies(sourceSets, buildTargets, targetsAndroid, targetsNotSupported = true)
        featureNoBackupSupportMain.setupDependencies(sourceSets, buildTargets, targetsBackupSupport, targetsNotSupported = true)

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            implementation(compose.components.resources)

            // kotlinx
            implementation(kotlinx.serialization.json)
            implementation(kotlinx.datetime)

            // compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)

            // Toolbox
            implementation(project(":toolbox:core"))
            implementation(project(":toolbox:modules:zip"))

            api(deps.kmp.parcelize)

            if (buildFilePlugin.useLiveDependencies()) {
                implementation(deps.lumberjack.core)
                implementation(deps.composedialogs.core)
                implementation(deps.composedialogs.dialog.info)
                api(deps.composedialogs.dialog.frequency)
            } else {
                implementation(project(":lumberjack:core"))
                implementation(project(":composedialogs:core"))
                implementation(project(":composedialogs:modules:info"))
                api(project(":composedialogs:modules:frequency"))
            }

            // libraries
            implementation(deps.filekit.dialogs.compose)

        }

        androidMain.dependencies {

            // androidX
            implementation(androidx.core)
            implementation(androidx.work)
            implementation(androidx.activity.compose)

            // libraries
            implementation(deps.accompanist.permission)

            // toolbox
            implementation(project(":toolbox:modules:service"))
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




