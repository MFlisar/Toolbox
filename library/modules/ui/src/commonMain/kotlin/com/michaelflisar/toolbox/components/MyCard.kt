package com.michaelflisar.toolbox.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.michaelflisar.toolbox.padding

object MyCard {

    enum class StyleType {
        Flat,
        Elevated,
        Outlined
    }

    class Style(
        val type: StyleType,
        val shape: Shape,
        val colors: CardColors,
        val elevation: CardElevation,
        val border: BorderStroke?,
    )
}

object MyCardDefaults {

    @Composable
    fun flatStyle(
        shape: Shape = CardDefaults.shape,
        colors: CardColors = CardDefaults.cardColors(),
        elevation: CardElevation = CardDefaults.cardElevation(),
        border: BorderStroke? = null,
    ): MyCard.Style = MyCard.Style(
        type = MyCard.StyleType.Flat,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    )

    @Composable
    fun elevatedStyle(
        shape: Shape = CardDefaults.elevatedShape,
        colors: CardColors = CardDefaults.elevatedCardColors(),
        elevation: CardElevation = CardDefaults.elevatedCardElevation(),
        border: BorderStroke? = null,
    ): MyCard.Style = MyCard.Style(
        type = MyCard.StyleType.Elevated,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    )

    @Composable
    fun outlinedStyle(
        shape: Shape = CardDefaults.outlinedShape,
        colors: CardColors = CardDefaults.outlinedCardColors(),
        elevation: CardElevation = CardDefaults.outlinedCardElevation(),
        border: BorderStroke? = CardDefaults.outlinedCardBorder(),
    ): MyCard.Style = MyCard.Style(
        type = MyCard.StyleType.Outlined,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    )
}

@Composable
fun MyCard(
    style: MyCard.Style,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    applyContentPadding: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (onClick == null) {
        Card(
            modifier = modifier,
            shape = style.shape,
            colors = style.colors,
            elevation = style.elevation,
            border = style.border,
            content = { Content(content, applyContentPadding) }
        )
    } else {
        Card(
            modifier = modifier,
            onClick = onClick,
            shape = style.shape,
            colors = style.colors,
            elevation = style.elevation,
            border = style.border,
            content = { Content(content, applyContentPadding) }
        )
    }
}

@Composable
fun MyCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable () -> Unit,
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
    content: @Composable () -> Unit,
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
    content: @Composable () -> Unit,
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
    content: @Composable () -> Unit,
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
    content: @Composable () -> Unit,
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
    content: @Composable () -> Unit,
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
private fun Content(content: @Composable () -> Unit, applyContentPadding: Boolean = true) {
    if (!applyContentPadding) {
        content()
        return
    }
    Box(
        modifier = Modifier.padding(MaterialTheme.padding.cardContent)
    ) {
        content()
    }
}
