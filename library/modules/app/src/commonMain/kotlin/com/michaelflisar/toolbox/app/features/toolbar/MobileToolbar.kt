package com.michaelflisar.toolbox.app.features.toolbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState

@Composable
internal expect fun UpdateStatusBarColor(background: Color)

@ExperimentalMaterial3Api
@Composable
fun MobileToolbar(
    modifier: Modifier = Modifier,
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    // stellt sicher dass Prefs.toolbarStyle genutzt wird!
    background: Color = MaterialTheme.colorScheme.toolbar,
    onBackground: Color = MaterialTheme.colorScheme.onToolbar
) {
    val appState = LocalAppState.current

    UpdateStatusBarColor(background)

    TopAppBar(
        modifier = modifier,
        title = {
            Column {
                Text(appState.toolbar.title.value)
                appState.toolbar.subtitle.value.takeIf { it.isNotEmpty() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (appState.toolbar.showBackButton.value) {
                IconButton(
                    onClick = { }
                ) {
                    Icon(Icons.Default.Menu, null)
                }
            }
        },
        actions = {
            appState.toolbar.Menu()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = background,
            scrolledContainerColor = background,
            titleContentColor = onBackground,
            navigationIconContentColor = onBackground,
            actionIconContentColor = onBackground,
        )
    )
}