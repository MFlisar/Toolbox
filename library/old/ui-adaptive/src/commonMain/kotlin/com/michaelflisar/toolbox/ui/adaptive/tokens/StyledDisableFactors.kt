package com.michaelflisar.toolbox.ui.adaptive.tokens

import androidx.compose.runtime.Immutable

@Immutable
data class StyledDisableFactors(
    val default: Float,
    val outline: Float,
    val content: Float,
) {
    enum class Type {
        /**
         * used for colors that are not an outline/border nor content (text/icon/...)
         */
        Default,

        /**
         * used for outline/border colors
         */
        Outline,

        /**
         * used for content colors (text/icon/...)
         */
        Content
    }

    fun get(type: Type): Float = when (type) {
        Type.Default -> default
        Type.Outline -> outline
        Type.Content -> content
    }
}

fun lightDisableFactors() = StyledDisableFactors(
    default = 0.38f, // Container / Surface
    outline = 0.38f, // Border / Divider
    content = 0.38f, // Text / Icon
)

fun darkDisableFactors() = StyledDisableFactors(
    default = 0.38f, // Container
    outline = 0.38f, // Border
    content = 0.50f, // Text / Icon stärker sichtbar
)