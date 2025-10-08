package com.michaelflisar.toolbox.app.features.navigation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.app.features.device.BaseDevice
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.toolbar.DesktopPage

class NavScreenBackPressHandler(
    val canHandle: @Composable () -> Boolean,
    val handle: () -> Unit = {},
)

abstract class NavScreen : INavScreen {

    //override val key: ScreenKey = if (DISABLE_ANIMATION) super.key else uniqueScreenKey

    @Composable
    final override fun Content() {
        when (CurrentDevice.base) {
            BaseDevice.Mobile,
            BaseDevice.Web -> {
                Screen()
            }

            BaseDevice.Desktop -> {
                DesktopPage(
                    screen = this
                ) {
                    Screen()
                }
            }
        }
    }

    override val navScreenBackPressHandler: NavScreenBackPressHandler? = null

    @Composable
    abstract fun Screen()

    @Composable
    override fun provideMenu(): List<MenuItem> = emptyList()

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