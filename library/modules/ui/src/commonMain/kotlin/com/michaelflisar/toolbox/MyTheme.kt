package com.michaelflisar.toolbox

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val MaterialTheme.padding: Padding
    @Composable
    get() = LocalTheme.current.padding

val MaterialTheme.spacing: Spacing
    @Composable
    get() = LocalTheme.current.spacing

val MaterialTheme.scrollbar: Scrollbar
    @Composable
    get() = LocalTheme.current.scrollbar

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
class MyTheme(
    val padding: Padding,
    val spacing: Spacing,
    val scrollbar: Scrollbar,
    val minimumInteractiveComponentSize: Dp,
    val shapes: Shapes,
    val typography: Typography,
) {
    companion object {

        @Composable
        fun default(
            shapes: Shapes = MaterialTheme.shapes,
            typography: Typography = MaterialTheme.typography,
        ) = MyTheme(
            padding = Padding(),
            spacing = Spacing(),
            scrollbar = Scrollbar(),
            minimumInteractiveComponentSize = 24.dp,
            shapes = shapes,
            typography = typography,
        )

        @Composable
        fun windowsDefault(
            shapes: Shapes = MaterialTheme.shapes,
            typography: Typography = MaterialTheme.typography,
        ) = MyTheme(
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
            minimumInteractiveComponentSize = 24.dp,
            shapes = shapes,
            typography = typography,
        )
    }
}

// --------------
// LocalTheme
// --------------

@Composable
fun MyTheme(
    theme: MyTheme = MyTheme.default(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalTheme provides theme,
        content = content
    )
}

internal val LocalTheme =
    staticCompositionLocalOf<MyTheme> { throw IllegalStateException("No LocalTheme provided") }