import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
}

// -------------------
// Informations
// -------------------

// Module
val artifactId = "ui"
val androidNamespace = "com.michaelflisar.toolbox.ui"

// Library
val libraryName = "Toolbox"
val libraryDescription = "Toolbox - $artifactId module"
val groupID = "io.github.mflisar.toolbox"
val release = 2021
val github = "https://github.com/MFlisar/Toolbox"
val license = "Apache License 2.0"
val licenseUrl = "$github/blob/main/LICENSE"

// -------------------
// Setup
// -------------------

kotlin {

    //-------------
    // Mobile
    //-------------

    // Android
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iOS
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    //-------------
    // Desktop
    //-------------

    // Windows
    jvm()

    // macOS
    macosX64()
    macosArm64()

    // Linux
    // linuxX64()
    // linuxArm64()

    //-------------
    // Web
    //-------------

    // WASM
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }

    //-------------
    // JavaScript
    //-------------

    // js()
    // js(IR)

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom shared sources
        // ---------------------

        // all targets but windows
        val notJvmMain by creating {
            dependsOn(commonMain.get())
        }

        // ---------------------
        // target sources
        // ---------------------

        val groupedTargets = mapOf(
            "android" to listOf("android"),
            "ios" to listOf("iosX64", "iosArm64", "iosSimulatorArm64"),
            "jvm" to listOf("jvm"),
            "macos" to listOf("macosX64", "macosArm64"),
            "wasmJs" to listOf("wasmJs")
        )

        groupedTargets.forEach { group, targets ->
            val groupMain = sourceSets.maybeCreate("${group}Main")
            when (group) {
                "android", "ios", "macos", "wasmJs" -> {
                    groupMain.dependsOn(notJvmMain)
                }
                "jvm" -> {

                }
            }

            targets.forEach { target ->
                sourceSets.getByName("${target}Main").dependsOn(groupMain)
            }
        }

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // Compose + AndroidX
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)

            // Library
            api(project(":toolbox:core"))

        }

        androidMain.dependencies {

            implementation(androidx.core)

        }
    }
}

android {

    namespace = androidNamespace

    compileSdk = app.versions.compileSdk.get().toInt()

    buildFeatures {
        compose = true
    }

    defaultConfig {
        minSdk = app.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

mavenPublishing {

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true
        )
    )

    coordinates(
        groupId = groupID,
        artifactId = artifactId,
        version = System.getenv("TAG")
    )

    pom {
        name.set(libraryName)
        description.set(libraryDescription)
        inceptionYear.set("$release")
        url.set(github)

        licenses {
            license {
                name.set(license)
                url.set(licenseUrl)
            }
        }

        developers {
            developer {
                id.set("mflisar")
                name.set("Michael Flisar")
                email.set("mflisar.development@gmail.com")
            }
        }

        scm {
            url.set(github)
        }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral(true)

    // Enable GPG signing for all publications
    signAllPublications()
}