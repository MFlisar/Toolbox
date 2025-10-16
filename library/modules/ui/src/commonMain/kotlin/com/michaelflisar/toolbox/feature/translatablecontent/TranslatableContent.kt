package com.michaelflisar.toolbox.feature.translatablecontent

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout

object TranslatableContent {
    enum class Side {
        Left,
        Right,
        Top,
        Bottom
    }
}

@LayoutScopeMarker
@Immutable
interface TranslatableSideScope {
    @Stable
    fun Modifier.stretch(stretch: Boolean): Modifier
}

internal class TranslatableSideScopeInstance(
    val side: TranslatableContent.Side
) : TranslatableSideScope {
    @Stable
    override fun Modifier.stretch(stretch: Boolean): Modifier {
        return if (stretch) {
            when (side) {
                TranslatableContent.Side.Left, TranslatableContent.Side.Right -> this.fillMaxHeight()
                TranslatableContent.Side.Top, TranslatableContent.Side.Bottom -> this.fillMaxWidth()
            }
        } else {
            this
        }
    }
}

@Composable
fun TranslatableContent(
    modifier: Modifier = Modifier,
    side: TranslatableContent.Side = TranslatableContent.Side.Left,
    translation: Float = 0f,
    content: @Composable TranslatableSideScope.() -> Unit
) {
    Layout(
        modifier = modifier.clipToBounds(),
        content = {
            TranslatableSideScopeInstance(side).content()
        }
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints)

        val width = when (side) {
            TranslatableContent.Side.Left,
            TranslatableContent.Side.Right -> (placeable.width * translation).toInt()
                .coerceAtLeast(0)

            TranslatableContent.Side.Top,
            TranslatableContent.Side.Bottom -> placeable.width
        }
        val height = when (side) {
            TranslatableContent.Side.Left,
            TranslatableContent.Side.Right -> placeable.height

            TranslatableContent.Side.Top,
            TranslatableContent.Side.Bottom -> (placeable.height * translation).toInt()
                .coerceAtLeast(0)
        }

        val (x, y) = when (side) {
            TranslatableContent.Side.Left -> width - placeable.width to 0
            TranslatableContent.Side.Right -> 0 to 0
            TranslatableContent.Side.Top -> 0 to height - placeable.height
            TranslatableContent.Side.Bottom -> 0 to 0
        }

        layout(width, height) {
            placeable.placeRelative(x, y)
        }
    }
}