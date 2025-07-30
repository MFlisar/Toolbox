import com.michaelflisar.kmpgradletools.BuildFilePlugin
import com.michaelflisar.kmpgradletools.Target
import com.michaelflisar.kmpgradletools.Targets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(deps.plugins.kmp.gradle.tools.gradle.plugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.toolbox.service"

val buildTargets = Targets(
    // mobile
    android = true,
    //iOS = true,
    // desktop
    //windows = true,
    //macOS = true,
    // web
    //wasm = true
)

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    //-------------
    // Targets
    //-------------

    buildFilePlugin.setupTargetsLibrary(
        targets = buildTargets
    )

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom shared sources
        // ---------------------

        // --
        // e.g.:
        // val nativeMain by creating { dependsOn(commonMain.get()) }

        // ---------------------
        // target sources
        // ---------------------

        // --
        // e.g.:
        // buildTargets.updateSourceSetDependencies(sourceSets) { groupMain, target ->
        //     when (target) {
        //         Target.ANDROID, Target.WINDOWS -> {
        //             groupMain.dependsOn(nativeMain)
        //         }
        //         Target.IOS, Target.MACOS, Target.WASM -> {
        //             // --
        //         }
        //         Target.LINUX,
        //         Target.JS -> {
        //             // not enabled
        //         }
        //     }
        // }

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            implementation(kotlinx.coroutines.core)

            // Toolbox
            implementation(project(":toolbox:core"))

        }

        androidMain.dependencies {
            implementation(androidx.core)
            implementation(deps.google.guava)
            api(androidx.lifecycle.service)
            api(androidx.work)
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
buildFilePlugin.setupMavenPublish()