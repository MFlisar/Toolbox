package com.michaelflisar.toolbox.app.features.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.DesktopApp
import com.michaelflisar.toolbox.app.jewel.JewelBaseTheme
import com.michaelflisar.toolbox.app.jewel.JewelTheme
import com.michaelflisar.toolbox.utils.JvmFolderUtil
import com.michaelflisar.toolbox.utils.JvmUtil

private fun JewelTheme.toAppBaseTheme(): Preferences.AppBaseTheme {
    return Preferences.AppBaseTheme(
        text = this.label,
        icon = this.imageVector,
        data = this
    )
}

private val AppBaseThemeLight = JewelTheme.Light.toAppBaseTheme()
private val AppBaseThemeLightWithLightHeader = JewelTheme.LightWithLightHeader.toAppBaseTheme()
private val AppBaseThemeDark = JewelTheme.Dark.toAppBaseTheme()
private val AppBaseThemeSystem = JewelTheme.System.toAppBaseTheme()

actual fun Preferences.createStorage(name: String): Storage {
    return DataStoreStorage.create(folder = JvmFolderUtil.getApplicationPath(), name = name)
}

actual val Preferences.BaseThemes: List<Preferences.AppBaseTheme>
    get() = listOf(
        AppBaseThemeLight,
        AppBaseThemeLightWithLightHeader,
        AppBaseThemeDark,
        AppBaseThemeSystem
    )

@Composable
actual fun Preferences.collectBaseTheme(): MutableState<Preferences.AppBaseTheme> {
    return DesktopApp.setup.prefs.jewelTheme.asMutableStateNotNull(
        mapper = { jewelTheme -> BaseThemes.find { it.data == jewelTheme }!! },
        unmapper = { (it.data as JewelTheme) }
    )
}

@Composable
actual fun Preferences.rememberComposeTheme(): ComposeTheme.State {
    val jewelTheme by DesktopApp.setup.prefs.jewelTheme.collectAsStateNotNull()
    val theme = remember(jewelTheme) {
        mutableStateOf(jewelTheme.baseTheme().let {
            when (it) {
                JewelBaseTheme.Light -> ComposeTheme.BaseTheme.Light
                JewelBaseTheme.Dark -> ComposeTheme.BaseTheme.Dark
                JewelBaseTheme.System -> ComposeTheme.BaseTheme.System
            }
        })
    }
    val setup = CommonApp.setup
    val contrast = setup.prefs.contrast.asMutableStateNotNull()
    val dynamic = setup.prefs.dynamicTheme.asMutableStateNotNull()
    val customTheme = setup.prefs.customTheme.asMutableStateNotNull()
    return ComposeTheme.State(theme, contrast, dynamic, customTheme)
}