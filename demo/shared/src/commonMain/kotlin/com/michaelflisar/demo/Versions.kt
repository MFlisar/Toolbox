package com.michaelflisar.demo

import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.toolbox.app.features.versions.Version

private val V1 = Version(
    versionCode = 1,
    versionName = "0.0.1",
    releaseDate = "2026-11-01",
    changelog = {
        added {
            item("Introduced the first version.")
        }
    },
    update = null
)

private val V2 = Version(
    versionCode = 2,
    versionName = "0.0.2",
    releaseDate = "2026-11-07",
    changelog = {
        fixed {
            item("Corrected a layout issue.")
        }
        deprecated {
            item("Marked an older setting as deprecated.")
        }
        removed {
            item("Removed an obsolete option.")
        }
    },
    update = null
)

private val V3 = Version(
    versionCode = 3,
    versionName = "0.0.3",
    releaseDate = "2026-11-14",
    changelog = {
        added {
            item("Added the <b>changelog screen</b> with <i>italic</i> and <u>underlined</u> text.")
        }
        changed {
            item("Release <primary>categories</primary> and <color=primary>entries</color> follow their declaration order.")
        }
        security {
            item("Security updates get their own category.")
        }
        custom {
            StyledText("Custom content can use any Styled component.")
        }
    },
    update = null
)

val Versions = listOf(
    V1,
    V2,
    V3
)