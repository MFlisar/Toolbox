package com.michaelflisar.toolbox.app.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.DesktopPrefs
import com.michaelflisar.toolbox.app.features.theme.IThemeSetup
import com.michaelflisar.toolbox.app.jewel.JewelBaseTheme
import com.michaelflisar.toolbox.app.jewel.JewelTheme

class AppBaseTheme(
    val text: String,
    val icon: ImageVector,
    val data: Any,
)

private fun JewelTheme.toAppBaseTheme(): AppBaseTheme {
    return AppBaseTheme(
        text = this.label,
        icon = this.imageVector,
        data = this
    )
}

private val AppBaseThemeLight = JewelTheme.Light.toAppBaseTheme()
private val AppBaseThemeLightWithLightHeader = JewelTheme.LightWithLightHeader.toAppBaseTheme()
private val AppBaseThemeDark = JewelTheme.Dark.toAppBaseTheme()
private val AppBaseThemeSystem = JewelTheme.System.toAppBaseTheme()

private val BaseThemes = listOf(
    AppBaseThemeLight,
    AppBaseThemeLightWithLightHeader,
    AppBaseThemeDark,
    AppBaseThemeSystem
)

object JRBThemeSetup : IThemeSetup {

    override val baseThemes: List<AppBaseTheme>
        get() = BaseThemes

    @Composable
    override fun collectBaseTheme(): MutableState<Any> {
        return DesktopPrefs.get().jewelTheme.asMutableStateNotNull(
            mapper = { jewelTheme -> BaseThemes.find { it.data == jewelTheme }!! },
            unmapper = { ((it as AppBaseTheme).data as JewelTheme) }
        )
    }

    override fun getText(item: Any): String {
        return (item as AppBaseTheme).text
    }

    override fun getIcon(item: Any): ImageVector {
        return (item as AppBaseTheme).icon
    }

    @Composable
    override fun rememberComposeThemeDefault(): ComposeTheme.State {
        val jewelTheme by DesktopPrefs.get().jewelTheme.collectAsStateNotNull()
        val theme = remember(jewelTheme) {
            mutableStateOf(jewelTheme.baseTheme().let {
                when (it) {
                    JewelBaseTheme.Light -> ComposeTheme.BaseTheme.Light
                    JewelBaseTheme.Dark -> ComposeTheme.BaseTheme.Dark
                    JewelBaseTheme.System -> ComposeTheme.BaseTheme.System
                }
            })
        }
        val setup = AppSetup.get()
        val contrast = setup.prefs.contrast.asMutableStateNotNull()
        val dynamic = setup.prefs.dynamicTheme.asMutableStateNotNull()
        val customTheme = setup.prefs.customTheme.asMutableStateNotNull()
        return ComposeTheme.State(theme, contrast, dynamic, customTheme)
    }

    @Composable
    override fun isDarkTheme(): Boolean {
        val theme = DesktopPrefs.get().jewelTheme.collectAsStateNotNull()
        return theme.value.isDark()
    }
}