package com.michaelflisar.toolbox.app

import com.michaelflisar.composechangelog.DefaultVersionFormatter

object Constants {

    const val DEVELOPER_NAME = "Michael Flisar"
    const val DEVELOPER_EMAIL = "mflisar.development@gmail.com"

    const val CHANGELOG_PATH = "files/changelog.xml"
    val CHANGELOG_FORMATTER = DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate)
}