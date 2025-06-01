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

    jvm()

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
            implementation(deps.jewel.int.ui.decorated.window)
            implementation(deps.jewel.foundation)
            implementation(deps.jewel.ui)
            api(deps.jewel.decorated.window)
            implementation(deps.intellij.platform.icons)
            implementation(deps.skiko)

            // Icons
            api(libs.compose.material.icons.core)
            api(libs.compose.material.icons.extended)

            if (useLiveDependencies) {

                // Lumberjack
                api(deps.lumberjack.core)
                implementation(deps.lumberjack.implementation.lumberjack)
                implementation(deps.lumberjack.logger.console)
                implementation(deps.lumberjack.logger.file)
                api(deps.lumberjack.extension.composeviewer)

                // KotPreferences
                api(deps.kotpreferences.core)
                implementation(deps.kotpreferences.storage.datastore)
                api(deps.kotpreferences.extension.compose)

                // ComposePreferences
                implementation(deps.composepreferences.core)
                implementation(deps.composepreferences.kotpreferences)
                implementation(deps.composepreferences.screen.bool)
                implementation(deps.composepreferences.screen.input)
                implementation(deps.composepreferences.screen.list)

                // ComposeColors
                implementation(deps.composecolors.material)

                // ComposeThemer
                api(deps.composethemer.core)

            } else {

                // Lumberjack
                api(project(":lumberjack:core"))
                implementation(project(":lumberjack:implementations:lumberjack"))
                implementation(project(":lumberjack:loggers:lumberjack:console"))
                implementation(project(":lumberjack:loggers:lumberjack:file"))
                api(project(":lumberjack:extensions:composeviewer"))

                // KotPreferences
                api(project(":kotpreferences:core"))
                implementation(project(":kotpreferences:modules:storage:datastore"))
                api(project(":kotpreferences:modules:compose"))

                // ComposePreferences
                implementation(project(":composepreferences:core"))
                implementation(project(":composepreferences:modules:kotpreferences"))
                implementation(project(":composepreferences:modules:screen:bool"))
                implementation(project(":composepreferences:modules:screen:input"))
                implementation(project(":composepreferences:modules:screen:list"))

                // ComposeColors
                implementation(project(":composecolors:material"))

                // ComposeThemer
                api(project(":composethemer:core"))

            }


            // Library
            api(project(":toolbox:core"))
            api(project(":toolbox:modules:ui"))
            api(project(":toolbox:modules:table"))
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
