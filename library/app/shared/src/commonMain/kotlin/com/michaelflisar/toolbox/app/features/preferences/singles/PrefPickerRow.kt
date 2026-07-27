package com.michaelflisar.toolbox.app.features.preferences.singles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.michaelflisar.composethemer.picker.internal.SingleChoice

@Composable
internal fun PrefPickerRow(
    data: SingleChoice.ItemData,
    icon: ImageVector?,
    text: String?
) {
    Row(
        modifier = data.modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(it, null)
        }
        Text(
            text = text ?: "",
            color = data.textColor(),
            maxLines = 1
        )
    }
}