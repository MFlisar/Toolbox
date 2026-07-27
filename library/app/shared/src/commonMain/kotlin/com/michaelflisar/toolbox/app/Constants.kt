package com.michaelflisar.toolbox.app

import com.michaelflisar.composechangelog.format.DefaultVersionFormatter

object Constants {

    const val DEVELOPER_NAME = "Michael Flisar"
    const val DEVELOPER_EMAIL = "mflisar.development@gmail.com"

    const val CHANGELOG_PATH = "files/changelog.xml"

    // wird auch in kmp-devtools standardmäßig benutzt, muss daher nirgendwo manuell übergeben werden
    // dieses Format ist auch windows kompatibel!
    val CHANGELOG_FORMATTER = DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatch)
}