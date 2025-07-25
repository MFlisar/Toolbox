package com.michaelflisar.toolbox.table

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.font.FontWeight
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyTitle
import com.michaelflisar.toolbox.table.data.TableState
import com.michaelflisar.toolbox.table.ui.TableRow

@Immutable
class TableFooterColors constructor(
    val containerColor: Color,
    val contentColor: Color
) {
    fun copy(
        containerColor: Color = this.containerColor,
        contentColor: Color = this.contentColor
    ) = TableFooterColors(
        containerColor.takeOrElse { this.containerColor },
        contentColor.takeOrElse { this.contentColor },
    )
}

@Composable
fun <T> TableFooter(
    state: TableState<T>,
    modifier: Modifier = Modifier,
    colors: TableFooterColors = TableDefaults.footerColors()
) {
    val countFiltered by remember(state.filteredRows.size) {
        derivedStateOf {
            state.filteredRows.size
        }
    }
    val countTotal by remember(state.items.size) {
        derivedStateOf {
            state.items.size
        }
    }

    TableRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LocalStyle.current.paddingDefault)
            .animateContentSize(),
        containerColor = colors.containerColor,
        contentColor = colors.contentColor
    ) {
        Spacer(modifier = modifier.weight(1f))
        MyTitle(
            modifier = Modifier.padding(vertical = LocalStyle.current.paddingDefault),
            text = if (countFiltered == countTotal) {
                "$countTotal"
            } else {
                "$countFiltered/$countTotal"
            },
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.titleMedium
        )
    }
}