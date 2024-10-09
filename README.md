[![Maven](https://img.shields.io/maven-central/v/io.github.mflisar.toolbox/core?style=for-the-badge&color=blue)](https://central.sonatype.com/namespace/io.github.mflisar.toolbox)
[![API](https://img.shields.io/badge/api-21%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/github/languages/top/mflisar/toolbox.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/)
[![KMP](https://img.shields.io/badge/Kotlin_Multiplatform-blue?style=for-the-badge&label=Kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/github/license/MFlisar/Toolbox?style=for-the-badge)](LICENSE)

<h1 align="center">Demo Utilities</h1>

## :heavy_check_mark: Features

* **core**: common composables for desktop and android apps with automatic handling of scrollbars and more
* **table**: a table composable
* **android-demo-app**: a demo activity and application and some demo specific composables
* **windows-app**: a aurora styled windows app and some composables and base settings and ui

> [!NOTE]  
> I use this library to create a demo android activities in many of my open source libraries as well as for all my desktop apps.

## :elephant: Gradle

This library is distributed via [maven central](https://central.sonatype.com/).

*build.gradle.kts*

```kts
val toolbox = "<LATEST-VERSION>"

implementation("io.github.mflisar.toolbox:core:$toolbox")
implementation("io.github.mflisar.toolbox:table:$toolbox")

implementation("io.github.mflisar.toolbox:android-demo-app:$toolbox")
implementation("io.github.mflisar.toolbox:windows-app:$toolbox")
```