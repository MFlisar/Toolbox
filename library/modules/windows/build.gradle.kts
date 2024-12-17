import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
}

// -------------------
// Informations
// -------------------

// Module
val artifactId = "windows"

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

compose.resources {
    packageOfResClass = "com.michaelflisar.toolbox.windows.resources"
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {

        commonMain.dependencies {
            implementation(compose.components.resources)
        }

        jvmMain.dependencies {
            api(libs.compose.material3)

            val useLiveDependencies =
                providers.gradleProperty("useLiveDependencies").get().toBoolean()

            // Jewel
            implementation(deps.jewel.int.ui.standalone)
            api(deps.jewel.int.ui.decorated.window)
            implementation(deps.intellij.platform.icons)
            //implementation("com.jetbrains.intellij.platform:icons:243.21565.208")

            // Icons
            api(libs.compose.material.icons.core)
            api(libs.compose.material.icons.extended)

            if (useLiveDependencies) {

                // Lumberjack
                api(deps.lumberjack.core)
                implementation(deps.lumberjack.implementation.lumberjack)
                implementation(deps.lumberjack.logger.console)
                implementation(deps.lumberjack.logger.file)
                api(deps.lumberjack.composeviewer)

                // KotPreferences
                api(deps.kotpreferences.core)
                implementation(deps.kotpreferences.datastore)
                api(deps.kotpreferences.compose)

                // ComposeColors
                implementation(deps.composecolors.material)

            } else {

                // Lumberjack
                api(project(":Lumberjack:Core"))
                implementation(project(":Lumberjack:Implementations:Lumberjack"))
                implementation(project(":Lumberjack:Loggers:Lumberjack:Console"))
                implementation(project(":Lumberjack:Loggers:Lumberjack:File"))
                api(project(":Lumberjack:Extensions:Composeviewer"))

                // KotPreferences
                api(project(":KotPreferences:Core"))
                implementation(project(":KotPreferences:Modules:Storage:Datastore"))
                api(project(":KotPreferences:Modules:Compose"))

                // ComposeColors
                implementation(project(":ComposeColors:Material"))

            }


            // Library
            api(project(":Toolbox:Core"))
            api(project(":Toolbox:Modules:Ui"))
            api(project(":Toolbox:Modules:Table"))
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
