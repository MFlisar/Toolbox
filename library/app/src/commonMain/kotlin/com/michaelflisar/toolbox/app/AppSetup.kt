package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.DefaultThemes
import com.michaelflisar.toolbox.app.classes.FileLogger
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.backup.IBackupSupport
import com.michaelflisar.toolbox.app.features.proversion.BaseAppProVersionManager
import com.michaelflisar.toolbox.app.platform.AppPrefs

class AppSetup(
    val versionCode: Int,
    val versionName: String,
    val packageName: String,
    val name: @Composable () -> String,
    val icon: @Composable () -> Painter,
    val themeSupport: ThemeSupport,
    val prefs: AppPrefs,
    val debugPrefs: DebugPrefs,
    val proVersionManager: BaseAppProVersionManager,
    val supportDebugDrawer: Boolean,
    val supportsChangelog: Boolean,
    val privacyPolicyLink: String,
    val supportLanguagePicker: Boolean,
    val fileLogger: FileLogger<*>?,
    val changelogSetup: Changelog.Setup?,
    val backupSupport: IBackupSupport?,
    val isDebugBuild: Boolean,
) {

    class ThemeSupport(
        val themes: List<ComposeTheme.Theme> = DefaultThemes.getAllThemes(),
        val supportDarkLight: Boolean,
        val supportContrast: Boolean,
        val supportDynamicColors: Boolean,
        val supportToolbarStyles: Boolean,
    ) {
        companion object {
            fun disabled(
                themes: List<ComposeTheme.Theme> = DefaultThemes.getAllThemes(),
            ) = ThemeSupport(themes, false, false, false, false)

            fun full(
                themes: List<ComposeTheme.Theme> = DefaultThemes.getAllThemes(),
            ) = ThemeSupport(themes, true, true, true, true)
        }

        fun supportsCustomThemes() = ComposeTheme.getRegisteredThemes().size > 1
        fun supportsTheming() =
            ComposeTheme.getRegisteredThemes().size > 1 || supportDarkLight || supportContrast || supportDynamicColors
    }
}