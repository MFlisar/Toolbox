package com.michaelflisar.toolbox.app.features.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme

@Composable
fun ColorBox(
    color: Color,
    color2: Color? = null,
    size: Dp = StyledTheme.sizes.iconSize,
    shape: Shape = StyledTheme.shapes.small,
) {
    if (color2 == null) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(color)
        )
        return
    }
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color)
            )
            Spacer(modifier = Modifier.width(1.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color2)
            )
        }
    }
}