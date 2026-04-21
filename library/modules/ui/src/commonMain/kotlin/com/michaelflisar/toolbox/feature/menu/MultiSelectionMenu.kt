package com.michaelflisar.toolbox.feature.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
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
import com.michaelflisar.toolbox.core.resources.menu_more
import com.michaelflisar.toolbox.core.resources.menu_move
import com.michaelflisar.toolbox.core.resources.menu_region_selection
import com.michaelflisar.toolbox.core.resources.menu_select_all
import com.michaelflisar.toolbox.drawables.Move
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.selection.SelectionState
import org.jetbrains.compose.resources.stringResource

object MultiSelectionMenu {

    @Stable
    sealed class Action {

        @Composable
        abstract fun IconButton()

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
            override fun IconButton() {
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
            override fun IconButton() {
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
            override fun IconButton() {
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

    object Actions {

        @Composable
        fun regionDivider() = Action.Separator(
            text = ""
        )

        @Composable
        fun regionSelection() = Action.Separator(
            text = stringResource(Res.string.menu_region_selection)
        )

        @Composable
        fun <ID : Comparable<ID>> selectAll(
            selection: SelectionState<ID>,
            enabled: Boolean = true,
        ) = Action.Item(
            text = stringResource(Res.string.menu_select_all),
            icon = Icons.Default.SelectAll.toIconComposable(),
            enabled = enabled && !selection.isAllSelected,
            onClick = { selection.selectAll() }
        )

        @Composable
        fun <ID : Comparable<ID>> deselectAll(
            selection: SelectionState<ID>,
            enabled: Boolean = true,
        ) = Action.Item(
            text = stringResource(Res.string.menu_deselect_all),
            icon = Icons.Default.Deselect.toIconComposable(),
            enabled = enabled && selection.isSomethingSelected,
            onClick = { selection.clear() }
        )


        @Composable
        fun <ID : Comparable<ID>> invertSelection(
            allIds: Collection<ID>,
            selection: SelectionState<ID>,
            enabled: Boolean = true,
        ) = Action.Item(
            text = stringResource(Res.string.menu_invert_selection),
            icon = Icons.Default.SwapHoriz.toIconComposable(),
            enabled = enabled,
            onClick = {
                val currentlySelected = selection.selected.toSet()
                selection.clear()
                selection.set(allIds.filter { it !in currentlySelected })
            }
        )

        @Composable
        fun <ID : Comparable<ID>> delete(
            selection: SelectionState<ID>,
            enabled: Boolean = true,
            onClick: () -> Unit,
        ) = Action.Item(
            text = stringResource(Res.string.menu_delete),
            icon = Icons.Default.Delete.toIconComposable(
                if (enabled && selection.isSomethingSelected)
                    MaterialTheme.colorScheme.error
                else
                    Color.Unspecified
            ),
            enabled = enabled && selection.isSomethingSelected,
            onClick = onClick
        )

        @Composable
        fun <ID : Comparable<ID>> move(
            selection: SelectionState<ID>,
            enabled: Boolean = true,
            onClick: () -> Unit,
        ) = Action.Item(
            text = stringResource(Res.string.menu_move),
            icon = Move.toIconComposable(),
            enabled = enabled && selection.isSomethingSelected,
            onClick = onClick
        )

        @Composable
        fun <ID : Comparable<ID>> copy(
            selection: SelectionState<ID>,
            enabled: Boolean = true,
            onClick: () -> Unit,
        ) = Action.Item(
            text = stringResource(Res.string.menu_copy),
            icon = Icons.Default.CopyAll.toIconComposable(),
            enabled = enabled && selection.isSomethingSelected,
            onClick = onClick
        )

        @Composable
        fun <ID : Comparable<ID>> duplicate(
            selection: SelectionState<ID>,
            enabled: Boolean = true,
            onClick: () -> Unit,
        ) = Action.Item(
            text = stringResource(Res.string.menu_duplicate),
            icon = Icons.Default.ContentCopy.toIconComposable(),
            enabled = enabled && selection.isSomethingSelected,
            onClick = onClick
        )

        @Composable
        fun <ID : Comparable<ID>> cut(
            selection: SelectionState<ID>,
            enabled: Boolean = true,
            onClick: () -> Unit,
        ) = Action.Item(
            text = stringResource(Res.string.menu_cut),
            icon = Icons.Default.ContentCut.toIconComposable(),
            enabled = enabled && selection.isSomethingSelected,
            onClick = onClick
        )
    }
}

@Composable
fun RowScope.MultiSelectionMenu(
    items: List<MultiSelectionMenu.Action> = emptyList(),
    overflowItems: List<MultiSelectionMenu.Action> = emptyList(),
) {
    items.forEach { it.IconButton() }
    if (overflowItems.isNotEmpty()) {
        val showMenu = rememberMenuState()
        MyTooltipBox(
            tooltip = stringResource(Res.string.menu_more)
        ) {
            Box(
                modifier = Modifier,
            ) {
                MyIconButton(
                    icon = Icons.Default.MoreVert.toIconComposable(),
                    onClick = { showMenu.show() }
                )
                PopupMenu(
                    state = showMenu
                ) {
                    overflowItems.forEach { it.MenuItem(this) }
                }
            }
        }
    }
}