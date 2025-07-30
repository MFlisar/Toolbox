package com.michaelflisar.toolbox.table

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import com.michaelflisar.toolbox.classes.LocalStyle
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
    textResetFilter: String = "Reset filter",
    textResetSort: String = "Reset sort",
    modifier: Modifier = Modifier,
    colors: TableHeaderColors = TableDefaults.headerColors(),
    additionalMenuContent: @Composable RowScope.() -> Unit = {}
) {
    val filterIsActive = state.filterIsActive.value

    TableRow(
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .padding(horizontal = LocalStyle.current.paddingDefault)
            .animateContentSize(),
        containerColor = colors.containerColor,
        contentColor = colors.contentColor
    ) {
        title()
        Spacer(modifier = modifier.weight(1f))
        AnimatedVisibility(
            visible = filterIsActive
        ) {
            MyTooltipBox(
                tooltip = textResetFilter
            ) {
                MyIconButton(
                    icon = Icons.Default.FilterAltOff,
                    onClick = {
                        state.clearFilter()
                    }
                )
            }
        }
        AnimatedVisibility(
            visible = state.sorts.isNotEmpty()
        ) {
            MyTooltipBox(
                tooltip = textResetSort
            ) {
                MyIconButton(
                    icon = Icons.Default.Clear,
                    onClick = {
                        state.sorts.clear()
                    }
                )
            }
        }
        additionalMenuContent()
    }
}