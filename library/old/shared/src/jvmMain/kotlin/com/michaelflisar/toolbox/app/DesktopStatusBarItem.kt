package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

sealed class DesktopStatusBarItem {

    class Text(
        val text: String,
        val color: Color = Color.Unspecified,
        val onClick: (() -> Unit)? = null,
    ) : DesktopStatusBarItem()

    class Custom(
        val content: @Composable () -> Unit,
    ) : DesktopStatusBarItem()
}