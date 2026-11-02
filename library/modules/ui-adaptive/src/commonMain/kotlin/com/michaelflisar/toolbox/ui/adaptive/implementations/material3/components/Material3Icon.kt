package com.michaelflisar.toolbox.ui.adaptive.implementations.material3.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.toolbox.ui.adaptive.components.StyledIcon

class Material3Icon internal constructor() : StyledIcon.Implementation {

    companion object {

        @Composable
        fun create() = Material3Icon()
    }

    @Composable
    override fun Render(
        imageVector: ImageVector,
        modifier: Modifier,
        contentDescription: String?,
        tint: Color,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint,
        )
    }

    @Composable
    override fun Render(
        painter: Painter,
        modifier: Modifier,
        contentDescription: String?,
        tint: Color,
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint,
        )
    }
}
