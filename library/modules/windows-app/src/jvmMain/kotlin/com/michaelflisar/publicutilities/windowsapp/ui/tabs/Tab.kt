package com.michaelflisar.publicutilities.windowsapp.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign

sealed class Tab {

    @Composable
    abstract fun RenderItem(page: MutableState<Int>, index: Int)

    abstract val content: @Composable () -> Unit

    class Item(
        val name: String,
        val painter: Painter? = null,
        val painterIsIcon: Boolean = true,
        override val content: @Composable () -> Unit
    ) : Tab() {

        @Composable
        override fun RenderItem(page: MutableState<Int>, index: Int) {
            if (painter != null)
                VerticalTabIconItem(name, painter, painterIsIcon, index, page)
            else
                VerticalTabItem(name, index, page)
        }
    }

    class Region(
        val text: String,
        val textAlign: TextAlign = TextAlign.Center
    ) : Tab() {

        override val content: @Composable () -> Unit = {}

        @Composable
        override fun RenderItem(page: MutableState<Int>, index: Int) {
            VerticalTabsRegion(text, textAlign)
        }
    }
}