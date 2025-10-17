package com.michaelflisar.toolbox.table

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.LocalTheme
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.components.MyTooltipBox
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
    title: @Composable () -> Unit = {},
    textResetSort: String = "Reset sort",
    modifier: Modifier = Modifier,
    colors: TableHeaderColors = TableDefaults.headerColors(),
    additionalMenuContent: @Composable RowScope.() -> Unit = {}
) {
    TableRow(
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .padding(horizontal = LocalTheme.current.padding.default)
            .animateContentSize(),
        containerColor = colors.containerColor,
        contentColor = colors.contentColor
    ) {
        title()
        Spacer(modifier = modifier.weight(1f))
        AnimatedVisibility(
            visible = state.sorts.isNotEmpty()
        ) {
            MyTooltipBox(
                tooltip = textResetSort
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    MyIconButton(
                        icon = Icons.Default.Sort,
                        onClick = {
                            state.sorts.clear()
                        }
                    )
                    val color = LocalContentColor.current
                    Canvas(
                        modifier = Modifier
                            .size(24.dp)
                        //.padding(8.dp)
                    ) {
                        drawLine(
                            color = color,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height),
                            strokeWidth = 2f
                        )
                    }
                }
            }
        }
        additionalMenuContent()
    }
}