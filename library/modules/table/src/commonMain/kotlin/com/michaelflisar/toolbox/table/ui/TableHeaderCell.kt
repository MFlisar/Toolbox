package com.michaelflisar.toolbox.table.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.components.MyTooltipBox
import com.michaelflisar.toolbox.extensions.disabled
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing
import com.michaelflisar.toolbox.table.data.TableSort
import com.michaelflisar.toolbox.table.definitions.Column
import com.michaelflisar.toolbox.table.definitions.Filter
import com.michaelflisar.toolbox.table.definitions.Header

@Composable
internal fun RowScope.TableHeaderCell(
    index: Int,
    column: Column<*, *>,
    width: Dp,
    sorts: SnapshotStateList<TableSort>,
    showFilterOnHeaderClick: Boolean,
) {
    val sort = remember(sorts.toList()) {
        derivedStateOf {
            sorts.find { it.columnIndex == index }
        }
    }
    val popup = remember { mutableStateOf(false) }

    val canBeSortedOnClick by remember(column.sortable) {
        derivedStateOf { column.sortable }
    }
    val canBeFilteredOnClick by remember(showFilterOnHeaderClick, column.filter != null) {
        derivedStateOf { showFilterOnHeaderClick && column.filter != null }
    }

    Box(
        modifier = Modifier.width(width)
            //column.width.modifier(this)
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
                .clickable(
                    enabled = canBeSortedOnClick || canBeFilteredOnClick
                ) {
                    if (showFilterOnHeaderClick) {
                        popup.value = true
                    } else {
                        // toggle sort...
                        if (sort.value == null) {
                            sorts.add(TableSort(index, TableSort.Type.Asc))
                        } else if (sort.value?.type == TableSort.Type.Asc) {
                            sort.value?.let { sorts.remove(it) }
                            sorts.add(TableSort(index, TableSort.Type.Desc))
                        } else if (sort.value?.type == TableSort.Type.Desc) {
                            sort.value?.let { sorts.remove(it) }
                        } else {
                            // should never happen
                        }
                        //sort.value?.let { sorts.remove(it) }
                        //sorts.add(TableSort(index, TableSort.Type.Desc))
                    }

                }
                .padding(column.header.cellPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val containerModifier = Modifier.padding(horizontal = 4.dp).weight(1f)
            val itemModifier = Modifier.fillMaxWidth()
            Row(
                modifier = Modifier.weight(1f),
            ) {
                HeaderTooltipContainer(column.header, containerModifier) {
                    when (val header = column.header) {
                        is Header.Icon -> {
                            Row(
                                modifier = itemModifier,
                                horizontalArrangement = Arrangement.aligned(header.align)
                            ) {
                                header.icon.invoke()
                            }
                        }

                        is Header.Text -> {
                            if (header.icon != null) {
                                Row(
                                    modifier = itemModifier,
                                    horizontalArrangement = Arrangement.spacedBy(
                                        4.dp,
                                        Alignment.CenterHorizontally
                                    )
                                ) {
                                    header.icon.invoke()
                                    if (header.label.isNotEmpty()) {
                                        HeaderItemText(Modifier.weight(1f), header)
                                    }
                                }
                            } else {
                                HeaderItemText(itemModifier, header)
                            }
                        }
                    }
                }
            }
            HeaderMenuIcon(index, column.sortable, column.filter?.takeIf { showFilterOnHeaderClick }, sorts, sort)
        }
        if (showFilterOnHeaderClick && popup.value) {
            HeaderPopup(popup, index, column.sortable, sorts, column.filter, sort)
        }
    }
}

@Composable
private fun RowScope.HeaderTooltipContainer(
    header: Header,
    modifier: Modifier,
    content: @Composable (() -> Unit),
) {
    MyTooltipBox(
        modifier = modifier,
        tooltip = {
            if (header.description.isNotEmpty()) {
                Column {
                    Text(header.label, fontWeight = FontWeight.Bold)
                    Text(header.description)
                }
            } else {
                Text(header.label)
            }
        },
        enabled = header.label.isNotEmpty() || header.description.isNotEmpty()
    ) {
        content()
    }
}

@Composable
private fun RowScope.HeaderItemText(
    modifier: Modifier,
    header: Header.Text,
) {
    Text(
        modifier = modifier,
        text = header.label,
        textAlign = header.textAlign,
        style = MaterialTheme.typography.titleSmall,
        maxLines = header.maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun HeaderMenuIcon(
    index: Int,
    sortable: Boolean,
    filter: Filter<*, *>?,
    sorts: SnapshotStateList<TableSort>,
    sort: State<TableSort?>,
) {
    val s = sort.value

    var largeIcon: ImageVector? = null
    var smallIcon: ImageVector? = null

    if (sortable && filter != null) {
        // Spalte ist sortierbar und filterbar
        if (s != null && filter.isActive()) {
            largeIcon = Icons.Default.FilterAlt
            smallIcon = when (s.type) {
                TableSort.Type.Asc -> Icons.Default.ArrowUpward
                TableSort.Type.Desc -> Icons.Default.ArrowDownward
            }
        } else if (s != null) {
            largeIcon = when (s.type) {
                TableSort.Type.Asc -> Icons.Default.ArrowUpward
                TableSort.Type.Desc -> Icons.Default.ArrowDownward
            }
        } else if (filter.isActive()) {
            largeIcon = Icons.Default.FilterAlt
        }
    } else if (sortable) {
        if (s != null) {
            largeIcon = when (s.type) {
                TableSort.Type.Asc -> Icons.Default.ArrowUpward
                TableSort.Type.Desc -> Icons.Default.ArrowDownward
            }
        }
    } else if (filter != null) {
        if (filter.isActive()) {
            largeIcon = Icons.Default.FilterAlt
        }
    }

    if (largeIcon != null && smallIcon != null) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .width(32.dp)
        ) {
            BigIcon(
                imageVector = largeIcon,
                withSmallIcon = true
            )
            SmallIcon(smallIcon)
        }
    } else if (largeIcon != null) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .width(32.dp)
        ) {
            BigIcon(largeIcon, false)
        }
    } else {
        Box(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun BoxScope.BigIcon(
    imageVector: ImageVector,
    withSmallIcon: Boolean,
) {
    Icon(
        modifier = Modifier
            //.padding(start = 8.dp)
            .size(24.dp)
            .align(if (withSmallIcon) Alignment.CenterEnd else Alignment.Center),
        tint = MaterialTheme.colorScheme.primary,
        imageVector = imageVector,
        contentDescription = null
    )
}

@Composable
private fun BoxScope.SmallIcon(
    imageVector: ImageVector,
) {
    Icon(
        modifier = Modifier
            .size(16.dp)
            .align(Alignment.BottomStart),
        tint = MaterialTheme.colorScheme.primary,
        imageVector = imageVector,
        contentDescription = null
    )
}

@Composable
private fun HeaderPopup(
    show: MutableState<Boolean>,
    index: Int,
    sortable: Boolean,
    sorts: SnapshotStateList<TableSort>,
    filter: Filter<*, *>?,
    sort: State<TableSort?>,
) {
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
                    modifier = Modifier.padding(MaterialTheme.padding.default)
                        .width(popupWidth),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default)
                ) {
                    CompositionLocalProvider(
                        LocalMinimumInteractiveComponentSize provides 0.dp
                    ) {

                        val iconPadding = MaterialTheme.padding.small
                        val iconSize = 24.dp
                        val iconModifier = Modifier.size(iconSize + iconPadding)
                        val contentInsetStart =
                            iconSize + iconPadding + MaterialTheme.spacing.default

                        if (sortable) {
                            Row(
                                modifier = Modifier.heightIn(min = iconSize + iconPadding),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Sort,
                                    null
                                )
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = "Sortierung",
                                    fontWeight = FontWeight.Bold
                                )
                                MyTooltipBox(
                                    tooltip = "Sort Descending",
                                ) {
                                    MyIconButton(
                                        modifier = iconModifier,
                                        iconPaddingValues = PaddingValues(iconPadding),
                                        icon = Icons.Default.ArrowDownward.toIconComposable(if (sort.value?.type == TableSort.Type.Desc) MaterialTheme.colorScheme.primary else LocalContentColor.current)
                                    ) {
                                        sort.value?.let { sorts.remove(it) }
                                        sorts.add(TableSort(index, TableSort.Type.Desc))
                                    }
                                }
                                MyTooltipBox(
                                    tooltip = "Sort Ascending",
                                ) {
                                    MyIconButton(
                                        modifier = iconModifier,
                                        iconPaddingValues = PaddingValues(iconPadding),
                                        icon = Icons.Default.ArrowUpward.toIconComposable( if (sort.value?.type == TableSort.Type.Asc) MaterialTheme.colorScheme.primary else LocalContentColor.current)
                                    ) {
                                        sort.value?.let { sorts.remove(it) }
                                        sorts.add(TableSort(index, TableSort.Type.Asc))
                                    }
                                }
                                MyIconButton(
                                    enabled = sort.value != null,
                                    modifier = iconModifier,
                                    iconPaddingValues = PaddingValues(iconPadding),
                                    icon = Icons.Default.Clear.toIconComposable(if (sort.value != null) LocalContentColor.current else LocalContentColor.current.disabled())
                                ) {
                                    sort.value?.let { sorts.remove(it) }
                                }
                            }
                        }
                    }

                    filter?.Render(style = Filter.Style.HeaderPopup())
                }
            }

        }
    }
}