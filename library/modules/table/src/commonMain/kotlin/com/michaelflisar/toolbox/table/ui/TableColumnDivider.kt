package com.michaelflisar.toolbox.table.ui

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun TableColumnDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground)
}