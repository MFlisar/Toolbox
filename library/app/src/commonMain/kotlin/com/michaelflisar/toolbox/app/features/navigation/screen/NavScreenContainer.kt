package com.michaelflisar.toolbox.app.features.navigation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import cafe.adriel.voyager.navigator.Navigator
import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.parcelize.Parcelable
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorSlideTransition
import com.michaelflisar.toolbox.app.features.navigation.lastNavItem
import kotlin.jvm.Transient

abstract class NavScreenContainer(
    val rootScreen: NavScreen,
) : INavScreen, Parcelable {

    //override val key: ScreenKey = if (DISABLE_ANIMATION) super.key else uniqueScreenKey

    @Transient
    @IgnoredOnParcel
    var navigator: MutableState<Navigator?> = mutableStateOf(null)
        internal set

    val currentNavItem: INavScreen?
        @Composable
        get() = navigator.value?.lastNavItem

    val currentNavItemOrThrow: INavScreen
        @Composable
        get() = navigator.value?.lastNavItem ?: error("No current NavScreen available")

    @Composable
    override fun provideData(): NavScreenData {
        val currentNavItem = navigator.value?.lastNavItem
        return currentNavItem?.provideData() ?: rootScreen.provideData()
    }

    @Composable
    fun provideRootData(): NavScreenData {
        return rootScreen.provideData()
    }

    @Composable
    override fun Toolbar()
    {
        val currentNavItem = navigator.value?.lastNavItem
        currentNavItem?.Toolbar()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        AppNavigator(
            screen = rootScreen
        ) { navigator ->
            DisposableEffect(navigator) {
                this@NavScreenContainer.navigator.value = navigator
                onDispose {
                    this@NavScreenContainer.navigator.value = null
                }
            }
            AppNavigatorSlideTransition(navigator)
        }
    }
}