package com.michaelflisar.publicutilities.windowsapp.ui.tabs

import androidx.compose.ui.graphics.painter.Painter

sealed class TabItem {
    class Item(
        val id: Int,
        val label: String,
        val icon: Icon? = null
    ) : TabItem()

    class Group(
        val label: String
    ) : TabItem()

    class Icon(
        val painter: Painter,
        val isIcon: Boolean = true
    )
}