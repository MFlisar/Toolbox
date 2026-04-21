package com.michaelflisar.toolbox.app.features.toolbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.features.backhandlerregistry.RegisterBackHandler
import com.michaelflisar.toolbox.app.platform.UpdateComposeThemeStatusBar
import com.michaelflisar.toolbox.feature.selection.SelectionState
import com.michaelflisar.toolbox.info

object SelectionToolbarDefaults {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun colorsBackground() = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        scrolledContainerColor = MaterialTheme.colorScheme.background,
        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        actionIconContentColor = MaterialTheme.colorScheme.onBackground,
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun colorsSurfaceContainerHigh() = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface,
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun colorsPrimary() = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        scrolledContainerColor = MaterialTheme.colorScheme.primary,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun colorsPrimaryContainer() = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )

    val DefaultStyle: SelectionToolbar.Style = SelectionToolbar.Style.Default
}

object SelectionToolbar {
    sealed class Style {
        object Default : Style()
        class Floating(
            val padding: PaddingValues,
            val shape: Shape,
            val height: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
            val applyPaddingForStatusbar: Boolean = true,
        ) : Style()
    }
}

@Composable
fun AnimatedSelectionToolbarWrapper(
    modifier: Modifier = Modifier,
    selection: SelectionState<*>,
    toolbar: @Composable () -> Unit = {},
    selectionToolbar: @Composable () -> Unit,
) {
    Crossfade(
        modifier = modifier,
        targetState = selection.visible
    ) { visible ->
        if (visible) {
            selectionToolbar()
        } else {
            toolbar()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionToolbar(
    selection: SelectionState<*>,
    modifier: Modifier = Modifier,
    title: @Composable (selected: Int, total: Int) -> String = { selected, total -> "$selected/$total" },
    colors: TopAppBarColors = SelectionToolbarDefaults.colorsBackground(),
    style: SelectionToolbar.Style = SelectionToolbarDefaults.DefaultStyle,
    actions: @Composable RowScope.() -> Unit,
) {
    // title beim beenden der selection mode merken, damit es beim fade out nicht falsch angezeigt wird
    val lastTitle = remember { mutableStateOf("") }
    if (selection.visible) {
        lastTitle.value = title(selection.selectedCount, selection.totalCount)
    }

    // Back Handling
    RegisterBackHandler(
        canHandle = {
            val handle = selection.visible
            println("SelectionToolbarState: canHandleBackPress = $handle")
            handle
        },
        handle = {
            L.info(ToolboxLogging.Tag.Navigation) { "BackHandlerRegistry - SelectionToolbarState handles back press" }
            selection.clearAndClose()
        }
    )

    when (style) {
        SelectionToolbar.Style.Default -> {
            // StatusBar anpassen
            Platform.UpdateComposeThemeStatusBar(
                statusBarColor = if (selection.visible) colors.containerColor else MaterialTheme.colorScheme.toolbar
            )
            // TopAppBar
            AnimatedVisibility(
                visible = selection.visible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TopAppBar(
                    modifier = modifier,
                    title = {
                        Text(
                            text = lastTitle.value,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                selection.clearAndClose()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null
                            )
                        }
                    },
                    actions = actions,
                    colors = colors
                )
            }
        }

        is SelectionToolbar.Style.Floating -> {
            AnimatedVisibility(
                visible = selection.visible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = modifier
                ) {
                    if (style.applyPaddingForStatusbar) {
                        Box(
                            modifier = Modifier
                                .height(
                                    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                                )
                                .fillMaxWidth()
                        )
                    }
                    TopAppBar(
                        modifier = Modifier
                            .height(style.height)
                            .padding(style.padding)
                            .clip(style.shape),
                        windowInsets = WindowInsets(),
                        title = {
                            Text(
                                text = lastTitle.value,
                                style = MaterialTheme.typography.titleLarge,
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    selection.clearAndClose()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null
                                )
                            }
                        },
                        actions = actions,
                        colors = colors
                    )
                }
            }
        }
    }
}