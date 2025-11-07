package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.features.navigation.screen.INavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreenContainer

val Navigator.lastNavItem: INavScreen
    get() {
        val item = this.lastItem
        return item as? INavScreen
            ?: throw IllegalStateException("The last item in the navigator is not a NavScreen: $item")
    }

/**
 * nutzt den current screen [NavScreenContainer] um den lokalen Navigator zu finden => braucht man nur wenn man außerhalb des contents ist
 * (z.B. in Toolbar)
 */
val ProvidableCompositionLocal<Navigator?>.findLocalByScreenOrThrow: Navigator
    @Composable
    get() = currentOrThrow.localNavigator

private val Navigator.localNavigator: Navigator
    get() {
        var nav = this
        var item = lastItem
        while (item is NavScreenContainer) {
            nav = item.navigator.value ?: break
            item = nav.lastItem
        }
        return nav
    }

val Navigator.navItemContainer: NavScreenContainer?
    get() {
        var nav: Navigator? = null
        var item: INavScreen? = lastNavItem
        while (item != null && item !is NavScreenContainer) {
            nav = nav?.parent
            item = nav?.lastNavItem
        }
        return item
    }
