package com.michaelflisar.publicutilities.windowsapp.ui.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme

@Composable
fun MyLabeledInformation(
    label: String,
    info: String,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    backgroundShape: Shape? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.ITEM_SPACING)
    ) {
        Content(label, info, null, null, color, backgroundColor, backgroundShape)
    }
}

@Composable
fun MyLabeledInformationHorizontal(
    label: String,
    info: String,
    labelWidth: Dp? = null,
    infoWidth: Dp? = null,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    backgroundShape: Shape? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.ITEM_SPACING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Content(label, info, labelWidth, infoWidth, color, backgroundColor, backgroundShape)
    }
}

@Composable
private fun Content(
    label: String,
    info: String,
    labelWidth: Dp?,
    infoWidth: Dp?,
    color: Color,
    backgroundColor: Color,
    backgroundShape: Shape?,
) {
    val mod = Modifier.then(
        if (labelWidth != null) {
            Modifier.width(labelWidth)
        } else Modifier
    )
    Text(
        modifier = mod,
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold
    )
    val mod2 = Modifier.then(
        if (backgroundColor != Color.Unspecified) {
            Modifier.background(backgroundColor, backgroundShape ?: RectangleShape)
                .padding(4.dp)
        } else Modifier
    ).then(
        if (infoWidth != null) {
            Modifier.width(infoWidth)
        } else Modifier
    )
    Text(
        modifier = mod2,
        text = info,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        //textAlign = TextAlign.End
    )
}