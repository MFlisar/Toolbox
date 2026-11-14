package com.michaelflisar.toolbox.theme

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.components.MyDropdownMenu

/**
 * enthält Optionen für Komponenten; Ziel ist es, dass Komponenten spezifisch (an [MyThemeType] angepasst) konfiguriert werden können
 *
 * Defaults und Eigenschaften die der Nutzer nicht ändern können soll, werden in [ComponentStyles] definiert.
 * Eigenschaften die der Nutzer ändern können soll, werden in [MyComponentOptions] definiert.
 */
@Immutable
data class MyComponentOptions(
    val button: Button,
    val iconButton: IconButton,
    val textField: TextField,
    val checkbox: Checkbox,
    val switch: Switch,
    val dropdown: Dropdown,
    val menu: Menu,
) {
    companion object {

        @Composable
        fun default(
            button: Button = Button(
                shape = ButtonDefaults.shape,
                outlinedShape = ButtonDefaults.outlinedShape,
                textShape = ButtonDefaults.textShape,
                filledTonalShape = ButtonDefaults.filledTonalShape
            ),
            iconButton: IconButton = IconButton(
                shape = IconButtonDefaults.standardShape,
                outlinedShape = IconButtonDefaults.outlinedShape,
                filledShape = IconButtonDefaults.filledShape
            ),
            textField: TextField = TextField(
                outlinedShape = OutlinedTextFieldDefaults.shape
            ),
            checkbox: Checkbox = Checkbox(toggleOnIconClickOnly = false),
            switch: Switch = Switch(toggleOnIconClickOnly = false),
            dropdown: Dropdown = Dropdown(widthConfig = MyDropdownMenu.WidthConfig.MatchParent()),
            menu: Menu = Menu(
                shape = MenuDefaults.shape,
                itemShape = MaterialTheme.shapes.small,
                itemPadding = 8.dp
            ),
        ) = MyComponentOptions(
            button = button,
            iconButton = iconButton,
            textField = textField,
            checkbox = checkbox,
            switch = switch,
            dropdown = dropdown,
            menu = menu
        )

        @Composable
        fun desktopCompact(
            buttonShape: Shape = MaterialTheme.shapes.small,
            button: Button = Button(allShapes = buttonShape),
            iconButton: IconButton = IconButton(allShapes = buttonShape),
            textField: TextField = TextField(outlinedShape = buttonShape),
            checkbox: Checkbox = Checkbox(toggleOnIconClickOnly = false),
            switch: Switch = Switch(toggleOnIconClickOnly = false),
            dropdown: Dropdown = Dropdown(widthConfig = MyDropdownMenu.WidthConfig.MatchParent()),
            menu: Menu = Menu(
                shape = MenuDefaults.shape,
                itemShape = buttonShape,
                itemPadding = 4.dp
            ),
        ) = MyComponentOptions(
            button = button,
            iconButton = iconButton,
            textField = textField,
            checkbox = checkbox,
            switch = switch,
            dropdown = dropdown,
            menu = menu
        )
    }

    @Immutable
    data class Button(
        val shape: Shape,
        val outlinedShape: Shape,
        val textShape: Shape,
        val filledTonalShape: Shape,
    ) {
        constructor(
            allShapes: Shape,
        ) : this(
            shape = allShapes,
            outlinedShape = allShapes,
            textShape = allShapes,
            filledTonalShape = allShapes
        )
    }

    @Immutable
    data class IconButton(
        val shape: Shape,
        val outlinedShape: Shape,
        val filledShape: Shape,
    ) {
        constructor(
            allShapes: Shape,
        ) : this(
            shape = allShapes,
            outlinedShape = allShapes,
            filledShape = allShapes
        )
    }

    @Immutable
    data class TextField(
        val outlinedShape: Shape,
    )

    @Immutable
    data class Checkbox(
        val toggleOnIconClickOnly: Boolean = false,
    )

    @Immutable
    data class Switch(
        val toggleOnIconClickOnly: Boolean = false,
    )

    @Immutable
    data class Dropdown(
        val widthConfig: MyDropdownMenu.WidthConfig = MyDropdownMenu.WidthConfig.Auto(),
    )

    @Immutable
    data class Menu(
        val shape: Shape,
        val itemShape: Shape,
        val itemPadding: Dp,
    )
}

val LocalComponentOptions =
    staticCompositionLocalOf<MyComponentOptions> {
        throw IllegalStateException("No ComponentOptions provided")
    }
