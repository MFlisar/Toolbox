package com.michaelflisar.toolbox.app.features.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.app.features.navigation.lastNavItem
import com.michaelflisar.toolbox.app.features.toolbar.parts.ToolbarBackButton
import com.michaelflisar.toolbox.app.features.toolbar.parts.ToolbarTitle
import com.michaelflisar.toolbox.components.MyRow

interface IDesktopToolbarContentProvider {
    @Composable
    fun ToolbarContent()
}

@ExperimentalMaterial3Api
@Composable
fun DesktopToolbar(
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBack: () -> Unit = {}
) {
    val navigator = LocalNavigator.currentOrThrow
    val currentNavScreen = navigator.lastNavItem
    val toolbarData = currentNavScreen.provideData()

    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onToolbar,
        LocalTextStyle provides MaterialTheme.typography.titleMedium
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.toolbar)
        ) {
            if (currentNavScreen is IDesktopToolbarContentProvider) {
                MyRow {
                    ToolbarBackButton(showBackButton, onBack)
                    ToolbarTitle(toolbarData, modifier = Modifier.padding(8.dp).weight(1f))
                    currentNavScreen.ToolbarContent()
                }
            } else {
                ToolbarTitle(toolbarData, modifier = Modifier.align(Alignment.Center).padding(8.dp))
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    ToolbarBackButton(showBackButton, onBack)
                }
            }

        }
    }
}