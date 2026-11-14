package com.michaelflisar.toolbox.app.features.changelog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composechangelog.ChangelogRelease
import com.michaelflisar.composechangelog.ChangelogSetup
import com.michaelflisar.composechangelog.changelogReleases
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.toolbox.app.features.versions.Version

object ChangelogUtil {

    fun createSetup(
        versions: List<Version>
    ) = createSetup(
        releases = changelogReleases {
            versions.forEach { it.changelog(this) }
        }
    )

    fun createSetup(
        releases: List<ChangelogRelease>,
        textFormatter: @Composable (String) -> AnnotatedString = ChangelogDefaults.textFormatter(
            color = mapOf(
                "primary" to @Composable { StyledTheme.colorScheme.primary }
            )
        ),
    ): ChangelogSetup = ChangelogDefaults.setup(
        textFormatter = textFormatter,
        releases = releases
    )
}