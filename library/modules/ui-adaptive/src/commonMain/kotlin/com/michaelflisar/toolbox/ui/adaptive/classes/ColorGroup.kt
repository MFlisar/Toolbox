package com.michaelflisar.toolbox.ui.adaptive.classes

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ColorGroup(
    val background: Color,
    val foreground: Color,
    val border: Color? = null,
)

@Immutable
data class ColorGroupDef(
    val background: ColorToken,
    val foreground: ColorToken,
    val border: ColorToken? = null,
) {
    constructor(
        background: Color,
        foreground: Color,
        border: Color? = null,
    ) : this(
        background = background.asColorToken(),
        foreground = foreground.asColorToken(),
        border = border?.asColorToken(),
    )

    constructor(colors: ColorGroup) : this(
        background = colors.background.asColorToken(),
        foreground = colors.foreground.asColorToken(),
        border = colors.border?.asColorToken(),
    )

    fun customise(
        background: ColorToken? = null,
        foreground: ColorToken? = null,
        border: ColorToken? = null,
    ) = ColorGroupDef(
        background = background ?: this.background,
        foreground = foreground ?: this.foreground,
        border = border ?: this.border,
    )
}