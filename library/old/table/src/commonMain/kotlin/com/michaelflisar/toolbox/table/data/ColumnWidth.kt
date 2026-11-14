package com.michaelflisar.toolbox.table.data

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

sealed class ColumnWidth {

    //abstract fun modifier(scope: RowScope): Modifier

    data class Fixed(val width: Dp) : ColumnWidth() {
        //override fun modifier(scope: RowScope): Modifier = Modifier.width(width)
    }

    data class Weight(val weight: Float = 1f, val minWidth: Dp = 0.dp) : ColumnWidth() {
        //override fun modifier(scope: RowScope): Modifier = with (scope) { Modifier.requiredSizeIn(minWidth = minWidth) .weight(weight) }
    }

    class Auto() : ColumnWidth() {
        //override fun modifier(scope: RowScope): Modifier = Modifier
    }
}