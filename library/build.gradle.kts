plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("maven-publish")
}

android {

    namespace = "com.michaelflisar.composedemobaseactivity"

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

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = compose.versions.compiler.get()
    }
}

dependencies {

    // ------------------------
    // Kotlin
    // ------------------------

    implementation(libs.kotlin)

    // ------------------------
    // AndroidX
    // ------------------------

    // Compose
    implementation(platform(compose.bom))
    implementation(compose.material3)
    implementation(compose.activity)
    implementation(compose.material.extendedicons)
    implementation(compose.drawablepainter)

    // ------------------------
    // Libraries
    // ------------------------

    // KotPreferences
    implementation(deps.kotpreferences.core)
    implementation(deps.kotpreferences.datastore)
    implementation(deps.kotpreferences.compose)

    // Dialogs
    implementation(deps.composedialogs.core)
    implementation(deps.composedialogs.dialog.list)
}

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])
            }
        }
    }
}