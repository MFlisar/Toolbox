package com.michaelflisar.toolbox.windows.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.classes.LocalStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopBasePage(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            title = { Text(title) }
        )
        Box(Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)) {
            content()
        }
    }
}