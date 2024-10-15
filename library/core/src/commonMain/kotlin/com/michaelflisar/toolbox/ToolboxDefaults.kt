package com.michaelflisar.toolbox

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.classes.Theme

object ToolboxDefaults {

    val TEXT_EMPTY = "Hier herrscht gähnende Leere..."

    val SCROLLBAR_SPACE = 4.dp

    val ITEM_SPACING = 8.dp
    val ITEM_SPACING_MINI = 2.dp
    val CONTENT_PADDING = 16.dp
    val CONTENT_PADDING_SMALL = 8.dp

    val DEFAULT_DIALOG_SIZE = DpSize(800.dp, 600.dp)
    val DEFAULT_DIALOG_SIZE_MEDIUM = DpSize(600.dp, 400.dp)
    val DEFAULT_DIALOG_SIZE_SMALL = DpSize(400.dp, 200.dp)

    @Composable
    fun colorScheme(
        theme: Theme = Theme.System,
        primary: Color = Color(0xff1976d2), // blue
        primaryContainer: Color = Color(0xffc1dcf7),
        secondary: Color = Color(0xff00c853), // green
        secondaryContainer: Color = Color(0xff91f5ba),
        onPrimary: Color = Color.White,
        onSecondary: Color = Color.White
    ) =
        if (theme.isDark()) {
            darkColorScheme(
                primary = primary,
                primaryContainer = primaryContainer,
                secondary = secondary,
                secondaryContainer = secondaryContainer,
                onPrimary = onPrimary,
                onSecondary = onSecondary
            )
        } else {
            lightColorScheme(
                primary = primary,
                primaryContainer = primaryContainer,
                secondary = secondary,
                secondaryContainer = secondaryContainer,
                onPrimary = onPrimary,
                onSecondary = onSecondary
            )
        }

    private val SHAPE_SMALL = RoundedCornerShape(4.dp)
    val SHAPES = Shapes(
        extraSmall = SHAPE_SMALL,
        small = SHAPE_SMALL,
        medium = SHAPE_SMALL,
        large = SHAPE_SMALL,
        extraLarge = SHAPE_SMALL
    )
}