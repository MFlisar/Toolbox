package com.michaelflisar.toolbox.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.ToolboxDefaults

@Composable
fun MyVerticalSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.width(ToolboxDefaults.ITEM_SPACING))
}