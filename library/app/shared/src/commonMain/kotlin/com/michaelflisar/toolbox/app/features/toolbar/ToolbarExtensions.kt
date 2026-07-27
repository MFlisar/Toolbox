package com.michaelflisar.toolbox.app.features.toolbar

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.app.AppSetup

val ColorScheme.toolbar
    @Composable get() = when (AppSetup.get().prefs.toolbarStyle.collectAsStateNotNull().value) {
        ToolbarStyle.Primary -> MaterialTheme.colorScheme.primary
        ToolbarStyle.Background -> MaterialTheme.colorScheme.background
        ToolbarStyle.Black -> Color.Black
    }

val ColorScheme.onToolbar
    @Composable get() = when (AppSetup.get().prefs.toolbarStyle.collectAsStateNotNull().value) {
        ToolbarStyle.Primary -> MaterialTheme.colorScheme.onPrimary
        ToolbarStyle.Background -> MaterialTheme.colorScheme.onBackground
        ToolbarStyle.Black -> Color.White
    }