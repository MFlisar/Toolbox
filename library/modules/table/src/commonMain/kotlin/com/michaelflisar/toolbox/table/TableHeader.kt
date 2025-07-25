package com.michaelflisar.toolbox.table

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.menu.MenuItem
import com.michaelflisar.toolbox.feature.menu.PopupMenu
import com.michaelflisar.toolbox.feature.menu.PopupMenuScope
import com.michaelflisar.toolbox.feature.menu.rememberMenuState
import com.michaelflisar.toolbox.table.data.TableState
import com.michaelflisar.toolbox.table.ui.TableRow

@Immutable
class TableHeaderColors constructor(
    val containerColor: Color,
    val contentColor: Color
) {
    fun copy(
        containerColor: Color = this.containerColor,
        contentColor: Color = this.contentColor
    ) = TableHeaderColors(
        containerColor.takeOrElse { this.containerColor },
        contentColor.takeOrElse { this.contentColor },
    )
}

@Composable
fun <T> TableHeader(
    state: TableState<T>,
    textResetFilter: String = "Filter zurücksetzen",
    textResetSort: String = "Sortierungen zurücksetzen",
    modifier: Modifier = Modifier,
    colors: TableHeaderColors = TableDefaults.headerColors(),
    additionalMenuContent: @Composable PopupMenuScope.() -> Unit = {}
) {
    val filterIsActive = state.filterIsActive.value

    TableRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LocalStyle.current.paddingDefault)
            .animateContentSize(),
        containerColor = colors.containerColor,
        contentColor = colors.contentColor
    ) {
        Spacer(modifier = modifier.weight(1f))
        Box {
            val menu = rememberMenuState()
            MyIconButton(
                icon = Icons.Default.Menu,
                onClick = {
                    menu.show()
                }
            )
            PopupMenu(
                state = menu
            ) {
                MenuItem(
                    text = { Text(textResetFilter) },
                    icon = Icons.Default.FilterAltOff.toIconComposable(),
                    onClick = {
                        state.clearFilter()
                    },
                    enabled = filterIsActive
                )
                MenuItem(
                    text = { Text(textResetSort) },
                    icon = Icons.Default.Clear.toIconComposable(),
                    onClick = {
                        state.sorts.clear()
                    },
                    enabled = state.sorts.isNotEmpty()
                )
                additionalMenuContent()
            }
        }
    }
}