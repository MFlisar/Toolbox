package com.michaelflisar.demo.pages

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.extensions.asStateFlow
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.app.features.toolbar.AnimatedSelectionToolbarWrapper
import com.michaelflisar.toolbox.app.features.toolbar.PageToolbar
import com.michaelflisar.toolbox.app.features.toolbar.SelectionToolbar
import com.michaelflisar.toolbox.app.features.toolbar.composables.ToolbarMainMenuItems
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.menu.MultiSelectionMenu
import com.michaelflisar.toolbox.feature.selection.SelectionState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

class PageSelectionScreenModel : ScreenModel {

    val idsFlow: StateFlow<Set<Int>> = flowOf((1..10).toSet())
        .asStateFlow(screenModelScope, emptySet())

    val selection = SelectionState<Int>(idsFlow, screenModelScope)

    // Optional
    override fun onDispose() {
        // ...
    }
}

@Parcelize
object PageSelectionScreen : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        name = "Selection Test",
        icon = Icons.Default.SelectAll.toIconComposable()
    )

    @Composable
    override fun Screen() {
        val screenModel = rememberScreenModel { PageSelectionScreenModel() }
        Page(this, screenModel)
    }

    @Composable
    override fun Toolbar() {
        val data = provideData()
        val screenModel = rememberScreenModel { PageSelectionScreenModel() }

        val selection = screenModel.selection

        AnimatedSelectionToolbarWrapper(
            selection = selection,
            toolbar = {
                PageToolbar(
                    screen = this@PageSelectionScreen,
                    title = data.name,
                    endContent = { ToolbarMainMenuItems(showInOverflow = true) },
                )
            },
            selectionToolbar = {
                SelectionToolbar(
                    selection = screenModel.selection,
                    style = SelectionToolbar.Style.Floating(
                        shape = MaterialTheme.shapes.medium,
                        padding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    )
                ) {
                    MultiSelectionMenu(
                        items = listOf(),
                        overflowItems = listOf(
                            MultiSelectionMenu.Actions.selectAll(selection),
                            MultiSelectionMenu.Actions.deselectAll(selection),
                            MultiSelectionMenu.Actions.delete(selection) {
                                selection.clearAndClose()
                            }
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun Page(
    screen: NavScreen,
    screenModel: PageSelectionScreenModel,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    MyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize()
    ) {
        Text("Counter: ${screenModel.selection.selectedCount}")

        MyButton(
            onClick = {
                changeSelection(
                    selection = screenModel.selection,
                    add = true,
                    id = screenModel.selection.selectedCount
                )
            },
            enabled = !screenModel.selection.isAllSelected
        ) {
            Text("Increment counter")
        }
        MyButton(
            onClick = {
                changeSelection(
                    selection = screenModel.selection,
                    add = false,
                    id = screenModel.selection.selectedCount - 1
                )
            },
            enabled = screenModel.selection.isSomethingSelected
        ) {
            Text("Decrement counter")
        }
    }
}

private fun changeSelection(
    selection: SelectionState<Int>,
    add: Boolean,
    id: Int,
) {
    // update selection data
    if (add) {
        selection.select(id)
        L.d { "add $id | selected: ${selection.selected}" }
    } else {
        selection.deselect(id)
    }

    // show selection toolbar if not already shown
    selection.show()
}