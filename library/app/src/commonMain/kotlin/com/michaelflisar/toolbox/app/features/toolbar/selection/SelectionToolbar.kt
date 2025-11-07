package com.michaelflisar.toolbox.app.features.toolbar.selection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.menu_selection_info
import org.jetbrains.compose.resources.stringResource

val LocalSelectionToolbarState = compositionLocalOf { SelectionToolbarState() }

class SelectionToolbarState internal constructor(
    val data: MutableState<SelectionData<*, *>?> = mutableStateOf(null)
) {
    val isInSelectionMode: Boolean
        get() = data.value?.isActive?.value == true

    val totalCount: Int
        get() = data.value?.total ?: 0

    val selectedCount: Int
        get() = data.value?.selected ?: 0

    fun clearSelection(finish: Boolean) {
        data.value?.clearSelection()
        if (finish) {
            finishSelectionMode()
        }
    }

    fun finishSelectionMode() {
        //println("isActive => finishSelectionMode")
        data.value?.clearSelection()
        data.value?.isActive?.value = false
        data.value = null
    }

    fun startSelectionMode(selectionData: SelectionData<*, *>) {
        data.value = selectionData
        selectionData.isActive.value = true
    }

    fun ensureSelectionMode(selectionData: SelectionData<*, *>) {
        if (data.value != selectionData || !selectionData.isActive.value) {
            startSelectionMode(selectionData)
        }
    }

    fun restoreSelectionMode(selectionData: SelectionData<*, *>) {
        if (selectionData.isActive.value) {
            ensureSelectionMode(selectionData)
        }
    }
}

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
}

@Composable
fun ResetSelectionToolbarOnScreenChange() {
    val navigator = LocalNavigator.currentOrThrow
    val selectionToolbarState = LocalSelectionToolbarState.current
    val lastKey = rememberSaveable { mutableStateOf(navigator.lastItem.key) }
    LaunchedEffect(navigator.lastItem.key) {
        if (lastKey.value != navigator.lastItem.key) {
            lastKey.value = navigator.lastItem.key
            selectionToolbarState.finishSelectionMode()
        }
    }
}

@Composable
fun AnimatedSelectionToolbarWrapper(
    modifier: Modifier = Modifier,
    toolbar: @Composable () -> Unit,
    selectionToolbar: @Composable () -> Unit,
) {
    val selectionToolbarState = LocalSelectionToolbarState.current
    Crossfade(
        modifier = modifier,
        targetState = selectionToolbarState.isInSelectionMode
    ) { selectionMode ->
        if (selectionMode) {
            selectionToolbar()
        } else {
            toolbar()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionToolbar(
    modifier: Modifier = Modifier,
    title: @Composable (selected: Int, total: Int) -> String = { selected, total ->
        stringResource(Res.string.menu_selection_info, selected, total)
    },
    colors: TopAppBarColors = SelectionToolbarDefaults.colorsBackground()
) {
    val toolbarState = LocalSelectionToolbarState.current

    // title beim beenden der selection mode merken, damit es beim fade out nicht falsch angezeigt wird
    val lastTitle = remember { mutableStateOf("") }
    if (toolbarState.isInSelectionMode) {
        lastTitle.value = title(toolbarState.selectedCount, toolbarState.totalCount)
    }

    AnimatedVisibility(
        visible = toolbarState.isInSelectionMode,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LaunchedEffect(toolbarState.data.value) {
            println("SelectionToolbar - data changed: ${toolbarState.data.value}")
        }
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
                        toolbarState.clearSelection(finish = true)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                }
            },
            actions = {
                // Menu rendern
                toolbarState.data.value?.Menu()
            },
            colors = colors
        )
    }
}