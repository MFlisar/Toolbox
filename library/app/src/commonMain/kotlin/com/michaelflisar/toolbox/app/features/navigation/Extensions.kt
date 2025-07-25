package com.michaelflisar.toolbox.app.features.navigation

import cafe.adriel.voyager.navigator.Navigator
import com.michaelflisar.toolbox.app.features.navigation.screen.INavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreenContainer

val Navigator.lastNavItem: INavScreen
    get() {
        val item = this.lastItem
        return item as? INavScreen
            ?: throw IllegalStateException("The last item in the navigator is not a NavScreen: $item")
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
