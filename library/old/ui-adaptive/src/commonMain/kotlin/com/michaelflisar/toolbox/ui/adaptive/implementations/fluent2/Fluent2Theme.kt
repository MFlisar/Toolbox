package com.michaelflisar.toolbox.ui.adaptive.implementations.fluent2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.ui.adaptive.classes.StyledThemeDefinition
import com.michaelflisar.toolbox.ui.adaptive.components.StyledButton
import com.michaelflisar.toolbox.ui.adaptive.components.StyledIcon
import com.michaelflisar.toolbox.ui.adaptive.implementations.fluent2.components.Fluent2Button
import com.michaelflisar.toolbox.ui.adaptive.implementations.fluent2.components.Fluent2Icon
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledRegistry

/**
 * Fluent 2 implementation with Foundation-based buttons, including hover, press
 * and keyboard focus states. Brand colors and typography follow the styled tokens.
 */
@Composable
fun rememberFluent2Theme(
    // components
    button: StyledButton.Implementation = Fluent2Button.create(),
    icon: StyledIcon.Implementation = Fluent2Icon.create(),
    // custom components can be added here
    register: (StyledRegistry.Builder.() -> Unit)? = null,
): StyledThemeDefinition {
    return remember(
        button, register, icon
    ) {
        StyledThemeDefinition(
            name = "Fluent2",
            registry = StyledRegistry.create {
                // components
                register(StyledButton.Key, button)
                register(StyledIcon.Key, icon)
                // custom components
                register?.invoke(this)
            },
            root = { content -> content() }
        )
    }
}