package com.michaelflisar.toolbox.app.pages

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.michaelflisar.composepreferences.core.classes.PreferenceState
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.features.backhandlerregistry.NavigatorBackHandler
import com.michaelflisar.toolbox.app.features.backhandlerregistry.RegisterBackHandler
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.app.features.preferences.AppPreferences
import com.michaelflisar.toolbox.app.features.preferences.AppPreferencesStyle
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.menu_settings
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.logIf
import org.jetbrains.compose.resources.stringResource
import kotlin.jvm.Transient

abstract class PageSettings : NavScreen() {

    @Composable
    abstract fun provideStyle(): AppPreferencesStyle

    @Transient
    @IgnoredOnParcel
    private var preferenceState: MutableState<PreferenceState?> = mutableStateOf(null)

    @Composable
    override fun provideData() = rememberNavScreenData(
        name = stringResource(Res.string.menu_settings),
        icon = Icons.Default.Settings.toIconComposable()
    )

    @Composable
    override fun Screen() {
        Page(provideStyle(), preferenceState)
    }

    @Composable
    override fun Toolbar() {
        val data = provideData()
        val canGoBack = remember {
            derivedStateOf { (preferenceState.value?.currentLevel ?: 0) > 0 }
        }
        com.michaelflisar.toolbox.app.features.toolbar.Toolbar(
            title = data.name,
            canGoBack = canGoBack.value,
            onBack = { preferenceState.value?.popLast() }
        )
    }
}

@Composable
private fun Page(
    style: AppPreferencesStyle,
    preferenceState: MutableState<PreferenceState?>,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val navigator = LocalNavigator.current
    AppPreferences(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        style = style,
        onPreferenceStateChanged = { preferenceState.value = it },
        // the screen handles the back press itself, so we don't want to handle it here
        // inside a dialog it should handle the back press automatically though
        handleBackPress = navigator != null
    )
    if (navigator != null) {
        RegisterBackHandler(
            canHandle = { (preferenceState.value?.currentLevel ?: 0) > 0 },
            handle = {
                L.logIf(ToolboxLogging.Tag.Navigation)
                    ?.i { "BackHandlerRegistry - PreferenceState handles back press" }
                preferenceState.value?.popLast()
            }
        )
        NavigatorBackHandler(navigator)
    }
}