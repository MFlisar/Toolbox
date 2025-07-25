package com.michaelflisar.toolbox.table.data

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.Dp

internal class TableData(
    val scrollState: ScrollState,
    val widths: MutableState<List<Dp>>
)