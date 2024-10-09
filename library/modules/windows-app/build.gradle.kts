import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
}

// -------------------
// Informations
// -------------------

// Module
val artifactId = "windows-app"

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
    jvm {
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {

                api(libs.compose.material3)

                // Aurora - Windows Only Theming and Components
                api(libs.aurora.theming)
                api(libs.aurora.component)
                api(libs.aurora.window)

                // Icons
                api(libs.compose.material.icons.core)
                api(libs.compose.material.icons.extended)

                // Dependencies
                api(libs.lumberjack.core)
                implementation(libs.lumberjack.implementation.lumberjack)
                implementation(libs.lumberjack.logger.console)
                implementation(libs.lumberjack.logger.file)
                api(libs.lumberjack.composeviewer)
                api(libs.kotpreferences.core)
                implementation(libs.kotpreferences.datastore)
                api(libs.kotpreferences.compose)
                api(libs.composethemer.core)
                implementation(libs.composethemer.themes)

                // Library
                api(project(":Toolbox:Core"))
                api(project(":Toolbox:Modules:Table"))

            }
        }
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
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, true)

    // Enable GPG signing for all publications
    signAllPublications()
}
