package com.michaelflisar.toolbox.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * enthält Styles für Komponenten; Ziel ist es, dass Komponenten spezifisch (an [MyThemeType] angepasst) gestylt werden können
 *
 * Defaults und Eigenschaften die der Nutzer nicht ändern können soll, werden hier definiert.
 * Eigenschaften die der Nutzer ändern können soll, werden in [MyComponentOptions] definiert.
 */
@Immutable
internal data class ComponentStyles(
    val type: MyThemeType,
    val button: Button,
    val iconButton: IconButton,
    val textField: TextField,
    val checkbox: Checkbox,
    val switch: Switch,
    val checkableItem: CheckableItem,
    val segmentedButton: SegmentedButton,
    val numberPicker: NumberPicker,
    val menu: Menu,
    val toolbar: Toolbar,
) {


    @Immutable
    data class Button(

        val contentPadding: PaddingValues,
        val textContentPadding: PaddingValues,

        val minWidth: Dp,
        val minHeight: Dp,
    )

    @Immutable
    data class IconButton(
        val size: Dp,
    )

    @Immutable
    data class TextField(

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
    ) {
        val trailingIconTotalSize: Dp
            get() = trailingIconSize + trailingIconPadding * 2
    }

    @Immutable
    data class Checkbox(
        val layerSize: Dp, // 40
        val checkboxSize: Dp, // 20
        val padding: Dp, // 2
        val radiusSize: Dp, // 2
    )

    @Immutable
    data class Switch(
        val layerSize: Dp, // 40
        val trackWidth: Dp, // 52
        val trackHeight: Dp, // 32
        val thumbShape: Shape, // Cirle
        val trackShape: Shape, // Cirle
        val trackOutlinedWidth: Dp, // 2
        val pressedHandleWidth: Dp, // 28
        val thumbDiameter: Dp, // 24
        val uncheckedThumbDiameter: Dp, // 16
    )

    @Immutable
    data class CheckableItem(
        val horizontalPadding: Dp,
        val verticalPadding: Dp,
    )

    @Immutable
    data class SegmentedButton(
        val contentPadding: PaddingValues,
    )

    @Immutable
    data class NumberPicker(
        val minWidth: Dp,
    )

    @Immutable
    data class Menu(
        val dropdownMenuItemPaddingValues: PaddingValues,
    )

    @Immutable
    data class Toolbar(
        val expandedHeight: Dp,
    )

    companion object {

        /**
         * default theme for touch devices - uses M3 components
         */
        @Composable
        fun default(): ComponentStyles {
            val iconSize = MyTheme.defaultIconSize
            val iconButtonSize = 48.dp
            return ComponentStyles(
                type = MyThemeType.Material3,
                button = Button(
                    contentPadding = ButtonDefaults.ContentPadding,
                    textContentPadding = ButtonDefaults.TextButtonContentPadding,
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = ButtonDefaults.MinHeight
                ),
                iconButton = IconButton(
                    size = iconButtonSize
                ),
                textField = TextField(
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
                    layerSize = 40.dp,
                    checkboxSize = 20.dp,
                    padding = 2.dp,
                    radiusSize = 2.dp,
                ),
                switch = Switch(
                    layerSize = 40.dp,
                    trackWidth = 52.dp,
                    trackHeight = 32.dp,
                    thumbShape = CircleShape,
                    trackShape = CircleShape,
                    trackOutlinedWidth = 2.dp,
                    pressedHandleWidth = 28.dp,
                    thumbDiameter = 24.dp,
                    uncheckedThumbDiameter = 16.dp
                ),
                checkableItem = CheckableItem(
                    horizontalPadding = 24.dp, // wie M3 Button: 24.dp
                    verticalPadding = 8.dp // wie M3 Button: 8.dp
                ),
                segmentedButton = SegmentedButton(
                    SegmentedButtonDefaults.ContentPadding
                ),
                numberPicker = NumberPicker(
                    minWidth = 200.dp
                ),
                menu = Menu(
                    dropdownMenuItemPaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
                ),
                toolbar = Toolbar(
                    expandedHeight = TopAppBarDefaults.TopAppBarExpandedHeight
                )
            )
        }

        /**
         * default theme for desktop devices - uses smaller paddings and sizes and custom M§ components if necessary
         */
        @Composable
        fun desktopCompact(): ComponentStyles {
            val textFieldContentPaddingWithLabel = PaddingValues(
                horizontal = 8.dp, // M3: 16.dp
                vertical = 4.dp // M3: 8.dp
            )
            val iconSize = MyTheme.compactIconSize
            val iconButtonSize = 32.dp
            return ComponentStyles(
                type = MyThemeType.Custom,
                button = Button(
                    contentPadding = PaddingValues(
                        horizontal = 8.dp, // M3: 24.dp
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
                    size = iconButtonSize // M3: 40.dp; 24dp icon size + 2 * 8.dp
                ),
                textField = TextField(
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
                    layerSize = 32.dp, // M3: 40.dp
                    checkboxSize = 16.dp, // M3: 20.dp
                    padding = 2.dp, // M3: 2.dp
                    radiusSize = 2.dp, // M3: 2.dp
                ),
                switch = Switch(
                    layerSize = 32.dp, // M3: 40.dp
                    trackWidth = 44.dp, // M3: 52.dp
                    trackHeight = 24.dp, // M3: 32.dp
                    thumbShape = CircleShape,
                    trackShape = CircleShape,
                    trackOutlinedWidth = 2.dp, // M3: 2.dp
                    pressedHandleWidth = 20.dp, // M3: 28.dp
                    thumbDiameter = 18.dp, // M3: 24.dp
                    uncheckedThumbDiameter = 10.dp // M3: 16.dp
                ),
                checkableItem = CheckableItem(
                    horizontalPadding = 8.dp, // wie Button
                    verticalPadding = 4.dp // wie Button
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
                    dropdownMenuItemPaddingValues = PaddingValues(
                        horizontal = 8.dp, // M3: 12.dp
                        vertical = 0.dp // M3: 0.dp
                    )
                ),
                toolbar = Toolbar(
                    expandedHeight = 48.dp // M3: 64.dp
                )
            )
        }
    }
}

internal val LocalComponentStyles =
    staticCompositionLocalOf<ComponentStyles> {
        throw IllegalStateException("No ComponentStyles provided")
    }