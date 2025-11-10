package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.app.features.toolbar.selection.LocalSelectionToolbarState
import com.michaelflisar.toolbox.feature.selection.SelectionDataItems
import com.michaelflisar.toolbox.app.features.toolbar.selection.SelectionToolbarState
import com.michaelflisar.toolbox.app.features.toolbar.selection.rememberSelectionDataItems
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.menu.MenuItem
import com.michaelflisar.toolbox.feature.menu.PopupMenu
import com.michaelflisar.toolbox.feature.menu.rememberMenuState

@Parcelize
object PageSelectionScreen : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        title = "Selection Test",
        subTitle = null,
        icon = Icons.Default.SelectAll.toIconComposable()
    )

    @Composable
    override fun provideMenu(): List<MenuItem> {
        return emptyList()
    }

    @Composable
    override fun Screen() {
        Page(this)
    }

}

@Composable
private fun Page(
    screen: NavScreen,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    MyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize()
    ) {
        val totalCount = 10
        val selectionToolbarState = LocalSelectionToolbarState.current

        val selectionData = rememberSelectionDataItems<Int>(
            totalItemCount = totalCount
        ) {
            SelectionMenu(selectionToolbarState)
        }

        Text("Counter: ${selectionToolbarState.selectedCount}")

        MyButton(
            onClick = {
                changeSelection(
                    selectionToolbarState,
                    selectionData,
                    true,
                    selectionToolbarState.selectedCount
                )
            },
            enabled = selectionToolbarState.selectedCount < totalCount
        ) {
            Text("Increment counter")
        }
        MyButton(
            onClick = {
                changeSelection(
                    selectionToolbarState,
                    selectionData,
                    false,
                    selectionToolbarState.selectedCount - 1
                )
            },
            enabled = selectionToolbarState.selectedCount > 0
        ) {
            Text("Decrement counter")
        }
    }
}

private fun changeSelection(
    selectionToolbarState: SelectionToolbarState,
    selectionData: SelectionDataItems<Int>,
    add: Boolean,
    id: Int
) {
    // update selection data
    if (add) {
        selectionData.select(id)
        L.d { "add $id | selected: ${selectionData.selected}" }
    } else {
        selectionData.deselect(id)
    }

    // show selection toolbar if not already shown
    selectionToolbarState.ensureSelectionMode(selectionData)
}

@Composable
private fun SelectionMenu(
    selectionToolbarState: SelectionToolbarState
) {
    val showMenu = rememberMenuState()
    Box(
        modifier = Modifier,
    ) {
        IconButton(
            onClick = {
                showMenu.show()
            },
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        PopupMenu(
            state = showMenu
        ) {
            MenuItem(
                text = { Text("Delete") },
                icon = Icons.Default.Delete.toIconComposable(),
                onClick = {
                    // ...
                    selectionToolbarState.clearSelection(finish = true)
                }
            )
        }
    }
}