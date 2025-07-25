package com.michaelflisar.toolbox.table

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyChip
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.components.MyTooltipBox
import com.michaelflisar.toolbox.extensions.variant
import com.michaelflisar.toolbox.table.data.TableState
import com.michaelflisar.toolbox.table.ui.TableRow
import com.michaelflisar.toolbox.ui.MyScrollableRow

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
    textClearAllFilters: String = "Clear all filters",
    showTextSearch: Boolean = true,
    showColumnFilters: Boolean = true,
    searchWidth: Dp = 256.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: TableSearchBarColors = TableDefaults.searchBarColors()
) {
    TableRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = LocalStyle.current.paddingDefault,
                vertical = LocalStyle.current.paddingDefault
            )
            .animateContentSize(),
        containerColor = colors.containerColor,
        contentColor = colors.contentColor,
        shape = shape,
        borderColor = colors.borderColor,
        horizontalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingDefault)
    ) {
        if (showTextSearch) {
            Text(text = textSearch)
            MyInput(
                //title = textSearch,
                modifier = Modifier.width(searchWidth),
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
            MyScrollableRow(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                overlapScrollbar = true
            ) {
                state.definition.columns.filter { it.filter != null }
                    .forEach {
                        val show = remember { mutableStateOf(false) }
                        Box {
                            val filter = it.filter!!
                            val label by remember(filter.state.value) {
                                derivedStateOf {
                                    if (!filter.isActive()) {
                                        it.header.label
                                    } else {
                                        it.header.label + " " + filter.info()
                                    }
                                }
                            }
                            MyRow {
                                MyChip(
                                    onClick = { show.value = true }
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (filter.isActive()) LocalContentColor.current else LocalContentColor.current.variant()
                                    )
                                    Icon(
                                        modifier = Modifier.size(18.dp),
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                    )
                                    AnimatedVisibility(
                                        visible = filter.isActive()
                                    ) {
                                        CompositionLocalProvider(
                                            LocalMinimumInteractiveComponentSize provides 0.dp
                                        ) {
                                            MyIconButton(
                                                modifier = Modifier.size(18.dp),
                                                icon = Icons.Default.Clear,
                                                onClick = { filter.clear() }
                                            )
                                        }
                                    }
                                }

                            }
                            if (show.value) {
                                val popupWidth = 256.dp
                                Popup(
                                    alignment = Alignment.TopEnd,
                                    onDismissRequest = {
                                        show.value = false
                                    },
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
                                            modifier = Modifier.padding(LocalStyle.current.paddingDefault)
                                                .width(popupWidth),
                                            verticalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingDefault)
                                        ) {
                                            filter.render(compact = true)
                                        }
                                    }

                                }
                            }
                        }
                    }
                Spacer(modifier.width(1.dp).height(LocalMinimumInteractiveComponentSize.current))
            }
            AnimatedVisibility(visible = state.filterIsActive.value) {
                MyTooltipBox(
                    tooltip = textClearAllFilters
                ) {
                    MyIconButton(
                        icon = Icons.Default.Clear,
                        onClick = {
                            state.clearFilter()
                        }
                    )
                }

            }
        }
    }
}