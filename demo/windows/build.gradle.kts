import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
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

            implementation(compose.desktop.currentOs)

            implementation(deps.composecolors.material)

            implementation(project(":toolbox:modules:windows"))

        }
    }
}

compose.resources {
    packageOfResClass = "com.michaelflisar.toolbox.demo.resources"
}

compose.desktop {
    application {
        mainClass = "com.michaelflisar.toolbox.demo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "Toolbox JVM Demo"
            packageVersion = "1.0.0"
        }
    }
}