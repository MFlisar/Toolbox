package com.michaelflisar.toolbox.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing

object MyTitleDefaults {

    @Composable
    fun defaultStyle(): TextStyle {
        return MaterialTheme.typography.titleSmall
    }

}

@Composable
fun MyTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = MyTitleDefaults.defaultStyle()
) {
    Text(
        modifier = modifier,
        text = text,
        style = style,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun MyTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = MyTitleDefaults.defaultStyle(),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = style,
                fontWeight = fontWeight,
                color = color,
                textAlign = textAlign
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default)) {
            content()
        }
    }
}

@Composable
fun MyFilledTitle(
    text: String,
    modifier: Modifier = Modifier,
    colorBackgorund: Color = MaterialTheme.colorScheme.primary,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = MyTitleDefaults.defaultStyle(),
) {
    Box(
        modifier = Modifier
            .background(colorBackgorund, MaterialTheme.shapes.small)
            .padding(MaterialTheme.padding.default)
    ) {
        MyTitle(
            modifier = modifier,
            text = text,
            style = style,
            fontWeight = fontWeight,
            color = color,
            textAlign = textAlign
        )
    }
}

@Composable
fun MyFilledTitle(
    text: String,
    modifier: Modifier = Modifier,
    colorBackgorund: Color = MaterialTheme.colorScheme.primary,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    style: TextStyle = MyTitleDefaults.defaultStyle(),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .background(colorBackgorund, MaterialTheme.shapes.small)
            .padding(MaterialTheme.padding.default)
    ) {
        MyTitle(
            text = text,
            modifier = modifier,
            style = style,
            fontWeight = fontWeight,
            color = color,
            textAlign = textAlign,
            content = content
        )
    }
}