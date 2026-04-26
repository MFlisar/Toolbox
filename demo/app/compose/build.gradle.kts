import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.configs.*
import com.michaelflisar.kmpdevtools.setupDependencies

plugins {
    // kmp + app/library
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    // org.jetbrains.kotlin
    alias(libs.plugins.jetbrains.kotlin.compose)
    // org.jetbrains.compose
    alias(libs.plugins.jetbrains.compose)
    // docs, publishing, validation
    // --
    // build tools
    alias(mflisar.plugins.kmpicon.plugin)
    alias(mflisar.plugins.kmpdevtools.buildplugin)
    // others
    alias(libs.plugins.launch4j)
}

// ------------------------
// Setup
// ------------------------

val module = AppModuleConfig.readManual(project)

val buildTargets = Targets(
    // mobile
    android = true,
    //iOS = true,
    // desktop
    windows = true,
    //macOS = true,
    // web
    wasm = true
)

val desktopConfig = DesktopAppConfig(
    mainClass = "com.michaelflisar.demo.MainKt",
    ico = "icon.ico"
)

val wasmConfig = WasmAppConfig(
    moduleName = "demo",
    outputFileName = "demo.js"
)

kmpIcon {

    setup {
        sourceModule = "demo/app/android" // default: app/app/android"
        sourceFile = "src/main/ic_launcher-playstore.png"
    }

    generateIco {
        file = "icon.ico"
    }

}

// -------------------
// Kotlin
// -------------------

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    //-------------
    // Targets
    //-------------

    buildTargets.setupTargetsApp(module, wasmAppConfig = wasmConfig)

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

            implementation(project(":demo:shared"))

        }

        //jvmMain.dependencies {
//
        //    implementation(compose.desktop.currentOs) {
        //        exclude(group = "org.jetbrains.compose.material", module = "material")
        //    }
//
        //}
    }
}

// -------------------
// Configurations
// -------------------

// windows configuration
compose.desktop {
    application {
        BuildFileUtil.setupWindowsApp(
            appModuleConfig = module,
            application = this,
            desktopAppConfig = desktopConfig
        )
    }
}

// FAT exe
BuildFileUtil.registerLaunch4JTask(
    appModuleConfig = module,
    desktopAppConfig = desktopConfig
)