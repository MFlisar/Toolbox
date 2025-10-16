package com.michaelflisar.toolbox.app.features.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.features.menu.Menu
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.NavBackHandler
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.logIf

typealias DesktopToolbarProvider = @Composable (screen: NavScreen) -> Unit
internal val LocalDesktopToolbarProvider = compositionLocalOf<DesktopToolbarProvider> { throw IllegalStateException("No DesktopToolbarProvider provided") }

object DesktopToolbar {

    @OptIn(ExperimentalMaterial3Api::class)
    val height = TopAppBarDefaults.TopAppBarExpandedHeight

}

interface IDesktopToolbarContentProvider {
    @Composable
    fun ToolbarContent()
}

@Composable
internal fun DesktopPage(
    toolbar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Column {
        toolbar()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            content()
        }
    }
}

@Composable
fun DesktopToolbar(
    screen: NavScreen,
    menuItems: List<MenuItem> = emptyList(),
) {
    val navigator = LocalNavigator.currentOrThrow
    val isBackPressHandled = NavBackHandler.canHandleBackPress()
    val customBackHandlerPressCanHandleBackPress =
        screen.navScreenBackPressHandler?.canHandle() == true
    val canPop = remember(screen, navigator.lastItem) { navigator.items.indexOf(screen) > 0 }
    NavBackHandler(
        canGoBack = customBackHandlerPressCanHandleBackPress && canPop
    ) {
        L.logIf(ToolboxLogging.Tag.Navigation)
            ?.i { "onBack called in NavScreen - Toolbar | navScreenBackPressHandler = ${screen.navScreenBackPressHandler}" }
        screen.navScreenBackPressHandler?.handle()
    }
    DesktopToolbar(
        screen = screen,
        menuItems = menuItems,
        showBackButton = canPop || customBackHandlerPressCanHandleBackPress,
        onBack = {
            if (isBackPressHandled) {
                // --
            } else if (customBackHandlerPressCanHandleBackPress) {
                screen.navScreenBackPressHandler?.handle()
            } else {
                navigator.pop()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DesktopToolbar(
    screen: NavScreen,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    menuItems: List<MenuItem> = emptyList(),
    onBack: () -> Unit = {}
) {
    val navigator = LocalNavigator.currentOrThrow
    val toolbarData = screen.provideData()

    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onToolbar,
        LocalTextStyle provides MaterialTheme.typography.titleMedium
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(DesktopToolbar.height)
                .background(MaterialTheme.colorScheme.toolbar),
            contentAlignment = Alignment.CenterStart
        ) {
            if (screen is IDesktopToolbarContentProvider) {
                MyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    ToolbarBackButton(
                        showBackButton = showBackButton,
                        onClick = onBack,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    ToolbarTitle(
                        toolbarData = toolbarData,
                        modifier = Modifier.weight(1f).heightIn(min = DesktopToolbar.height),
                        endContent = {
                            Menu(menuItems)
                        }
                    )
                    screen.ToolbarContent()
                }
            } else {
                ToolbarTitle(
                    toolbarData = toolbarData,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .heightIn(min = DesktopToolbar.height)
                        .padding(start = 56.dp, end = 56.dp),
                    endContent = {
                        Menu(menuItems)
                    }
                )
                ToolbarBackButton(
                    showBackButton = showBackButton,
                    onClick = onBack,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
internal fun ToolbarBackButton(
    showBackButton: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    if (showBackButton) {
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentSize provides 0.dp
        ) {
            Box(
                modifier = modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        }
    }
}

