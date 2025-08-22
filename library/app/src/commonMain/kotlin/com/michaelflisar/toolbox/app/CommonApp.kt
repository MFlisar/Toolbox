package com.michaelflisar.toolbox.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.renderer.header.ChangelogHeaderRenderer
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.app.features.logging.LogManager

object CommonApp {

    lateinit var setup: AppSetup
        private set

    internal fun init(
        setup: AppSetup
    ) {
        this.setup = setup

        // logging
        LogManager.init()

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

}