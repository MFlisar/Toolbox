package com.michaelflisar.toolbox.app.features.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.features.navigation.NavBackHandler
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreenData
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.logIf

interface IDesktopToolbarContentProvider {
    @Composable
    fun ToolbarContent()
}

@Composable
fun DesktopPage(
    screen: NavScreen,
    toolbar: @Composable () -> Unit = { DesktopToolbar(screen) },
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
    screen: NavScreen
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

@Composable
private fun DesktopToolbar(
    screen: NavScreen,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
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
                        showBackButton,
                        onClick = onBack,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    ToolbarTitle(toolbarData, modifier = Modifier.weight(1f).heightIn(min = 56.dp))
                    screen.ToolbarContent()
                }
            } else {
                ToolbarTitle(
                    toolbarData = toolbarData,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .heightIn(min = 56.dp)
                        .padding(start = 56.dp, end = 56.dp)
                )
                ToolbarBackButton(
                    showBackButton,
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

@Composable
internal fun ToolbarTitle(
    toolbarData: State<NavScreenData>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = toolbarData.value.title,
            style = MaterialTheme.typography.titleMedium
        )
        toolbarData.value.subTitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}