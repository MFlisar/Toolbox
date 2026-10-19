package com.michaelflisar.toolbox.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --------------
// MaterialTheme Extensions
// --------------

val MaterialTheme.padding: Padding
    @Composable
    get() = LocalTheme.current.padding

val MaterialTheme.spacing: Spacing
    @Composable
    get() = LocalTheme.current.spacing

val MaterialTheme.scrollbar: Scrollbar
    @Composable
    get() = LocalTheme.current.scrollbar

val MaterialTheme.icon : Icon
    @Composable
    get() = LocalTheme.current.icon

val MaterialTheme.minimumInteractiveComponentSize: Dp
    @Composable
    get() = LocalTheme.current.minimumInteractiveComponentSize

// --------------
// Klassen
// --------------

/**
 * @param mini mini padding value
 * @param small small padding value
 * @param default default padding value
 * @param content padding value for content areas
 * @param cardContent padding value between cards and their content
 */
@Immutable
data class Padding(
    val mini: Dp = 2.dp,
    val small: Dp = 4.dp,
    val default: Dp = 8.dp,
    val content: Dp = 16.dp,
    val cardContent: Dp = 8.dp,
)

@Immutable
data class Spacing(
    val mini: Dp = 2.dp,
    val small: Dp = 4.dp,
    val default: Dp = 8.dp,
    val large: Dp = 16.dp,
)

@Immutable
data class Scrollbar(
    val size: Dp = 8.dp,
    val spacing: Dp = 4.dp,
) {
    val paddingForScrollbar = size + spacing
}

@Immutable
data class Icon(
    val size: Dp
)

internal enum class MyThemeType {
    Material3,
    Custom
}

@Immutable
class MyTheme internal constructor(
    internal val type: MyThemeType,
    val padding: Padding,
    val spacing: Spacing,
    val scrollbar: Scrollbar,
    val icon: Icon,
    val minimumInteractiveComponentSize: Dp,
    val shapes: Shapes,
    val typography: Typography,
) {
    companion object {

        internal val defaultIconSize = 24.dp
        internal val compactIconSize = 20.dp

        internal val desktopCompactTypography: Typography
            @Composable get() {
                val t = MaterialTheme.typography
                return Typography(
                    displayLarge = t.displayLarge.copy(
                        fontSize = 24.sp,
                        lineHeight = 28.sp,
                    ),
                    displayMedium = t.displayMedium.copy(
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                    ),
                    displaySmall = t.displaySmall.copy(
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                    ),

                    headlineLarge = t.headlineLarge.copy(
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                    ),
                    headlineMedium = t.headlineMedium.copy(
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                    ),
                    headlineSmall = t.headlineSmall.copy(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                    ),

                    titleLarge = t.titleLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                    ),
                    titleMedium = t.titleMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                    ),
                    titleSmall = t.titleSmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    ),

                    bodyLarge = t.bodyLarge.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    ),
                    bodyMedium = t.bodyMedium.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    ),
                    bodySmall = t.bodySmall.copy(
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                    ),

                    labelLarge = t.labelLarge.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    ),
                    labelMedium = t.labelMedium.copy(
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                    ),
                    labelSmall = t.labelSmall.copy(
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                    ),
                )
            }

        internal val desktopMinimumInteractiveComponentSize = 32.dp

        @Composable
        fun default(
            shapes: Shapes = MaterialTheme.shapes,
            typography: Typography = MaterialTheme.typography,
        ) = MyTheme(
            type = MyThemeType.Material3,
            padding = Padding(),
            spacing = Spacing(),
            scrollbar = Scrollbar(),
            icon = Icon(
                size = defaultIconSize
            ),
            minimumInteractiveComponentSize = 48.dp,
            shapes = shapes,
            typography = typography
        )

        @Composable
        fun desktopCompact(
            shapes: Shapes = MaterialTheme.shapes,
            typography: Typography = desktopCompactTypography,
        ) = MyTheme(
            type = MyThemeType.Custom,
            padding = Padding(
                mini = 2.dp,
                small = 4.dp,
                default = 8.dp,
                content = 8.dp,
                cardContent = 8.dp
            ),
            spacing = Spacing(
                mini = 4.dp,
                small = 4.dp,
                default = 8.dp,
                large = 8.dp
            ),
            scrollbar = Scrollbar(
                size = 8.dp,
                spacing = 4.dp
            ),
            icon = Icon(
                size = compactIconSize
            ),
            minimumInteractiveComponentSize = desktopMinimumInteractiveComponentSize,
            shapes = shapes,
            typography = typography
        )
    }
}

// --------------
// LocalTheme
// --------------

@Composable
fun MyTheme(
    theme: MyTheme = MyTheme.default(),
    options: MyComponentOptions = MyComponentOptions.default(),
    texts: MyComponentTexts = MyComponentTexts.DEFAULT,
    content: @Composable () -> Unit,
) {
    ProvideMyTheme(theme, options, texts, content)
}

@Composable
private fun ProvideMyTheme(
    theme: MyTheme,
    options: MyComponentOptions,
    texts: MyComponentTexts,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalTheme provides theme,
        LocalComponentOptions provides options,
        LocalComponentStyles provides when (theme.type) {
            MyThemeType.Material3 -> ComponentStyles.default()
            MyThemeType.Custom -> ComponentStyles.desktopCompact()
        },
        LocalComponentTexts provides texts,
        content = content
    )
}

internal val LocalTheme =
    staticCompositionLocalOf<MyTheme> { throw IllegalStateException("No LocalTheme provided") }