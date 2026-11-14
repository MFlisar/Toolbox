package com.michaelflisar.toolbox.app.features.navigation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.parcelize.Parcelable
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorSlideTransition
import com.michaelflisar.toolbox.app.features.navigation.lastNavItem
import kotlin.jvm.Transient

/**
 * A NavScreen that contains a nested Navigator. The rootScreen is the first screen that is shown in the nested Navigator.
 * The rootScreen is also used to provide the Toolbar and MenuItems for the nested Navigator, but can be overridden by the current screen in the nested Navigator.
 *
 * @param rootScreen The first screen that is shown in the nested Navigator.
 * @param supportRootBackButton set it to true if the container is not used with a on screen navigation like a bottom bar so that a toolbar in the root screen will show a back button that pops the root screen
 */
abstract class NavScreenContainer(
    val rootScreen: NavScreen,
    val supportRootBackButton: Boolean = false
) : INavScreen, Parcelable {

    //override val key: ScreenKey = if (DISABLE_ANIMATION) super.key else uniqueScreenKey

    @Transient
    @IgnoredOnParcel
    var rootNavigator: MutableState<Navigator?> = mutableStateOf(null)
        internal set

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
        currentNavItem?.Toolbar() ?: rootScreen.Toolbar()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        DisposableEffect(rootNavigator) {
            this@NavScreenContainer.rootNavigator.value = rootNavigator
            onDispose {
                this@NavScreenContainer.rootNavigator.value = null
            }
        }
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