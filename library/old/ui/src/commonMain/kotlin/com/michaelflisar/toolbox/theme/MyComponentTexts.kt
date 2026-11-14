package com.michaelflisar.toolbox.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalComponentTexts = staticCompositionLocalOf { MyComponentTexts.DEFAULT }

/**
 * commonly used texts for components, can be overridden in the theme
 *
 * used because localisation is never fully defined so this is a way to provide a default for all components
 * but also every user can provide fully localised texts for their app
 */
@Stable
data class MyComponentTexts(
    val filterAll: String,
    val selectAll: String,
    val clearSelection: String,
) {
    companion object {

        val GERMAN = MyComponentTexts(
            filterAll = "Alle",
            selectAll = "Alles auswählen",
            clearSelection = "Auswahl löschen",
        )
        val ENGLISH = MyComponentTexts(
            filterAll = "All",
            selectAll = "Select all",
            clearSelection = "Clear selection",
        )

        val DEFAULT = GERMAN
    }
}