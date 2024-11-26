package com.michaelflisar.toolbox.table.definitions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class Header {

    abstract val label: String
    abstract val description: String
    abstract val cellPadding: Dp

    data class Text(
        override val label: String,
        override val description: String = "",
        val icon: @Composable (() -> Unit)? = null,
        override val cellPadding: Dp = 4.dp,
        val textAlign: TextAlign = TextAlign.Unspecified,
        val maxLines: Int = 1
    ) : Header()

    data class Icon(
        override val label: String,
        val icon: @Composable (() -> Unit),
        override val description: String = "",
        override val cellPadding: Dp = 4.dp,
        val align: Alignment.Horizontal = Alignment.Start
    ) : Header()
}