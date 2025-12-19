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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing

object MyLabeledInformationDefaults {

    @Composable
    fun defaultLabelStyle(): TextStyle {
        return MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
    }

    @Composable
    fun defaultInfoStyle(): TextStyle {
        return MaterialTheme.typography.bodyMedium
    }
}

@Composable
fun MyLabeledInformation(
    label: String,
    info: String,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    backgroundShape: Shape? = null,
    labelStyle: TextStyle = MyLabeledInformationDefaults.defaultLabelStyle(),
    infoStyle: TextStyle = MyLabeledInformationDefaults.defaultInfoStyle(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default)
    ) {
        InfoContent(
            label,
            info,
            labelStyle,
            infoStyle,
            null,
            null,
            color,
            backgroundColor,
            backgroundShape
        )
    }
}

@Composable
fun MyLabeledInformation(
    label: String,
    backgroundColor: Color = Color.Unspecified,
    backgroundShape: Shape? = null,
    labelStyle: TextStyle = MyLabeledInformationDefaults.defaultLabelStyle(),
    modifier: Modifier = Modifier,
    info: @Composable (modifier: Modifier) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default)
    ) {
        InfoContent(label, labelStyle, null, null, backgroundColor, backgroundShape, info)
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
    labelStyle: TextStyle = MyLabeledInformationDefaults.defaultLabelStyle(),
    infoStyle: TextStyle = MyLabeledInformationDefaults.defaultInfoStyle(),
    endContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfoContent(
            label,
            info,
            labelStyle,
            infoStyle,
            labelWidth,
            infoWidth,
            color,
            backgroundColor,
            backgroundShape,
            endContent
        )
    }
}

@Composable
fun MyLabeledInformationHorizontal(
    label: String,
    labelWidth: Dp? = null,
    infoWidth: Dp? = null,
    backgroundColor: Color = Color.Unspecified,
    backgroundShape: Shape? = null,
    labelStyle: TextStyle = MyLabeledInformationDefaults.defaultLabelStyle(),
    modifier: Modifier = Modifier,
    info: @Composable (modifier: Modifier) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfoContent(
            label,
            labelStyle,
            labelWidth,
            infoWidth,
            backgroundColor,
            backgroundShape,
            info
        )
    }
}

@Composable
private fun InfoContent(
    label: String,
    info: String,
    labelStyle: TextStyle,
    infoStyle: TextStyle,
    labelWidth: Dp?,
    infoWidth: Dp?,
    color: Color,
    backgroundColor: Color,
    backgroundShape: Shape?,
    endContent: @Composable () -> Unit = {}
) {
    InfoContent(label, labelStyle, labelWidth, infoWidth, backgroundColor, backgroundShape) {
        MyRow(modifier = it) {
            Text(
                modifier = Modifier.weight(1f),
                text = info,
                style = infoStyle,
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
    labelStyle: TextStyle,
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
        style = labelStyle
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