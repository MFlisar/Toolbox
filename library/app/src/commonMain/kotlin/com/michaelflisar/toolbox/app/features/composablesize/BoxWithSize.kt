package com.michaelflisar.toolbox.app.features.composablesize

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable

@Composable
fun BoxWithSize(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    content: @Composable @UiComposable BoxScope.(sizeClass: SizeClass) -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = contentAlignment,
        propagateMinConstraints = propagateMinConstraints
    ) {
        val heightClass = HeightSizeClass.from(maxHeight)
        val widthClass = WidthSizeClass.from(maxWidth)
        val size = remember(heightClass, widthClass) {
            derivedStateOf {
                SizeClass(
                    widthSizeClass = widthClass,
                    heightSizeClass = heightClass,
                    maxHeight = maxHeight,
                    maxWidth = maxWidth
                )
            }
        }
        content(size.value)
    }
}