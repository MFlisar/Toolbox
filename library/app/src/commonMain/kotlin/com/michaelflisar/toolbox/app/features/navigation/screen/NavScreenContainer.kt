package com.michaelflisar.toolbox.app.features.navigation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import cafe.adriel.voyager.navigator.Navigator
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.parcelize.Parcelable
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorSlideTransition
import com.michaelflisar.toolbox.app.features.navigation.NavBackHandler
import com.michaelflisar.toolbox.app.features.navigation.lastNavItem
import kotlin.jvm.Transient

abstract class NavScreenContainer(
    val rootScreen: NavScreen,
) : INavScreen, Parcelable {

    //override val key: ScreenKey = if (DISABLE_ANIMATION) super.key else uniqueScreenKey

    @Transient
    @IgnoredOnParcel
    internal var navigator: MutableState<Navigator?> = mutableStateOf(null)

    @Composable
    override fun provideData(): State<NavScreenData> {
        //val currentNavItem = navigator.value?.lastNavItem
        //return currentNavItem?.provideData() ?: rootScreen.provideData()
        return rootScreen.provideData()
    }

    @Composable
    override fun provideMenu(): List<MenuItem> {
        val currentNavItem = navigator.value?.lastNavItem
        return currentNavItem?.provideMenu() ?: rootScreen.provideMenu()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        AppNavigator(
            screen = rootScreen,
            onBackPressed = {
                L.i { "onBackPressed called in NavScreenContainer Navigator" }
                false
            }
        ) { navigator ->
            DisposableEffect(navigator) {
                this@NavScreenContainer.navigator.value = navigator
                onDispose {
                    this@NavScreenContainer.navigator.value = null
                }
            }
            NavBackHandler()
            AppNavigatorSlideTransition(navigator)
        }
    }
}