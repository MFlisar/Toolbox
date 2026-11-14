package com.michaelflisar.toolbox.ui.adaptive.tokens

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.ui.adaptive.classes.StyledLocalMissingException

@Immutable
data class StyledShapes(
    val extraSmall: CornerBasedShape,
    val small: CornerBasedShape,
    val medium: CornerBasedShape,
    val large: CornerBasedShape,
    val extraLarge: CornerBasedShape,
) {
    companion object {
        internal fun create(
            sizes: List<Dp>,
        ): StyledShapes {
            require(sizes.size == 5) { "You must provide a sizes list with 5 entries!"}
            return StyledShapes(
                RoundedCornerShape(sizes[0]),
                RoundedCornerShape(sizes[1]),
                RoundedCornerShape(sizes[2]),
                RoundedCornerShape(sizes[3]),
                RoundedCornerShape(sizes[4]),
            )
        }
    }
}

internal val LocalStyledShapes =
    staticCompositionLocalOf<StyledShapes> { throw StyledLocalMissingException("LocalStyledShapes") }
