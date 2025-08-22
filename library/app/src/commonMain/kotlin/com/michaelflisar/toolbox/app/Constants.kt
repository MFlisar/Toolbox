package com.michaelflisar.toolbox.app

import com.michaelflisar.composechangelog.DefaultVersionFormatter

object Constants {

    val DEVELOPER_NAME = "Michael Flisar"
    val DEVELOPER_EMAIL = "mflisar.development@gmail.com"

    val CHANGELOG_PATH = "files/changelog.xml"
    val CHANGELOG_FORMATTER = DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate)
}