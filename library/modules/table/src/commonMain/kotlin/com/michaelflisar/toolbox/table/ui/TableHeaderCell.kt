package com.michaelflisar.toolbox.table.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.components.MyTooltipBox
import com.michaelflisar.toolbox.disabled
import com.michaelflisar.toolbox.table.data.Sort
import com.michaelflisar.toolbox.table.definitions.Column
import com.michaelflisar.toolbox.table.definitions.Filter
import com.michaelflisar.toolbox.table.definitions.Header

@Composable
internal fun RowScope.TableHeaderCell(
    index: Int,
    column: Column<*, *>,
    sorts: SnapshotStateList<Sort>
) {
    val sort = remember(sorts.toList()) {
        derivedStateOf {
            sorts.find { it.columnIndex == index }
        }
    }
    Row(
        modifier = column
            .modifier(this)
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
        HeaderMenuIcon(index, column.filter, sorts, sort)
    }
}

@Composable
private fun RowScope.HeaderTooltipContainer(
    header: Header,
    modifier: Modifier,
    content: @Composable (() -> Unit)
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
        }
    ) {
        content()
    }
}

@Composable
private fun RowScope.HeaderItemText(
    modifier: Modifier,
    header: Header.Text
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
    filter: Filter<*, *>?,
    sorts: SnapshotStateList<Sort>,
    sort: State<Sort?>
) {
    val popup = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                popup.value = true
            }
            .padding(4.dp)
            .width(32.dp)
    ) {
        val s = sort.value
        // big filter icon
        Icon(
            modifier = Modifier
                //.padding(start = 8.dp)
                .size(24.dp)
                .align(if (s != null) Alignment.CenterEnd else Alignment.Center),
            tint = if (filter?.isActive() != true) LocalContentColor.current.disabled() else MaterialTheme.colorScheme.primary,
            imageVector = Icons.Default.FilterAlt,
            contentDescription = null
        )
        // small sort "overlay"
        if (s != null) {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomStart),
                tint = MaterialTheme.colorScheme.primary,
                imageVector = when (s.type) {
                    Sort.Type.Asc -> Icons.Default.ArrowUpward
                    Sort.Type.Desc -> Icons.Default.ArrowDownward
                },
                contentDescription = null
            )
        }
        if (popup.value) {
            HeaderMenuIconPopup(popup, index, sorts, filter, sort)
        }
    }
}

@Composable
private fun HeaderMenuIconPopup(
    show: MutableState<Boolean>,
    index: Int,
    sorts: SnapshotStateList<Sort>,
    filter: Filter<*, *>?,
    sort: State<Sort?>
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
                    modifier = Modifier.padding(LocalStyle.current.paddingDefault)
                        .width(popupWidth),
                    verticalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingDefault)
                ) {
                    CompositionLocalProvider(
                        LocalMinimumInteractiveComponentSize provides 0.dp
                    ) {

                        val iconPadding = LocalStyle.current.paddingSmall
                        val iconSize = 24.dp
                        val iconModifier = Modifier.size(iconSize + iconPadding)
                        val contentInsetStart =
                            iconSize + iconPadding + LocalStyle.current.spacingDefault

                        Row(
                            modifier = Modifier.heightIn(min = iconSize + iconPadding),
                            horizontalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingDefault),
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
                            MyIconButton(
                                modifier = iconModifier,
                                iconPaddingValues = PaddingValues(iconPadding),
                                icon = Icons.Default.ArrowUpward,
                                tint = if (sort.value?.type == Sort.Type.Asc) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            ) {
                                sort.value?.let { sorts.remove(it) }
                                sorts.add(Sort(index, Sort.Type.Asc))
                            }
                            MyIconButton(
                                modifier = iconModifier,
                                iconPaddingValues = PaddingValues(iconPadding),
                                icon = Icons.Default.ArrowDownward,
                                tint = if (sort.value?.type == Sort.Type.Desc) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            ) {
                                sort.value?.let { sorts.remove(it) }
                                sorts.add(Sort(index, Sort.Type.Desc))
                            }
                            MyIconButton(
                                enabled = sort.value != null,
                                modifier = iconModifier,
                                iconPaddingValues = PaddingValues(iconPadding),
                                icon = Icons.Default.Clear,
                                tint = if (sort.value != null) LocalContentColor.current else LocalContentColor.current.disabled()
                            ) {
                                sort.value?.let { sorts.remove(it) }
                            }
                        }
                    }

                    filter?.render()
                }
            }

        }
    }
}