package com.michaelflisar.toolbox.app

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.jakewharton.processphoenix.ProcessPhoenix
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

    abstract fun initEssential() : AppSetup
    abstract fun initFull(setup: AppSetup)

}

@Composable
fun ComponentActivity.AndroidApplication(
    // Navigator
    screen: Screen,
    theme: MyTheme = MyTheme.default(),
    // Content
    content: @Composable (navigator: Navigator) -> Unit,
) {
    ProvideAppLocals(this) {
        AppNavigator(
            screen = screen
        ) { navigator ->
            val appState = rememberAppState()
            AppThemeProvider(theme) {
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