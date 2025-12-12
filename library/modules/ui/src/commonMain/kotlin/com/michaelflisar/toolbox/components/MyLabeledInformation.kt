package com.michaelflisar.toolbox.components

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
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing


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
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default)
    ) {
        InfoContent(label, info, null, null, color, backgroundColor, backgroundShape)
    }
}

@Composable
fun MyLabeledInformation(
    label: String,
    backgroundColor: Color = Color.Unspecified,
    backgroundShape: Shape? = null,
    modifier: Modifier = Modifier,
    info: @Composable (modifier: Modifier) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default)
    ) {
        InfoContent(label, null, null, backgroundColor, backgroundShape, info)
    }
}

@Composable
fun MyLabeledInformationHorizontal(
    label: String,
    info: String,
    modifier: Modifier = Modifier,
    labelWidth: Dp? = null,
    infoWidth: Dp? = null,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    backgroundShape: Shape? = null,
   endContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfoContent(label, info, labelWidth, infoWidth, color, backgroundColor, backgroundShape, endContent)
    }
}

@Composable
fun MyLabeledInformationHorizontal(
    label: String,
    labelWidth: Dp? = null,
    infoWidth: Dp? = null,
    backgroundColor: Color = Color.Unspecified,
    backgroundShape: Shape? = null,
    modifier: Modifier = Modifier,
    info: @Composable (modifier: Modifier) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfoContent(label, labelWidth, infoWidth, backgroundColor, backgroundShape, info)
    }
}

@Composable
private fun InfoContent(
    label: String,
    info: String,
    labelWidth: Dp?,
    infoWidth: Dp?,
    color: Color,
    backgroundColor: Color,
    backgroundShape: Shape?,
    endContent: @Composable () -> Unit = {}
) {
    InfoContent(label, labelWidth, infoWidth, backgroundColor, backgroundShape) {
        MyRow(modifier = it) {
            Text(
                modifier = Modifier,
                text = info,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                //textAlign = TextAlign.End
            )
            endContent()
        }
    }
}

@Composable
private fun InfoContent(
    label: String,
    labelWidth: Dp?,
    infoWidth: Dp?,
    backgroundColor: Color,
    backgroundShape: Shape?,
    info: @Composable (modifier: Modifier) -> Unit
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
                .padding(MaterialTheme.padding.small)
        } else Modifier
    ).then(
        if (infoWidth != null) {
            Modifier.width(infoWidth)
        } else Modifier
    )
    info(mod2)
}