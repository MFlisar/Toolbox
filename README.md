[![Maven Central](https://img.shields.io/maven-central/v/io.github.mflisar.toolbox/core?color=blue)](https://central.sonatype.com/artifact/io.github.mflisar.toolbox/core) ![API](https://img.shields.io/badge/api-24%2B-brightgreen.svg) ![Kotlin](https://img.shields.io/github/languages/top/MFlisar/Toolbox.svg?color=blueviolet) ![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin_Multiplatform-blue?label=Kotlin)
# Toolbox
![Platforms](https://img.shields.io/badge/PLATFORMS-black?style=for-the-badge)

> [!NOTE]
> This library currently is for my personal usage only.
> I do use it in production, but have not yet documented it properly.

> [!TIP]
> Try the interactive demo:
> 
> [![Open Interactive Demo](https://img.shields.io/badge/Toolbox_Demo-2962FF?style=for-the-badge&logo=googlechrome&logoColor=white)](https://mflisar.github.io/Toolbox/demo/)

# Table of Contents

- [Supported Platforms](#computer-supported-platforms)
- [Versions](#arrow_right-versions)
- [Setup](#wrench-setup)
- [Usage](#rocket-usage)
- [Demo](#sparkles-demo)
- [API](#books-api)
- [Other Libraries](#bulb-other-libraries)

# :computer: Supported Platforms

| Module | Notes |
|---|---|
| core | the core module |
| app | the styled app module |
| zip | a zip module |
| csv | a csv module |
| backup | a backup module |
| service | a service module |
| form | a form module |
| powershell | a powershell module |
| room | provides room database classes |
| ads | provides ads composables |
| proversion | provides proversion checker functions (via openiap) |
| diff | diff functions and UI |
| excel | excel functions |
| coil | a coil module |
| tasks | a tasks module including a task viewer |

# :arrow_right: Versions

| Dependency | Version |
|---|---|
| Kotlin | `2.4.21` |
| Jetbrains Compose | `1.12.2` |
| Jetbrains Compose Material3 | `1.9.0` |

> :warning: Following experimental annotations are used:
> - **OptIn**
>   - `androidx.compose.ui.ExperimentalComposeUiApi` (3x)
>   - `app.lexilabs.basic.ads.DependsOnGoogleMobileAds` (3x)
>   - `app.lexilabs.basic.ads.DependsOnGoogleUserMessagingPlatform` (6x)
>   - `ExperimentalWasmJsInterop` (1x)
>   - `kotlin.js.ExperimentalWasmJsInterop` (1x)
>   - `kotlinx.cinterop.ExperimentalForeignApi` (2x)
>   - `kotlinx.coroutines.FlowPreview` (1x)
>
> I try to use as less experimental features as possible, but in this case the ones above are needed!

# :wrench: Setup

<details open>

<summary><b>Using Version Catalogs</b></summary>

<br>

Define the dependencies inside your **libs.versions.toml** file.

```toml
[versions]

toolbox = "<LATEST-VERSION>"

[libraries]

toolbox-core = { module = "io.github.mflisar.toolbox:core", version.ref = "toolbox" }
toolbox-app = { module = "io.github.mflisar.toolbox:app", version.ref = "toolbox" }
toolbox-zip = { module = "io.github.mflisar.toolbox:zip", version.ref = "toolbox" }
toolbox-csv = { module = "io.github.mflisar.toolbox:csv", version.ref = "toolbox" }
toolbox-backup = { module = "io.github.mflisar.toolbox:backup", version.ref = "toolbox" }
toolbox-service = { module = "io.github.mflisar.toolbox:service", version.ref = "toolbox" }
toolbox-form = { module = "io.github.mflisar.toolbox:form", version.ref = "toolbox" }
toolbox-powershell = { module = "io.github.mflisar.toolbox:powershell", version.ref = "toolbox" }
toolbox-room = { module = "io.github.mflisar.toolbox:room", version.ref = "toolbox" }
toolbox-ads = { module = "io.github.mflisar.toolbox:ads", version.ref = "toolbox" }
toolbox-proversion = { module = "io.github.mflisar.toolbox:proversion", version.ref = "toolbox" }
toolbox-diff = { module = "io.github.mflisar.toolbox:diff", version.ref = "toolbox" }
toolbox-excel = { module = "io.github.mflisar.toolbox:excel", version.ref = "toolbox" }
toolbox-coil = { module = "io.github.mflisar.toolbox:coil", version.ref = "toolbox" }
toolbox-tasks = { module = "io.github.mflisar.toolbox:tasks", version.ref = "toolbox" }
```

And then use the definitions in your projects **build.gradle.kts** file like following:

```java
implementation(libs.toolbox.core)
implementation(libs.toolbox.app)
implementation(libs.toolbox.zip)
implementation(libs.toolbox.csv)
implementation(libs.toolbox.backup)
implementation(libs.toolbox.service)
implementation(libs.toolbox.form)
implementation(libs.toolbox.powershell)
implementation(libs.toolbox.room)
implementation(libs.toolbox.ads)
implementation(libs.toolbox.proversion)
implementation(libs.toolbox.diff)
implementation(libs.toolbox.excel)
implementation(libs.toolbox.coil)
implementation(libs.toolbox.tasks)
```

</details>

<details>

<summary><b>Direct Dependency Notation</b></summary>

<br>

Simply add the dependencies inside your **build.gradle.kts** file.

```kotlin
val toolbox = "<LATEST-VERSION>"

implementation("io.github.mflisar.toolbox:core:${toolbox}")
implementation("io.github.mflisar.toolbox:app:${toolbox}")
implementation("io.github.mflisar.toolbox:zip:${toolbox}")
implementation("io.github.mflisar.toolbox:csv:${toolbox}")
implementation("io.github.mflisar.toolbox:backup:${toolbox}")
implementation("io.github.mflisar.toolbox:service:${toolbox}")
implementation("io.github.mflisar.toolbox:form:${toolbox}")
implementation("io.github.mflisar.toolbox:powershell:${toolbox}")
implementation("io.github.mflisar.toolbox:room:${toolbox}")
implementation("io.github.mflisar.toolbox:ads:${toolbox}")
implementation("io.github.mflisar.toolbox:proversion:${toolbox}")
implementation("io.github.mflisar.toolbox:diff:${toolbox}")
implementation("io.github.mflisar.toolbox:excel:${toolbox}")
implementation("io.github.mflisar.toolbox:coil:${toolbox}")
implementation("io.github.mflisar.toolbox:tasks:${toolbox}")
```

</details>

# :rocket: Usage

> [!IMPORTANT]
> NOT DOCUMENTED

# :sparkles: Demo

A [demo project](demo) is included.

# :books: API

Check out the [API documentation](https://MFlisar.github.io/Toolbox/api/).

# :bulb: Other Libraries

You can find more of my multiplatform libraries that work well together [here](https://mflisar.github.io/Libraries/).

# :twisted_rightwards_arrows: Compatibility

When combining my libraries, you can find compatibility information [here](https://mflisar.github.io/Libraries/compatibilities/).
