package com.michaelflisar.toolbox.app.features.toolbar

import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.toolbar_mode_background
import com.michaelflisar.toolbox.core.resources.toolbar_mode_black
import com.michaelflisar.toolbox.core.resources.toolbar_mode_primary
import org.jetbrains.compose.resources.StringResource

enum class ToolbarStyle(val label: StringResource) {
    Primary(Res.string.toolbar_mode_primary),
    Background(Res.string.toolbar_mode_background),
    Black(Res.string.toolbar_mode_black),
}