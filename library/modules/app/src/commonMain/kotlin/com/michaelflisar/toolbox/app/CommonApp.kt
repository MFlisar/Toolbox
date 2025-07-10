package com.michaelflisar.toolbox.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.DefaultVersionFormatter
import com.michaelflisar.composechangelog.renderer.header.ChangelogHeaderRenderer
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.loggers.console.ConsoleLogger
import com.michaelflisar.lumberjack.loggers.file.FileLogger

object CommonApp {

    lateinit var setup: AppSetup
        private set

    internal fun init(
        setup: AppSetup
    ) {
        this.setup = setup

        // logging
        L.init(LumberjackLogger)
        L.plant(ConsoleLogger())
        setup.fileLoggerSetup?.let { L.plant(FileLogger(it)) }

        // themes
        ComposeTheme.register(*setup.themeSupport.themes.toTypedArray())

        // changelog
        Changelog.registerRenderer(
            ChangelogHeaderRenderer {
                val icon = when (it?.lowercase()) {
                    "info" -> Icons.Default.Info
                    "new" -> Icons.Default.NewReleases
                    "warning" -> Icons.Default.Warning
                    else -> null
                }
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        )
    }

    val changelogPath = "files/changelog.xml"
    val changelogFormatter =
        DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate)
}