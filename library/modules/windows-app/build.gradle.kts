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
                api(deps.aurora.theming)
                api(deps.aurora.component)
                api(deps.aurora.window)

                // Icons
                api(libs.compose.material.icons.core)
                api(libs.compose.material.icons.extended)

                // Dependencies
                api(deps.lumberjack.core)
                implementation(deps.lumberjack.implementation.lumberjack)
                implementation(deps.lumberjack.logger.console)
                implementation(deps.lumberjack.logger.file)
                api(deps.lumberjack.composeviewer)
                api(deps.kotpreferences.core)
                implementation(deps.kotpreferences.datastore)
                api(deps.kotpreferences.compose)
                implementation(deps.composecolors.material)

                // Library
                api(project(":Toolbox:Core"))
                api(project(":Toolbox:Modules:UI"))
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
