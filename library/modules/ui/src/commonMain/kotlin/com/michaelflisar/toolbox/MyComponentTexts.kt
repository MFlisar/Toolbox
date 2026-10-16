package com.michaelflisar.toolbox

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalComponentTexts = staticCompositionLocalOf { MyComponentTexts.DEFAULT }

@Stable
data class MyComponentTexts(
    val selectAll: String,
    val clearSelection: String,
) {
    companion object {

        val GERMAN = MyComponentTexts(
            selectAll = "Alles auswählen",
            clearSelection = "Auswahl löschen",
        )
        val ENGLISH = MyComponentTexts(
            selectAll = "Select all",
            clearSelection = "Clear selection",
        )

        val DEFAULT = GERMAN
    }
}