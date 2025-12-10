package com.michaelflisar.toolbox.app.features.navigation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.IconComposable

/**
 * WICHTIG: data class nutzen, wegen android restore - ohne hashCode geht das bspw. nicht!
 */
abstract class NavScreen(
    val fillMaxSize: Boolean = true,
) : INavScreen {

    //override val key: ScreenKey = if (DISABLE_ANIMATION) super.key else uniqueScreenKey

    @Composable
    final override fun Content() {
        if (fillMaxSize) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Screen()
            }
        } else {
            Screen()
        }
    }

    @Composable
    abstract fun Screen()

    @Composable
    override fun Toolbar() {
        val data = provideData()
        com.michaelflisar.toolbox.app.features.toolbar.Toolbar(
            title = data.name,
        )
    }
}

class NavScreenData internal constructor(
    val name: String,
    val icon: IconComposable? = null,
)

@Composable
fun rememberNavScreenData(
    name: String,
    icon: IconComposable? = null,
): NavScreenData {
    return remember(name, icon) {
        NavScreenData(
            name = name,
            icon = icon
        )
    }
}