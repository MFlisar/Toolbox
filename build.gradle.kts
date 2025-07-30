// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.gradle.maven.publish.plugin) apply false
    alias(deps.plugins.composechangelog) apply false
    alias(deps.plugins.kmp.gradle.tools.gradle.plugin) apply false
    alias(libs.plugins.launch4j) apply false
}

// ------------------------
// Scripts (kmp-gradle-tools)
// ------------------------

buildscript {
    dependencies {
        classpath(deps.kmp.gradle.tools.scripts)
        classpath(deps.kmp.gradle.tools.docs)
    }
}

tasks.register("buildDocs") {
    doLast {
        // read env from build-mkdocs.yml
        val generatedDocsDir = project.findProperty("generatedDocsDir") as String? ?: "gen/docs"
        com.michaelflisar.kmpgradletools.docs.buildDocs(
            relativePathDocsCustom = "documentation/custom",
            relativePathGeneratedDocsOutput = generatedDocsDir,
            relativeModulesPath = "library",
            relativeDemosPath = "demo",
            customOtherProjectsYamlUrl = "https://raw.githubusercontent.com/MFlisar/kmp-template/refs/heads/main/data/other-projects.yml"
        )

        println("Docs have been build!")
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