package com.michaelflisar.toolbox.feature.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.components.MyTooltipBox
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.menu_copy
import com.michaelflisar.toolbox.core.resources.menu_cut
import com.michaelflisar.toolbox.core.resources.menu_delete
import com.michaelflisar.toolbox.core.resources.menu_deselect_all
import com.michaelflisar.toolbox.core.resources.menu_duplicate
import com.michaelflisar.toolbox.core.resources.menu_invert_selection
import com.michaelflisar.toolbox.core.resources.menu_move
import com.michaelflisar.toolbox.core.resources.menu_region_selection
import com.michaelflisar.toolbox.core.resources.menu_select_all
import com.michaelflisar.toolbox.drawables.Move
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.selection.SelectionDataItems
import org.jetbrains.compose.resources.stringResource

object MultiSelectionMenu {

    @Stable
    sealed class Action {

        @Composable
        abstract fun MenuItem()

        @Composable
        abstract fun MenuItem(scope: MenuScope)

        @Stable
        class Item(
            val icon: IconComposable,
            val text: String,
            val enabled: Boolean = true,
            val onClick: () -> Unit,
        ) : Action() {

            @Composable
            override fun MenuItem() {
                MyTooltipBox(
                    tooltip = text
                ) {
                    MyIconButton(
                        icon = icon,
                        enabled = enabled,
                        onClick = onClick
                    )
                }
            }

            @Composable
            override fun MenuItem(scope: MenuScope) {
                with(scope) {
                    MenuItem(
                        text = { Text(text) },
                        icon = icon,
                        enabled = enabled,
                        onClick = onClick
                    )
                }
            }
        }

        @Stable
        class Separator(
            val text: String,
        ) : Action() {

            @Composable
            override fun MenuItem() {
                VerticalDivider()
            }

            @Composable
            override fun MenuItem(scope: MenuScope) {
                with(scope) {
                    MenuSeparator(text = text)
                }
            }
        }

        @Stable
        class Menu(
            val icon: IconComposable,
            val text: String,
            val enabled: Boolean = true,
            val subItems: List<Action>,
        ) : Action() {

            @Composable
            override fun MenuItem() {
                val menu = rememberMenuState()
                MyTooltipBox(
                    tooltip = text
                ) {
                    Box {
                        MyIconButton(
                            icon = icon,
                            enabled = enabled,
                            onClick = { menu.show() }
                        )
                        PopupMenu(
                            state = menu
                        ) {
                            subItems.forEach { it.MenuItem(this) }
                        }
                    }
                }
            }

            @Composable
            override fun MenuItem(scope: MenuScope) {
                with(scope) {
                    MenuSubMenu(
                        text = text,
                        icon = icon,
                        enabled = enabled
                    ) {
                        subItems.forEach { it.MenuItem(this) }
                    }
                }
            }
        }
    }
}

object MultiSelectionMenuDefaults {

    object Actions {

        @Composable
        fun regionDivider() = MultiSelectionMenu.Action.Separator(
            text = ""
        )

        @Composable
        fun regionSelection() = MultiSelectionMenu.Action.Separator(
            text = stringResource(Res.string.menu_region_selection)
        )

        @Composable
        fun <ID : Comparable<ID>> selectAll(
            allIds: List<ID>,
            selectionData: SelectionDataItems<ID>,
            enabled: Boolean = true,
        ) = MultiSelectionMenu.Action.Item(
            text = stringResource(Res.string.menu_select_all),
            icon = Icons.Default.SelectAll.toIconComposable(),
            enabled = enabled && selectionData.selectedIds.value.sorted() != allIds.sorted(),
            onClick = { selectionData.select(allIds) }
        )

        @Composable
        fun <ID : Comparable<ID>> deselectAll(
            selectionData: SelectionDataItems<ID>,
            enabled: Boolean = true,
        ) = MultiSelectionMenu.Action.Item(
            text = stringResource(Res.string.menu_deselect_all),
            icon = Icons.Default.Deselect.toIconComposable(),
            enabled = enabled && selectionData.isSomethingSelected,
            onClick = { selectionData.clearSelection() }
        )


        @Composable
        fun <ID : Comparable<ID>> invertSelection(
            allIds: List<ID>,
            selectionData: SelectionDataItems<ID>,
            enabled: Boolean = true,
        ) = MultiSelectionMenu.Action.Item(
            text = stringResource(Res.string.menu_invert_selection),
            icon = Icons.Default.SwapHoriz.toIconComposable(),
            enabled = enabled,
            onClick = {
                val currentlySelected = selectionData.selectedIds.value.toSet()
                selectionData.clearSelection()
                selectionData.select(allIds.filter { it !in currentlySelected })
            }
        )

        @Composable
        fun <ID : Comparable<ID>> delete(
            selectionData: SelectionDataItems<ID>,
            enabled: Boolean = true,
            onClick: () -> Unit,
        ) = MultiSelectionMenu.Action.Item(
            text = stringResource(Res.string.menu_delete),
            icon = Icons.Default.Delete.toIconComposable(
                if (enabled && selectionData.isSomethingSelected)
                    MaterialTheme.colorScheme.error
                else
                    Color.Unspecified
            ),
            enabled = enabled && selectionData.isSomethingSelected,
            onClick = onClick
        )

        @Composable
        fun <ID : Comparable<ID>> move(
            selectionData: SelectionDataItems<ID>,
            enabled: Boolean = true,
            onClick: () -> Unit,
        ) = MultiSelectionMenu.Action.Item(
            text = stringResource(Res.string.menu_move),
            icon = Move.toIconComposable(),
            enabled = enabled && selectionData.isSomethingSelected,
            onClick = onClick
        )

        @Composable
        fun <ID : Comparable<ID>> copy(
            selectionData: SelectionDataItems<ID>,
            enabled: Boolean = true,
            onClick: () -> Unit,
        ) = MultiSelectionMenu.Action.Item(
            text = stringResource(Res.string.menu_copy),
            icon = Icons.Default.CopyAll.toIconComposable(),
            enabled = enabled && selectionData.isSomethingSelected,
            onClick = onClick
        )

        @Composable
        fun <ID : Comparable<ID>> duplicate(
            selectionData: SelectionDataItems<ID>,
            enabled: Boolean = true,
            onClick: () -> Unit,
        ) = MultiSelectionMenu.Action.Item(
            text = stringResource(Res.string.menu_duplicate),
            icon = Icons.Default.ContentCopy.toIconComposable(),
            enabled = enabled && selectionData.isSomethingSelected,
            onClick = onClick
        )

        @Composable
        fun <ID : Comparable<ID>> cut(
            selectionData: SelectionDataItems<ID>,
            enabled: Boolean = true,
            onClick: () -> Unit,
        ) = MultiSelectionMenu.Action.Item(
            text = stringResource(Res.string.menu_cut),
            icon = Icons.Default.ContentCut.toIconComposable(),
            enabled = enabled && selectionData.isSomethingSelected,
            onClick = onClick
        )
    }
}

@Composable
fun MultiSelectionMenu(
    items: List<MultiSelectionMenu.Action>,
) {
    items.forEach { it.MenuItem() }
}