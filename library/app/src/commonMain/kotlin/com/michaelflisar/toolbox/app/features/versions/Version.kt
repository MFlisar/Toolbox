package com.michaelflisar.toolbox.app.features.versions

import com.michaelflisar.composechangelog.ChangelogReleaseBuilder
import com.michaelflisar.composechangelog.ChangelogReleasesBuilder
import com.michaelflisar.toolbox.app.features.update.BaseUpdate

class Version(
    val versionCode: Int,
    val versionName: String,
    val changelog: ChangelogReleaseBuilder.() -> Unit,
    val releaseDate: String? = null,
    val update: BaseUpdate? = null,
) {
    fun changelog(builder: ChangelogReleasesBuilder) = with(builder) {
        release(name = versionName, code = versionCode, date = releaseDate) {
            changelog(this)
        }
    }
}