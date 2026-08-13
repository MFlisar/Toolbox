package com.michaelflisar.toolbox.app

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.jakewharton.processphoenix.ProcessPhoenix
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.kmp.platformcontext.PlatformContextProvider
import com.michaelflisar.toolbox.MyTheme
import com.michaelflisar.toolbox.acra.AcraManager
import com.michaelflisar.toolbox.app.features.appstate.rememberAppState
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.root.Root
import com.michaelflisar.toolbox.app.features.root.RootLocalProvider
import com.michaelflisar.toolbox.app.features.theme.AppThemeProvider

abstract class AndroidApplication : Application() {

    final override fun onCreate() {

        super.onCreate()

        if (ProcessPhoenix.isPhoenixProcess(this))
            return

        PlatformContextProvider.init(this)
        val isAcraProcess = isAcraProcess()
        val setup = initEssential()
        // nach minimalem init im ACRA Prozess ggf. abbrechen
        if (isAcraProcess) {
            return
        }
        initFull(setup)
    }

    fun isAcraProcess(): Boolean {
        return AcraManager.isACRAProcess()
    }

    abstract fun initEssential(): AppSetup
    abstract fun initFull(setup: AppSetup)

}

/**
 * This is the main entry point for the app. It sets up the app state, theme, and navigation.
 *
 * composeTheme... default to rememberComposeTheme() which dervids its state from the ThemeSetup
 * appThemeProvider... default to rememberAppTheme() which derives its state from the ThemeSetup
 * theme... default to MyTheme.default()
 */
@Composable
fun ComponentActivity.AndroidApplication(
    // Navigator
    screen: Screen,
    composeTheme: ComposeTheme.State = rememberComposeTheme(),
    appThemeProvider: @Composable () -> AppTheme = { rememberAppTheme() },
    theme: MyTheme = MyTheme.default(),
    // Content
    content: @Composable (navigator: Navigator) -> Unit,
) {
    ProvideAppLocals(this) {
        AppNavigator(
            screen = screen
        ) { navigator ->
            val appState = rememberAppState()
            AppThemeProvider(
                theme = theme,
                composeTheme = composeTheme,
                appThemeProvider = appThemeProvider,
            ) {
                RootLocalProvider(appState, setRootLocals = true) {
                    Root(
                        appState = appState,
                        setRootLocals = false
                    ) {
                        content(navigator)
                    }
                }
            }
        }
    }
}

/**
 * This is the main entry point for the app. It sets up the app state and theme.
 *
 * This overload does NOT provide a navigator!
 *
 * composeTheme... default to rememberComposeTheme() which dervids its state from the ThemeSetup
 * appThemeProvider... default to rememberAppTheme() which derives its state from the ThemeSetup
 * theme... default to MyTheme.default()
 */
@Composable
fun ComponentActivity.AndroidApplication(
    composeTheme: ComposeTheme.State = rememberComposeTheme(),
    appThemeProvider: @Composable () -> AppTheme = { rememberAppTheme() },
    theme: MyTheme = MyTheme.default(),
    content: @Composable () -> Unit,
) {
    ProvideAppLocals(this) {
        val appState = rememberAppState()
        AppThemeProvider(
            theme = theme,
            composeTheme = composeTheme,
            appThemeProvider = appThemeProvider,
        ) {
            RootLocalProvider(appState, setRootLocals = true) {
                Root(
                    appState = appState,
                    setRootLocals = false
                ) {
                    content()
                }
            }
        }
    }
}