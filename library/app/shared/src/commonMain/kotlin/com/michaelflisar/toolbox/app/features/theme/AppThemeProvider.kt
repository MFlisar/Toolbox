package com.michaelflisar.toolbox.app.features.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.toolbox.MyTheme
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.AppTheme
import com.michaelflisar.toolbox.app.LocalAppTheme
import com.michaelflisar.toolbox.app.LocalComposeTheme
import com.michaelflisar.toolbox.app.features.device.Current
import com.michaelflisar.toolbox.app.features.device.Device

interface IThemeSetup {

    val baseThemes: List<Any>

    @Composable
    fun collectBaseTheme(): MutableState<Any>

    fun getText(item: Any): String
    fun getIcon(item: Any): ImageVector

    @Composable
    fun rememberComposeThemeDefault(): ComposeTheme.State

    @Composable
    fun isDarkTheme(): Boolean
}

object DefaultThemeSetup : IThemeSetup {

    override val baseThemes: List<Any>
        get() = ComposeTheme.BaseTheme.entries

    @Composable
    override fun collectBaseTheme(): MutableState<Any> {
        return AppSetup.get().prefs.theme.asMutableStateNotNull(
            mapper = { it as Any },
            unmapper = { it as ComposeTheme.BaseTheme }
        )
    }

    override fun getText(item: Any): String {
        return (item as ComposeTheme.BaseTheme).name
    }

    override fun getIcon(item: Any): ImageVector {
        return when (item as ComposeTheme.BaseTheme) {
            ComposeTheme.BaseTheme.Light -> Icons.Default.LightMode
            ComposeTheme.BaseTheme.Dark -> Icons.Default.DarkMode
            ComposeTheme.BaseTheme.System -> Device.Current.icon
        }
    }

    @Composable
    override fun rememberComposeThemeDefault(): ComposeTheme.State {
        val setup = AppSetup.get()
        val theme = setup.prefs.theme.asMutableStateNotNull()
        val contrast = setup.prefs.contrast.asMutableStateNotNull()
        val dynamic = setup.prefs.dynamicTheme.asMutableStateNotNull()
        val customTheme = setup.prefs.customTheme.asMutableStateNotNull()
        return ComposeTheme.State(theme, contrast, dynamic, customTheme)
    }

    @Composable
    override fun isDarkTheme(): Boolean {
        val setup = AppSetup.get()
        val theme = setup.prefs.theme.asMutableStateNotNull()
        return theme.value.isDark()
    }
}

object ThemeSetup {
    private var instance: IThemeSetup = DefaultThemeSetup

    fun set(themeSetup: IThemeSetup) {
        instance = themeSetup
    }

    fun get(): IThemeSetup =
        instance
}

@Composable
fun AppThemeProvider(
    theme: MyTheme,
    composeTheme: ComposeTheme.State,
    appThemeProvider: @Composable () -> AppTheme,
    content: @Composable () -> Unit,
) {
    ComposeTheme(
        state = composeTheme,
        shapes = theme.shapes,
        typography = theme.typography
    ) {
        AppTheme(
            composeTheme = composeTheme,
            appThemeProvider = appThemeProvider
        ) {
            MyTheme(
                theme = theme
            ) {
                content()
            }
        }
    }
}