package com.michaelflisar.toolbox.app.features.navigation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.app.features.device.BaseDevice
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.app.features.navigation.NavBackHandler
import com.michaelflisar.toolbox.app.features.toolbar.DesktopToolbar

class NavScreenBackPressHandler(
    val canHandle: @Composable () -> Boolean,
    val handle: () -> Unit = {},
)

abstract class NavScreen : INavScreen {

    //override val key: ScreenKey = if (DISABLE_ANIMATION) super.key else uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    final override fun Content() {
        Column {
            when (CurrentDevice.base) {
                BaseDevice.Mobile,
                BaseDevice.Web,
                    -> {
                    // --
                }

                BaseDevice.Desktop -> {
                    val navigator = LocalNavigator.currentOrThrow
                    val isBackPressHandled = NavBackHandler.canHandleBackPress()
                    val customBackHandlerPressCanHandleBackPress =
                        navScreenBackPressHandler?.canHandle() == true
                    NavBackHandler(
                        canGoBack = customBackHandlerPressCanHandleBackPress
                    ) {
                        L.i { "onBack called in NavScreen - Toolbar | navScreenBackPressHandler = $navScreenBackPressHandler" }
                        navScreenBackPressHandler?.handle()
                    }
                    DesktopToolbar(
                        showBackButton = navigator.canPop || customBackHandlerPressCanHandleBackPress,
                        onBack = {
                            if (isBackPressHandled) {
                                // --
                            } else if (customBackHandlerPressCanHandleBackPress) {
                                navScreenBackPressHandler?.handle()
                            } else {
                                navigator.pop()
                            }
                        }
                    )
                }
            }
            Screen()
        }
    }

    open val navScreenBackPressHandler: NavScreenBackPressHandler? = null

    @Composable
    abstract fun Screen()

}

class NavScreenData internal constructor(
    val title: String,
    val subTitle: String?,
    val icon: IconComposable? = null,
)

@Composable
fun rememberNavScreenData(
    title: String,
    subTitle: String?,
    icon: IconComposable? = null,
): State<NavScreenData> {
    return remember(title, subTitle, icon) {
        derivedStateOf {
            NavScreenData(
                title = title,
                subTitle = subTitle,
                icon = icon
            )
        }
    }
}