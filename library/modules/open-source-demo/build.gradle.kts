import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    //alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
}

// -------------------
// Informations
// -------------------

val description = "a collection of UI elements for my open source demo projects"

// Module
val artifactId = "open-source-demo"

// Library
val libraryName = "kmp-gradle-tools"
val libraryDescription = "$libraryName - $artifactId module - $description"
val groupID = "io.github.mflisar.kmp-gradle-tools"
val release = 2025
val github = "https://github.com/MFlisar/kmp-gradle-tools"
val license = "Apache License 2.0"
val licenseUrl = "$github/blob/main/LICENSE"

// -------------------
// Setup
// -------------------

val androidNamespace = "com.michaelflisar.kmpgradletools.opensourcedemo"

kotlin {

    //-------------
    // Targets
    //-------------

    // Android
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget("17"))
        }
    }

    // iOS
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // Windows
    jvm()

    // macOS
    macosX64()
    macosArm64()

    // Linux
    //linuxX64()
    //linuxArm64()

    // WASM
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }

    // JavaScript
    //js {
    //    browser()
    //}
    //js(IR)

    // -------
    // Sources
    // -------

    sourceSets {

        commonMain.dependencies {

            // Kotlin
            implementation(kotlinx.coroutines.core)

            // AndroidX / Google
            implementation(libs.compose.runtime)
            api(libs.compose.material3)
            api(libs.compose.material.icons.core)
            api(libs.compose.material.icons.extended)

        }
    }
}

android {

    namespace = androidNamespace

    compileSdk = app.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = app.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// -------------------
// Configurations
// -------------------

mavenPublishing {

    configure(
        KotlinMultiplatform(
            //javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true
        )
    )

    coordinates(
        groupId = groupID,
        artifactId = artifactId,
        version = System.getenv("TAG") ?: "LOCAL"
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
    if (System.getenv("CI")?.toBoolean() == true)
        signAllPublications()
}