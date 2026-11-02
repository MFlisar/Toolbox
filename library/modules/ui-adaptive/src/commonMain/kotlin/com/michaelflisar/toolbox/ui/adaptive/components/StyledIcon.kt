package com.michaelflisar.toolbox.ui.adaptive.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme
import com.michaelflisar.toolbox.ui.adaptive.classes.IconComposable
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledContentColor
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledRegistry
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledKey

object StyledIcon {

    val Key = StyledKey<Implementation>("StyledIcon")

    interface Implementation {

        @Composable
        fun Render(
            imageVector: ImageVector,
            modifier: Modifier,
            contentDescription: String?,
            tint: Color,
        )

        @Composable
        fun Render(
            painter: Painter,
            modifier: Modifier,
            contentDescription: String?,
            tint: Color,
        )
    }
}

@Composable
fun StyledIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp? = StyledTheme.sizes.iconSize,
    tint: Color = LocalStyledContentColor.current,
) {
    LocalStyledRegistry.current[StyledIcon.Key].Render(
        imageVector = imageVector,
        modifier = modifier.then(
            if (size != null) Modifier.size(size) else Modifier
        ),
        contentDescription = contentDescription,
        tint = tint,
    )
}

@Composable
fun StyledIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp? = StyledTheme.sizes.iconSize,
    tint: Color = LocalStyledContentColor.current,
) {
    LocalStyledRegistry.current[StyledIcon.Key].Render(
        painter = painter,
        modifier = modifier.then(
            if (size != null) Modifier.size(size) else Modifier
        ),
        contentDescription = contentDescription,
        tint = tint,
    )
}

@Composable
fun StyledIcon(
    icon: IconComposable,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp? = StyledTheme.sizes.iconSize,
    tint: Color = LocalStyledContentColor.current,
) {
    IconComposable(
        icon = icon,
        modifier = modifier.then(
            if (size != null) Modifier.size(size) else Modifier
        ),
        contentDescription = contentDescription,
        tint = tint,
    )
}