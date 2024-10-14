package com.michaelflisar.toolbox.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.michaelflisar.toolbox.ToolboxDefaults

@Composable
fun MyLoading(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
        ) {
            CircularProgressIndicator()
            MyTitle(text, fontWeight = FontWeight.Normal)
        }
    }
}