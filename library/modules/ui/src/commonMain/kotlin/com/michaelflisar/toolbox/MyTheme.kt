package com.michaelflisar.toolbox

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.michaelflisar.toolbox.components.MyDropdownMenu

val MaterialTheme.padding: Padding
    @Composable
    get() = LocalTheme.current.padding

val MaterialTheme.spacing: Spacing
    @Composable
    get() = LocalTheme.current.spacing

val MaterialTheme.scrollbar: Scrollbar
    @Composable
    get() = LocalTheme.current.scrollbar

val MaterialTheme.components: Components
    @Composable
    get() = LocalTheme.current.components

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
data class Components(
    val style: Style,
    val icons: Icon,
    val button: Button,
    val iconButton: IconButton,
    val textField: TextField,
    val checkbox: Checkbox,
    val switch: Switch,
    val segmentedButton: SegmentedButton,
    val numberPicker: NumberPicker,
    val menu: Menu,
    val dropdown: Dropdown
) {
    enum class Style {
        Material3,
        Custom
    }

    @Immutable
    data class Icon(
        val size: Dp,
    )

    @Immutable
    data class Button(
        val shape: Shape,
        val outlinedShape: Shape,
        val textShape: Shape,
        val filledTonalShape: Shape,

        val contentPadding: PaddingValues,
        val textContentPadding: PaddingValues,

        val minWidth: Dp,
        val minHeight: Dp,
    )

    @Immutable
    data class IconButton(
        val shape: Shape,
        val outlinedShape: Shape,
        val filledShape: Shape,
        val size: Dp,
    )

    @Immutable
    data class TextField(

        val outlinedShape: Shape,

        val contentPaddingWithLabel: PaddingValues,
        val contentPaddingWithoutLabel: PaddingValues,

        val outlinedContentPadding: PaddingValues,
        val outlinedButtonStyleContentPadding: PaddingValues,

        val minWidth: Dp,
        val minHeight: Dp,
        val outlinedMinWidth: Dp,
        val outlinedMinHeight: Dp,

        val trailingIconPadding: Dp,
        val trailingIconSize: Dp,
    )

    @Immutable
    data class Checkbox(
        val size: Dp,
        val toggleOnIconClickOnly: Boolean
    )

    @Immutable
    data class Switch(
        val toggleOnIconClickOnly: Boolean
    )

    @Immutable
    data class SegmentedButton(
        val contentPadding: PaddingValues
    )

    @Immutable
    data class NumberPicker(
        val minWidth: Dp,
    )

    @Immutable
    data class Menu(
        val shape: Shape,
        val dropdownMenuItemPaddingValues: PaddingValues,
        val style: Style,
    ) {
        @Immutable
        data class Style(
            val itemShape: Shape,
            val itemPadding: Dp,
        )
    }

    @Immutable
    data class Dropdown(
        val widthConfig: MyDropdownMenu.WidthConfig
    )

    companion object {

        @Composable
        fun material3(
            buttonShape: Shape = ButtonDefaults.shape,
            buttonOutlinedShape: Shape = ButtonDefaults.outlinedShape,
            buttonTextShape: Shape = ButtonDefaults.textShape,
            buttonFilledTonalShape: Shape = ButtonDefaults.filledTonalShape,
            outlinedTextFieldShape: Shape = OutlinedTextFieldDefaults.shape,
            toggleOnIconClickOnly: Boolean = false,
            menuStyle: Menu.Style = Menu.Style(
                itemShape = MaterialTheme.shapes.small,
                itemPadding = 8.dp
            ),
            dropdown: Dropdown = Dropdown(
                widthConfig = MyDropdownMenu.WidthConfig.Auto()
            )
        ): Components {
            val iconSize = 24.dp
            return Components(
                style = Style.Material3,
                icons = Icon(
                    size = iconSize
                ),
                button = Button(
                    shape = buttonShape,
                    outlinedShape = buttonOutlinedShape,
                    textShape = buttonTextShape,
                    filledTonalShape = buttonFilledTonalShape,
                    contentPadding = ButtonDefaults.ContentPadding,
                    textContentPadding = ButtonDefaults.TextButtonContentPadding,
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = ButtonDefaults.MinHeight
                ),
                iconButton = IconButton(
                    shape = IconButtonDefaults.standardShape,
                    outlinedShape = IconButtonDefaults.outlinedShape,
                    filledShape = IconButtonDefaults.filledShape,
                    size = 48.dp
                ),
                textField = TextField(
                    outlinedShape = outlinedTextFieldShape,
                    contentPaddingWithLabel = TextFieldDefaults.contentPaddingWithLabel(),
                    contentPaddingWithoutLabel = TextFieldDefaults.contentPaddingWithoutLabel(),
                    outlinedContentPadding = OutlinedTextFieldDefaults.contentPadding(),
                    outlinedButtonStyleContentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 5.dp
                    ),
                    minWidth = TextFieldDefaults.MinWidth,
                    minHeight = TextFieldDefaults.MinHeight,
                    outlinedMinWidth = OutlinedTextFieldDefaults.MinWidth,
                    outlinedMinHeight = OutlinedTextFieldDefaults.MinHeight,
                    trailingIconPadding = 8.dp,
                    trailingIconSize = iconSize
                ),
                checkbox = Checkbox(
                    size = iconSize,
                    toggleOnIconClickOnly = toggleOnIconClickOnly
                ),
                switch = Switch(
                    toggleOnIconClickOnly = toggleOnIconClickOnly
                ),
                segmentedButton = SegmentedButton(
                    SegmentedButtonDefaults.ContentPadding
                ),
                numberPicker = NumberPicker(
                    minWidth = 200.dp
                ),
                menu = Menu(
                    shape = MenuDefaults.shape,
                    dropdownMenuItemPaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
                    style = menuStyle
                ),
                dropdown = dropdown
            )
        }

        @Composable
        fun windowsCompact(
            buttonShape: Shape = ButtonDefaults.shape,
            iconButtonShape: Shape = IconButtonDefaults.standardShape,
            textFieldShape: Shape = OutlinedTextFieldDefaults.shape,
            menuShape: Shape = MenuDefaults.shape,
            toggleOnIconClickOnly: Boolean = false,
            menuStyle: Menu.Style = Menu.Style(
                itemShape = MaterialTheme.shapes.small,
                itemPadding = 4.dp
            ),
            dropdown: Dropdown = Dropdown(
                widthConfig = MyDropdownMenu.WidthConfig.Auto()
            )
        ): Components {
            val textFieldContentPaddingWithLabel = PaddingValues(
                horizontal = 8.dp, // M3: 16.dp
                vertical = 4.dp // M3: 8.dp
            )
            val iconSize = 24.dp // M3: 24.dp
            return Components(
                style = Style.Custom,
                icons = Icon(
                    size = iconSize
                ),
                button = Button(
                    shape = buttonShape,
                    outlinedShape = buttonShape,
                    textShape = buttonShape,
                    filledTonalShape = buttonShape,
                    contentPadding = PaddingValues(
                        horizontal = 8.dp, // M3: 16.dp
                        vertical = 4.dp // M3: 8.dp
                    ),
                    textContentPadding = PaddingValues(
                        horizontal = 8.dp, // M3: 16.dp
                        vertical = 4.dp // M3: 8.dp
                    ),
                    minWidth = 48.dp, // M3: 58.dp
                    minHeight = 32.dp // M3: 40.dp
                ),
                iconButton = IconButton(
                    shape = iconButtonShape,
                    outlinedShape = iconButtonShape,
                    filledShape = iconButtonShape,
                    size = 32.dp // M3: 40.dp; 24dp icon size + 2 * 8.dp
                ),
                textField = TextField(
                    outlinedShape = textFieldShape,
                    contentPaddingWithLabel = textFieldContentPaddingWithLabel,
                    contentPaddingWithoutLabel = PaddingValues(
                        all = 8.dp // M3: 16.dp
                    ),
                    outlinedContentPadding = textFieldContentPaddingWithLabel,
                    outlinedButtonStyleContentPadding = PaddingValues(
                        horizontal = 8.dp,
                        vertical = 4.dp // trial & error bis es passt
                    ),
                    minWidth = 180.dp, // M3: 280.dp
                    minHeight = 48.dp, // M3: 56.dp
                    outlinedMinWidth = 180.dp, // M3: 280.dp
                    outlinedMinHeight = 48.dp, // M3: 56.dp
                    trailingIconPadding = 8.dp,
                    trailingIconSize = iconSize
                ),
                checkbox = Checkbox(
                    size = iconSize,
                    toggleOnIconClickOnly = toggleOnIconClickOnly
                ),
                switch = Switch(
                    toggleOnIconClickOnly = toggleOnIconClickOnly
                ),
                segmentedButton = SegmentedButton(
                    PaddingValues(
                        horizontal = 4.dp, // M3: 12.dp
                        vertical = 2.dp // M3: 8.dp
                    )
                ),
                numberPicker = NumberPicker(
                    minWidth = 160.dp
                ),
                menu = Menu(
                    shape = menuShape,
                    dropdownMenuItemPaddingValues = PaddingValues(
                        horizontal = 8.dp, // M3: 12.dp
                        vertical = 0.dp // M3: 0.dp
                    ),
                    style = menuStyle
                ),
                dropdown = dropdown
            )
        }
    }
}

@Immutable
class MyTheme(
    val padding: Padding,
    val spacing: Spacing,
    val scrollbar: Scrollbar,
    val components: Components,
    val minimumInteractiveComponentSize: Dp,
    val shapes: Shapes,
    val typography: Typography,
) {
    companion object {

        @Composable
        fun default(
            shapes: Shapes = MaterialTheme.shapes,
            typography: Typography = MaterialTheme.typography,
            components: Components = Components.material3(),
        ) = MyTheme(
            padding = Padding(),
            spacing = Spacing(),
            scrollbar = Scrollbar(),
            components = components,
            minimumInteractiveComponentSize = 24.dp,
            shapes = shapes,
            typography = typography,
        )

        val windowsCompactTypography: Typography
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

        val windowsMinimumInteractiveComponentSize = 32.dp

        @Composable
        fun windowsDefault(
            shapes: Shapes = MaterialTheme.shapes,
            typography: Typography = windowsCompactTypography,
            components: Components = Components.material3(),
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
            components = components,
            minimumInteractiveComponentSize = windowsMinimumInteractiveComponentSize,
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
    texts: MyComponentTexts = MyComponentTexts.DEFAULT,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalTheme provides theme,
        LocalComponentTexts provides texts,
        content = content
    )
}

internal val LocalTheme =
    staticCompositionLocalOf<MyTheme> { throw IllegalStateException("No LocalTheme provided") }