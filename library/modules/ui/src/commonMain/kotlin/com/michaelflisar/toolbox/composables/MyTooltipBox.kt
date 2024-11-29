package com.michaelflisar.toolbox.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTooltipBox(
    tooltip: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (tooltip.isNotEmpty()) {
        TooltipBox(
            modifier = modifier,
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text(tooltip)
                }
            },
            state = rememberTooltipState()
        ) {
            content()
        }
    } else {
        Box(modifier = modifier) {
            content()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTooltipBox(
    tooltip: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                tooltip()
            }
        },
        state = rememberTooltipState()
    ) {
        content()
    }
}