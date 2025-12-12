package com.michaelflisar.toolbox.table

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

import com.michaelflisar.toolbox.components.MyChip
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyFlowRow
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyTooltipBox
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.menu.MenuItem
import com.michaelflisar.toolbox.feature.menu.PopupMenu
import com.michaelflisar.toolbox.feature.menu.rememberMenuState
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing
import com.michaelflisar.toolbox.table.data.TableState
import com.michaelflisar.toolbox.table.definitions.Column
import com.michaelflisar.toolbox.table.definitions.Filter
import com.michaelflisar.toolbox.table.ui.TableRow

@Immutable
class TableSearchBarColors constructor(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color
) {
    fun copy(
        containerColor: Color = this.containerColor,
        contentColor: Color = this.contentColor,
        borderColor: Color = this.borderColor
    ) = TableSearchBarColors(
        containerColor.takeOrElse { this.containerColor },
        contentColor.takeOrElse { this.contentColor },
        borderColor.takeOrElse { this.borderColor }
    )
}

@Composable
fun <T> TableSearchBar(
    state: TableState<T>,
    modifier: Modifier = Modifier,
    textSearch: String = "Search",
    textResetFilter: String = "Reset filter",
    showTextSearch: Boolean = true,
    showColumnFilters: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: TableSearchBarColors = TableDefaults.searchBarColors()
) {
    TableRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.padding.default,
                vertical = MaterialTheme.padding.default
            )
            .animateContentSize(),
        containerColor = colors.containerColor,
        contentColor = colors.contentColor,
        shape = shape,
        borderColor = colors.borderColor,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default)
    ) {
        MyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (showTextSearch) {
                MyInput(
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    placeholder = { Text(textSearch) },
                    modifier = Modifier.fillMaxWidth(),
                    value = state.filter,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LocalContentColor.current,
                        unfocusedTextColor = LocalContentColor.current,
                        cursorColor = LocalContentColor.current,
                        focusedPlaceholderColor = LocalContentColor.current,
                        unfocusedPlaceholderColor = LocalContentColor.current,
                        focusedLabelColor = LocalContentColor.current,
                        unfocusedLabelColor = LocalContentColor.current,
                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTrailingIconColor = LocalContentColor.current,
                        unfocusedTrailingIconColor = LocalContentColor.current,
                        focusedLeadingIconColor = LocalContentColor.current,
                        unfocusedLeadingIconColor = LocalContentColor.current,
                    )
                )
            }
            if (showColumnFilters) {
                MyFlowRow(
                    verticalAlignment = Alignment.CenterVertically,
                    verticalItemAlignment = Alignment.CenterVertically,
                ) {
                    Crossfade(
                        targetState = state.filterState.value.isColumnFilterActive
                    ) { active ->
                        if (active) {
                            MyTooltipBox(
                                tooltip = textResetFilter
                            ) {
                                MyIconButton(
                                    icon = Icons.Default.FilterAltOff.toIconComposable(),
                                    onClick = {
                                        state.clearFilter(textFilter = false, columnFilters = true)
                                    }
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.FilterAlt,
                                null,
                                modifier = Modifier.minimumInteractiveComponentSize()
                            )
                        }
                    }

                    // remembed selected filters IN ORDER
                    val currentFilter = state.definition.columns.filter { it.filter != null && it.filter.isActive() }
                    val filter = remember { mutableStateOf(currentFilter) }
                    LaunchedEffect(currentFilter) {
                        val add = currentFilter.filter { it !in filter.value }
                        val remove = filter.value.filter { it !in currentFilter }
                        filter.value = (filter.value - remove) + add
                    }

                    filter.value.forEach { column ->
                        MyChip(
                            //title = label,
                            endIcon = {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    //modifier = Modifier.size(12.dp)
                                )
                            },
                            onClick = {
                                column.filter!!.clear()
                            }
                        ) {
                            Row {
                                Text(column.header.label, fontWeight = FontWeight.SemiBold)
                                Text(" ")
                                Text(column.filter!!.info())
                            }
                        }
                    }
                    CompositionLocalProvider(
                        LocalMinimumInteractiveComponentSize provides 0.dp
                    ) {
                        val menu = rememberMenuState()
                        val subMenu = remember { mutableStateOf<Column<*, *>?>(null) }
                        MyChip(
                            onClick = { menu.show() }
                        ) {
                            Box {
                                Icon(Icons.Default.Add, null)

                                // Main Menu
                                PopupMenu(
                                    state = menu,
                                    //setup = rememberMenuSetup(autoDismiss = false)
                                ) {
                                    state.definition.columns.filter { it.filter != null }
                                        .forEach { column ->
                                            MenuItem(
                                                text = {
                                                    Text(column.header.label)
                                                },
                                                onClick = {
                                                    subMenu.value = column
                                                },
                                                enabled = column.filter?.isActive() == false
                                            )
                                        }
                                }


                                // SubMenu
                                subMenu.value?.let { column ->
                                    val popupWidth = 256.dp
                                    Popup(
                                        alignment = Alignment.TopEnd,
                                        onDismissRequest = { subMenu.value = null },
                                        properties = PopupProperties(focusable = true),
                                        offset = IntOffset(
                                            with(LocalDensity.current) { (popupWidth / 2 - 12.dp).roundToPx() },
                                            with(LocalDensity.current) { 32.dp.roundToPx() }
                                        )
                                    ) {
                                        Surface(
                                            shape = MaterialTheme.shapes.medium,
                                            tonalElevation = 8.dp,
                                            shadowElevation = 8.dp
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(MaterialTheme.padding.default)
                                                    .width(popupWidth),
                                                verticalArrangement = Arrangement.spacedBy(
                                                    MaterialTheme.spacing.default
                                                )
                                            ) {
                                                column.filter!!.Render(
                                                    style = Filter.Style.ChipPopup(column.header.label)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}