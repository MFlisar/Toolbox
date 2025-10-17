package com.michaelflisar.toolbox.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.michaelflisar.toolbox.LocalTheme

@Composable
fun MyCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = { Content(content) }
    )
}

@Composable
fun MyCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        interactionSource = interactionSource,
        content = { Content(content) }
    )
}

@Composable
fun MyElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.elevatedShape,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        content = { Content(content) }
    )
}

@Composable
fun MyElevatedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardDefaults.elevatedShape,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        interactionSource = interactionSource,
        content = { Content(content) }
    )
}

@Composable
fun MyOutlinedCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.outlinedShape,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
    border: BorderStroke = CardDefaults.outlinedCardBorder(),
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = modifier,
        shape = shape,
        colors = colors,
        border = border,
        elevation = elevation,
        content = { Content(content) }
    )
}

@Composable
fun MyOutlinedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardDefaults.outlinedShape,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
    border: BorderStroke = CardDefaults.outlinedCardBorder(enabled),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        interactionSource = interactionSource,
        content = { Content(content) }
    )
}

@Composable
private fun Content(content: @Composable ColumnScope.() -> Unit) {
    MyColumn(
        modifier = Modifier.padding(LocalTheme.current.padding.cardContent)
    ) {
        content()
    }
}
