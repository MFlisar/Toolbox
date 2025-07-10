// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.gradle.maven.publish.plugin) apply false
    alias(deps.plugins.composechangelog) apply false
    alias(deps.plugins.kmp.gradle.tools.gradle.plugin) apply false
}

// ------------------------
// Scripts (kmp-template)
// ------------------------

buildscript {
    dependencies {
        classpath(deps.kmp.gradle.tools.scripts)
    }
}

tasks.register("createNewModule") {
    val dir = rootDir
    doLast {
        com.michaelflisar.kmpgradletools.scripts.createNewModule(
            rootDir = dir,
            folder = "library/modules",
            baseFeatureName = "ui",
            newFeatureName = "app"
        )
    }
}