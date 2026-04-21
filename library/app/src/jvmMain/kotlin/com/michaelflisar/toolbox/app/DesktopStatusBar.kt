package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.DesktopAppDefaults.getDefaultStatusBarItemsLeft
import com.michaelflisar.toolbox.app.DesktopAppDefaults.getDefaultStatusBarItemsRight
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemSpacer
import com.michaelflisar.toolbox.app.features.navigationbar.NavigationBar
import com.michaelflisar.toolbox.app.features.scaffold.NavigationStyle
import com.michaelflisar.toolbox.app.jewel.JewelNavigation
import com.michaelflisar.toolbox.app.jewel.JewelStatusBar
import com.michaelflisar.toolbox.app.jewel.JewelTitleBar
import com.michaelflisar.toolbox.app.jewel.JewelTitleMenu
import com.michaelflisar.toolbox.app.jewel.toJewelNavigationItems
import org.jetbrains.jewel.window.DecoratedWindowScope

sealed class DesktopStatusBarItem {

    class Text(
        val text: String,
        val color: Color = Color.Unspecified,
        val onClick: (() -> Unit)? = null,
    ) : DesktopStatusBarItem()

    class Custom(
        val content: @Composable () -> Unit,
    ) : DesktopStatusBarItem()
}

@Composable
fun DesktopStatusBar(
    showAppVersionLeft: Boolean = true,
    showJavaVersionRight: Boolean = true,
    showUserNameRight: Boolean = true,
    showHostNameRight: Boolean = true,
    onAppVersionClick: (() -> Unit)? = null,
    onJavaVersionClick: (() -> Unit)? = null,
    onUserNameClick: (() -> Unit)? = null,
    onHostNameClick: (() -> Unit)? = null,
    foreground: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    content: @Composable (() -> Unit)? = null,
) {
    val statusBarLeft = getDefaultStatusBarItemsLeft(
        showAppVersionLeft,
        onAppVersionClick
    )
    val statusBarRight = getDefaultStatusBarItemsRight(
        showJavaVersionRight,
        showUserNameRight,
        showHostNameRight,
        onJavaVersionClick,
        onUserNameClick,
        onHostNameClick
    )
    if (statusBarLeft.isEmpty() && statusBarRight.isEmpty()) {
        return // nothing to show
    }
    JewelStatusBar(
        left = statusBarLeft,
        right = statusBarRight,
        foreground = foreground,
        background = background,
        content = content
    )
}

@Composable
fun DesktopStatusBarCustom(
    left: List<DesktopStatusBarItem> = emptyList(),
    right: List<DesktopStatusBarItem> = emptyList(),
    foreground: Color = Color.Unspecified,
    background: Color = Color.Unspecified,
    content: @Composable (() -> Unit)? = null,
) {
    JewelStatusBar(
        left = left,
        right = right,
        foreground = foreground,
        background = background,
        content = content
    )
}
