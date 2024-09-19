<h1 align="center">Demo Utilities</h1>

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