package com.michaelflisar.toolbox.ui.adaptive.implementations.cupertino

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.michaelflisar.toolbox.ui.adaptive.classes.StyledThemeDefinition
import com.michaelflisar.toolbox.ui.adaptive.components.StyledButton
import com.michaelflisar.toolbox.ui.adaptive.components.StyledIcon
import com.michaelflisar.toolbox.ui.adaptive.implementations.cupertino.components.CupertinoButton
import com.michaelflisar.toolbox.ui.adaptive.implementations.cupertino.components.CupertinoIcon
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledRegistry

@Composable
fun rememberCupertinoTheme(
    // components
    button: StyledButton.Implementation = CupertinoButton.create(),
    icon: StyledIcon.Implementation = CupertinoIcon.create(),
    // custom components can be added here
    register: (StyledRegistry.Builder.() -> Unit)? = null,
): StyledThemeDefinition {
    return remember(
        button, register, icon
    ) {
        StyledThemeDefinition(
            name = "Cupertino",
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