import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.configs.*
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.setupDependencies
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SourcesJar

plugins {
    // kmp + app/library
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library.kmp)
    // org.jetbrains.kotlin
    alias(libs.plugins.jetbrains.kotlin.compose)
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
    // mobile
    android = true,
    iOS = true,
    // desktop
    windows = true,
    macOS = false, // because of compose unstyled dialogs
    // web
    wasm = true
)

val androidConfig = AndroidLibraryConfig.create(
    libraryModuleConfig = module,
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = false
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
    android {
        buildTargets.setupTargetsAndroidLibrary(module, androidConfig, this)
    }

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom shared sources
        // ---------------------

        // --

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // Compose + AndroidX
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.material.icons.core)
            implementation(libs.jetbrains.compose.material.icons.extended)

            // Library
            api(project(":toolbox:core"))
            api(project(":toolbox:modules:ui"))

            // TODO: dialogs for mac...
            api(mflisar.composedialogs.core)
            api(mflisar.composedialogs.dialog.info)

        }
    }
}

// -------------------
// Publish
// -------------------

// maven publish configuration
//if (BuildFileUtil.checkGradleProperty(project, "publishToMaven") != false)
//    BuildFileUtil.setupMavenPublish(module)

// local publish configuration => diese kann dann auf github per workflow hochgeladen werden
// task name: publishAllPublicationsToLocalMavenRepoRepository
setupLocalMavenPublish(
    libraryModuleConfig = module,
    version = System.getenv("VERSION") ?: "LOCAL-SNAPSHOT"
)

fun setupLocalMavenPublish(
    libraryModuleConfig: LibraryModuleConfig.Library,
    platform: com.vanniktech.maven.publish.Platform = KotlinMultiplatform(
        javadocJar = JavadocJar.Dokka("dokkaGenerateHtml"),
        sourcesJar = SourcesJar.Sources()
    ),
    version: String,
) {
    val module = libraryModuleConfig.libraryConfig.getModuleForProject(
        libraryModuleConfig.project.rootDir,
        libraryModuleConfig.project.projectDir
    )

    libraryModuleConfig.project.extensions.configure(MavenPublishBaseExtension::class.java) {
        configure(platform)

        coordinates(
            groupId = libraryModuleConfig.libraryConfig.maven.groupId,
            artifactId = module.artifactId,
            version = version
        )

        pom {
            name.set(libraryModuleConfig.libraryConfig.library.name)
            description.set(module.libraryDescription(libraryModuleConfig.libraryConfig))
            inceptionYear.set(libraryModuleConfig.libraryConfig.library.release.toString())
            url.set(
                libraryModuleConfig.libraryConfig.library.getRepoLink(
                    libraryModuleConfig.config.developer
                )
            )

            licenses {
                license {
                    name.set(libraryModuleConfig.libraryConfig.library.license.name)
                    url.set(
                        libraryModuleConfig.libraryConfig.library.license.getLink(
                            libraryModuleConfig.config.developer,
                            libraryModuleConfig.libraryConfig.library
                        )
                    )
                }
            }

            developers {
                developer {
                    id.set(libraryModuleConfig.config.developer.mavenId)
                    name.set(libraryModuleConfig.config.developer.name)
                    email.set(libraryModuleConfig.config.developer.mail)
                }
            }

            scm {
                url.set(
                    libraryModuleConfig.libraryConfig.library.getRepoLink(
                        libraryModuleConfig.config.developer
                    )
                )
            }
        }
    }

    libraryModuleConfig.project.extensions.configure(PublishingExtension::class.java) {
        repositories {
            maven {
                name = "LocalMavenRepo"

                url = libraryModuleConfig.project.layout.buildDirectory
                    .dir("maven-repo")
                    .get()
                    .asFile
                    .toURI()
            }
        }
    }
}