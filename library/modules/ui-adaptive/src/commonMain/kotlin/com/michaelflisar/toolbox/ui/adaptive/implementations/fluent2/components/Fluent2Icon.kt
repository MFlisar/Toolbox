package com.michaelflisar.toolbox.ui.adaptive.implementations.fluent2.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.toolbox.ui.adaptive.components.StyledIcon

class Fluent2Icon internal constructor() : StyledIcon.Implementation {

    companion object {

        @Composable
        fun create() = Fluent2Icon()
    }

    @Composable
    override fun Render(
        imageVector: ImageVector,
        modifier: Modifier,
        contentDescription: String?,
        tint: Color,
    ) {
        Image(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            colorFilter = ColorFilter.tint(tint),
        )
    }

    @Composable
    override fun Render(
        painter: Painter,
        modifier: Modifier,
        contentDescription: String?,
        tint: Color,
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier,
            colorFilter = ColorFilter.tint(tint),
        )
    }
}
