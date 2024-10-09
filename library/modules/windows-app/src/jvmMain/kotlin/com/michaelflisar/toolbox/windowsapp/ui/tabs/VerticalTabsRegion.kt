package com.michaelflisar.toolbox.windowsapp.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun VerticalTabsRegion(text: String, textAlign: TextAlign = TextAlign.Center) {
    Text(
        modifier = Modifier.background(MaterialTheme.colorScheme.onBackground).fillMaxWidth()
            .padding(4.dp),
        text = text,
        color = MaterialTheme.colorScheme.background,
        textAlign = textAlign,
        fontWeight = FontWeight.Bold
    )
}