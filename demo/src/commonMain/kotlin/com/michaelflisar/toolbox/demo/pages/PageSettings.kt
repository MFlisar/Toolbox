package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.composepreferences.core.classes.PreferenceState
import com.michaelflisar.composepreferences.core.classes.rememberPreferenceState
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreenBackPressHandler
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.app.features.preferences.BaseAppPreferences
import com.michaelflisar.toolbox.app.features.preferences.groups.PreferenceSettingsTheme
import com.michaelflisar.toolbox.demo.resources.Res
import com.michaelflisar.toolbox.demo.resources.page_settings
import com.michaelflisar.toolbox.extensions.toIconComposable
import org.jetbrains.compose.resources.stringResource
import kotlin.jvm.Transient

@Parcelize
object PageSettingsScreen : NavScreen() {

    @Transient
    @IgnoredOnParcel
    private var preferenceState: MutableState<PreferenceState?> = mutableStateOf(null)

    @Composable
    override fun provideData() = rememberNavScreenData(
        title = stringResource(Res.string.page_settings),
        subTitle = null,
        icon = Icons.Default.Settings.toIconComposable()
    )

    @Composable
    override fun provideMenu(): List<MenuItem> {
        return emptyList()
    }

    @Composable
    override fun Screen() {
        val state = rememberPreferenceState()
        DisposableEffect(state) {
            //L.i { "PageSettingsScreen: DisposableEffect called" }
            preferenceState.value = state
            onDispose {
                preferenceState.value = null
            }
        }
        Page(state)
    }

    override val navScreenBackPressHandler = NavScreenBackPressHandler(
        canHandle = { (preferenceState.value?.currentLevel ?: 0) > 0 },
        handle = { preferenceState.value?.popLast() }
    )
}

@Composable
private fun Page(
    preferenceState: PreferenceState,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
        //.padding(all = 16.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val navigator = LocalNavigator.current
        BaseAppPreferences(
            showProVersionDialog = rememberDialogState(),
            preferenceState = preferenceState,
            // the screen handles the back press itself, so we don't want to handle it here
            // inside a dialog it should handle the back press automatically though
            handleBackPress = navigator != null
        ) {
            PreferenceSettingsTheme(true)
        }
    }
}