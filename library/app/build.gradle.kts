import com.michaelflisar.kmplibrary.BuildFilePlugin
import com.michaelflisar.kmplibrary.Target
import com.michaelflisar.kmplibrary.Targets
import com.michaelflisar.kmplibrary.setupDependencies
import org.gradle.kotlin.dsl.project

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.toolbox.app"

val buildTargets = Targets(
    // mobile
    android = true,
    iOS = true,
    // desktop
    windows = true,
    macOS = false, // dialogs unterstützen dass nicht wegen compose unstyled
    // web
    wasm = true
)

// -------------------
// Setup
// -------------------

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
        val targetsJvm = listOf(Target.WINDOWS)

        val featureFileSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoFileSupportMain by creating { dependsOn(commonMain.get()) }
        val featureBackupSupportMain by creating { dependsOn(commonMain.get()) }
        val featureNoBackupSupportMain by creating { dependsOn(commonMain.get()) }
        val notAndroidMain by creating { dependsOn(commonMain.get()) }
        val notJvmMain by creating { dependsOn(commonMain.get()) }

        featureFileSupportMain.setupDependencies(sourceSets, buildTargets, Target.LIST_FILE_SUPPORT)
        featureNoFileSupportMain.setupDependencies(
            sourceSets,
            buildTargets,
            Target.LIST_FILE_SUPPORT,
            targetsNotSupported = true
        )

        featureBackupSupportMain.setupDependencies(sourceSets, buildTargets, targetsBackupSupport)
        featureNoBackupSupportMain.setupDependencies(
            sourceSets,
            buildTargets,
            targetsBackupSupport,
            targetsNotSupported = true
        )

        notAndroidMain.setupDependencies(
            sourceSets,
            buildTargets,
            targetsAndroid,
            targetsNotSupported = true
        )
        notJvmMain.setupDependencies(
            sourceSets,
            buildTargets,
            targetsJvm,
            targetsNotSupported = true
        )

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // resources
            implementation(compose.components.resources)

            // kotlinx
            implementation(kotlinx.serialization.core)
            implementation(kotlinx.serialization.json)
            implementation(kotlinx.datetime)

            // Compose + AndroidX
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.compose.ui.backhandler)

            // Compose Icons
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)
            implementation(deps.icons.fontawesome)

            // Navigation
            api(deps.voyager.navigator)
            api(deps.voyager.transitions)
            api(deps.voyager.screenmodel)

            // Library
            api(project(":toolbox:core"))
            api(project(":toolbox:modules:ui"))
            api(project(":toolbox:modules:zip"))
            api(project(":toolbox:modules:backup"))

            if (buildFilePlugin.useLiveDependencies()) {

                // Lumberjack
                api(deps.lumberjack.core)
                api(deps.lumberjack.implementation.lumberjack)
                api(deps.lumberjack.logger.console)

                // KotPreferences
                api(deps.kotpreferences.core)
                api(deps.kotpreferences.extension.compose)

                // Compose Debug Drawer
                api(deps.composedebugdrawer.core)
                implementation(deps.composedebugdrawer.plugin.kotpreferences)

                // Compose Changelog
                api(deps.composechangelog.core)
                implementation(deps.composechangelog.renderer.header)
                implementation(deps.composechangelog.statesaver.kotpreferences)

                // Compose Dialogs
                api(deps.composedialogs.core)
                implementation(deps.composedialogs.dialog.info)
                implementation(deps.composedialogs.dialog.time)
                implementation(deps.composedialogs.dialog.frequency)

                // Compose Preferences
                api(deps.composepreferences.core)
                implementation(deps.composepreferences.screen.bool)
                implementation(deps.composepreferences.screen.list)
                implementation(deps.composepreferences.screen.button)
                implementation(deps.composepreferences.kotpreferences)

                // Compose Colors
                implementation(deps.composecolors.material)

                // Theming
                api(deps.composethemer.core)
                implementation(deps.composethemer.modules.picker)
                implementation(deps.composethemer.modules.defaultpicker)

            } else {

                // Lumberjack
                api(project(":lumberjack:core"))
                api(project(":lumberjack:implementations:lumberjack"))
                api(project(":lumberjack:loggers:lumberjack:console"))

                // KotPreferences
                api(project(":kotpreferences:core"))
                api(project(":kotpreferences:modules:compose"))

                // Compose Debug Drawer
                api(project(":composedebugdrawer:core"))
                implementation(project(":composedebugdrawer:plugins:kotpreferences"))

                // Compose Changelog
                api(project(":composechangelog:core"))
                implementation(project(":composechangelog:modules:renderer:header"))
                implementation(project(":composechangelog:modules:statesaver:kotpreferences"))

                // Compose Dialogs
                api(project(":composedialogs:core"))
                implementation(project(":composedialogs:modules:info"))
                implementation(project(":composedialogs:modules:frequency"))

                // Compose Preferences
                api(project(":composepreferences:core"))
                implementation(project(":composepreferences:modules:screen:bool"))
                implementation(project(":composepreferences:modules:screen:list"))
                implementation(project(":composepreferences:modules:screen:button"))
                implementation(project(":composepreferences:modules:kotpreferences"))

                // Compose Colors
                implementation(project(":composecolors:modules:material"))

                // Theming
                api(project(":composethemer:core"))
                implementation(project(":composethemer:modules:picker"))
                implementation(project(":composethemer:modules:defaultpicker"))
            }

            implementation(deps.filekit.dialogs.compose)

        }

        featureFileSupportMain.dependencies {

            if (buildFilePlugin.useLiveDependencies()) {

                // Lumberjack
                api(deps.lumberjack.logger.file)
                implementation(deps.lumberjack.extension.composeviewer)

                // KotPreferences
                api(deps.kotpreferences.storage.datastore)

                // Compose Debug Drawer
                implementation(deps.composedebugdrawer.plugin.lumberjack )

            } else {

                // Lumberjack
                api(project(":lumberjack:loggers:lumberjack:file"))
                implementation(":lumberjack:extensions:composeviewer")

                // KotPreferences
                api(project(":kotpreferences:modules:storage:datastore"))

                // Compose Debug Drawer
                implementation(project(":composedebugdrawer:plugins:lumberjack") )
            }

        }

        featureNoFileSupportMain.dependencies {

            if (buildFilePlugin.useLiveDependencies()) {

                // KotPreferences
                api(deps.kotpreferences.storage.keyvalue)

            } else {

                // KotPreferences
                api(project(":kotpreferences:modules:storage:keyvalue"))

            }
        }


        jvmMain.dependencies {

            // Jewel
            api(deps.jewel.int.ui.standalone)
            api(deps.jewel.int.ui.decorated.window)
            api(deps.jewel.foundation)
            api(deps.jewel.ui)
            api(deps.jewel.decorated.window)
            api(deps.intellij.platform.icons)
            api(deps.skiko)

            api(deps.htmlconverter)

        }

        androidMain.dependencies {

            if (buildFilePlugin.useLiveDependencies()) {

                // Compose Debug Drawer
                implementation( deps.composedebugdrawer.infos.build)
                implementation(deps.composedebugdrawer.infos.device )

            } else {

                // Compose Debug Drawer
                implementation( project(":composedebugdrawer:modules:buildinfos"))
                implementation(project(":composedebugdrawer:modules:deviceinfos" ))

            }

        }

        wasmJsMain.dependencies {
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




