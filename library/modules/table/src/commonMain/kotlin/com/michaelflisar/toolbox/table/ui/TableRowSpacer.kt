package com.michaelflisar.toolbox.table.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.TableRowSpacer() {
    VerticalDivider(color = MaterialTheme.colorScheme.onBackground)
}

@Composable
fun RowScope.TableRowSpacerInvisible() {
    Spacer(modifier = Modifier.fillMaxHeight().width(1.dp))
}

