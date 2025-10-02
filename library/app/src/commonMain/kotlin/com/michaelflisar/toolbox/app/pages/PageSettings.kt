package com.michaelflisar.toolbox.app.pages

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.michaelflisar.composepreferences.core.classes.PreferenceState
import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreenBackPressHandler
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.app.features.preferences.AppPreferences
import com.michaelflisar.toolbox.app.features.preferences.AppPreferencesStyle
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.menu_settings
import com.michaelflisar.toolbox.extensions.toIconComposable
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
        title = stringResource(Res.string.menu_settings),
        subTitle = null,
        icon = Icons.Default.Settings.toIconComposable()
    )

    @Composable
    override fun provideMenu(): List<MenuItem> = emptyList()

    @Composable
    override fun Screen() {
        Page(provideStyle(), preferenceState)
    }

    override val navScreenBackPressHandler = NavScreenBackPressHandler(
        canHandle = { (preferenceState.value?.currentLevel ?: 0) > 0 },
        handle = { preferenceState.value?.popLast() }
    )
}

@Composable
private fun Page(
    style: AppPreferencesStyle,
    preferenceState: MutableState<PreferenceState?>,
    paddingValues: PaddingValues = PaddingValues(0.dp)
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
}