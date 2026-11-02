package com.michaelflisar.toolbox.ui.adaptive.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.michaelflisar.toolbox.ui.adaptive.classes.StyledLocalMissingException

@Immutable
data class StyledTypography(
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
) {
    companion object {
        internal fun create(
            headline: List<Int>,
            title: List<Int>,
            body: List<Int>,
            label: List<Int>,
            headlineFontWeight: FontWeight = FontWeight.Bold,
            titleFontWeight: FontWeight = FontWeight.SemiBold,
            bodyFontWeight: FontWeight = FontWeight.Normal,
            labelFontWeight: FontWeight = FontWeight.Medium,
        ): StyledTypography {
            val lists = listOf(
                headline, title, body, label
            )
            require(lists.all { it.size == 3 }) { "You must provide size lists with 3 entries!"}
            return StyledTypography(
                headlineLarge = TextStyle(fontSize = headline[0].sp, fontWeight = headlineFontWeight),
                headlineMedium = TextStyle(fontSize = headline[1].sp, fontWeight = headlineFontWeight),
                headlineSmall = TextStyle(fontSize = headline[2].sp, fontWeight = headlineFontWeight),
                titleLarge = TextStyle(fontSize = title[0].sp, fontWeight = titleFontWeight),
                titleMedium = TextStyle(fontSize = title[1].sp, fontWeight = titleFontWeight),
                titleSmall = TextStyle(fontSize = title[2].sp, fontWeight = titleFontWeight),
                bodyLarge = TextStyle(fontSize = body[0].sp, fontWeight = bodyFontWeight),
                bodyMedium = TextStyle(fontSize = body[1].sp, fontWeight = bodyFontWeight),
                bodySmall = TextStyle(fontSize = body[2].sp, fontWeight = bodyFontWeight),
                labelLarge = TextStyle(fontSize = label[0].sp, fontWeight = labelFontWeight),
                labelMedium = TextStyle(fontSize = label[1].sp, fontWeight = labelFontWeight),
                labelSmall = TextStyle(fontSize = label[2].sp, fontWeight = labelFontWeight),
            )
        }
    }
}

internal val LocalStyledTypography =
    staticCompositionLocalOf<StyledTypography> { throw StyledLocalMissingException("LocalStyledTypography") }
