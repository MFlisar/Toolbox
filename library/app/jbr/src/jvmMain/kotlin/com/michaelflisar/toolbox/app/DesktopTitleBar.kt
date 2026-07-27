package com.michaelflisar.toolbox.app

import androidx.compose.ui.graphics.vector.ImageVector

object DesktopTitleBar {

    /**
     * setup for the DesktopMenuBar, which is used to configure the appearance and behavior of the menu bar.
     *
     * the corresponding actiosn will be shown in the right corner of the menu bar
     *
     * @param showAlwaysOnTop whether to show the "Always on Top" option in the menu bar. Default is true.
     * @param showThemeSelector whether to show the theme selector in the menu bar. Default
     */
    class Setup(
        val showAlwaysOnTop: Boolean = true,
        val showThemeSelector: Boolean = true
    )


    /**
     * an action item placed directly in the right corner of the menu bar, which can be used to perform a specific action when clicked.
     */
    class TitleAction(
        val title: String,
        val imageVector: ImageVector,
        val onClick: () -> Unit,
    )

}