package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.ChangelogSetup
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.isDynamicColorsSupported
import com.michaelflisar.composestyled.themes.StyledThemeColorsCollection
import com.michaelflisar.composestyled.themes.nature.NatureThemes
import com.michaelflisar.composestyled.themes.pastel.PastelThemes
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.app.features.logging.ConsoleLoggingSetup
import com.michaelflisar.toolbox.app.features.preferences.BaseDebugPrefs
import com.michaelflisar.toolbox.app.features.preferences.BasePrefs
import com.michaelflisar.toolbox.app.features.theme.themeCollection

class AppSetup(
    val developer: Developer = Developer.Author(
        Constants.DEVELOPER_NAME,
        Constants.DEVELOPER_EMAIL
    ),
    val appData: AppData,
    val icon: @Composable () -> Painter,
    val themeSupport: ThemeSupport,
    val prefs: BasePrefs,
    val debug: Debug,
    val disableLanguagePicker: Boolean,
    val privacyPolicyLink: String,
    val logging: Logging,
    val changelogSetup: ChangelogSetup?,
    val isDebugBuild: Boolean,
) {
    companion object {
        private var instance: AppSetup? = null

        fun get(): AppSetup =
            instance ?: error("App.init(setup) must be called before accessing AppSetup.")

        internal fun initialize(setup: AppSetup): Boolean {
            if (instance != null)
                return false

            instance = setup
            return true
        }
    }

    @Stable
    class Debug(
        val prefs: BaseDebugPrefs,
        val sideSheetTriggerWidth: Dp = 48.dp,
        val sideSheet: @Composable (expanded: MutableState<Boolean>) -> Unit,
        val appTesting: AppTesting? = null,
    )

    @Immutable
    class AppTesting(
        val joinLink: String,
        val downloadLink: String
    )

    @Stable
    class Logging(
        val file: IFileLoggingSetup?,
        val console: ConsoleLoggingSetup?,
    )

    @Stable
    class ThemeSupport(
        val colorsCollections: List<StyledThemeColorsCollection> = colorsCollections(),
        val supportThemeSelector: Boolean = true,
        val supportDarkLight: Boolean = true,
        val supportContrast: Boolean = true,
        val supportDynamicColors: Boolean = true,
        val supportToolbarStyles: Boolean = true,
    ) {
        companion object {

            val Disabled = ThemeSupport(
                colorsCollections = colorsCollections(),
                supportThemeSelector = false,
                supportDarkLight = false,
                supportContrast = false,
                supportDynamicColors = false,
                supportToolbarStyles = false
            )

            fun colorsCollections() = listOf(
                themeCollection(),
                PastelThemes.collection,
                NatureThemes.collection
            )

        }

        fun supportsCustomThemes() =
            get().themeSupport.colorsCollections.map { it.themes.size }.sum() > 1

        fun supportsTheming() =
            supportsCustomThemes() || supportDarkLight || supportContrast || (supportDynamicColors && StyledTheme.isDynamicColorsSupported)
    }

    @Immutable
    class AppData(
        val versionCode: Int,
        val versionName: String,
        val namespace: String,
        val name: String,
    )

    @Stable
    sealed class Developer {

        companion object {
            val MFLISAR = Author(Constants.DEVELOPER_NAME, Constants.DEVELOPER_EMAIL)
        }

        abstract val name: String
        abstract val email: String

        @Immutable
        class Author(override val name: String, override val email: String) : Developer()

        @Immutable
        class Company(override val name: String, override val email: String) : Developer()
    }
}