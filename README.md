[![Maven](https://img.shields.io/maven-central/v/io.github.mflisar.demoutilities/core?style=for-the-badge&color=blue)](https://central.sonatype.com/namespace/io.github.mflisar.demoutilities)
[![API](https://img.shields.io/badge/api-21%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/github/languages/top/mflisar/demoutilities.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/)
[![KMP](https://img.shields.io/badge/Kotlin_Multiplatform-blue?style=for-the-badge&label=Kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/github/license/MFlisar/DemoUtilities?style=for-the-badge)](LICENSE)

<h1 align="center">Demo Utilities</h1>

Offers following:

* common composables
* a demo app and activity

I use this library to create a demo activity in many of my open source libraries.

## :elephant: Gradle

This library is distributed via [maven central](https://central.sonatype.com/).

*build.gradle.kts*

```kts
val demoutilities = "<LATEST-VERSION>"

implementation("io.github.mflisar.demoutilities:core:$demoutilities")
implementation("io.github.mflisar.demoutilities:app:$demoutilities")
```