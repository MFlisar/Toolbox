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
        val jvmMain by getting {
            dependencies {

                implementation(compose.desktop.currentOs)

                implementation(project(":PublicUtilities:Modules:WindowsApp"))

            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.michaelflisar.publicutilities.demo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "PublicUtilities JVM Demo"
            packageVersion = "1.0.0"
        }
    }
}