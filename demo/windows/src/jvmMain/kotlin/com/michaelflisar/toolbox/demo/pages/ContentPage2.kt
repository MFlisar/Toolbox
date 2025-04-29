package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyExpandableTitle
import com.michaelflisar.toolbox.components.rememberMyExpandableTitleStyle
import com.michaelflisar.toolbox.ui.MyScrollableColumn

@Composable
fun ContentPage2() {

    val selectedIndex = remember { mutableStateOf(0) }
    val items = remember { mutableStateOf((1..100).map { "Item $it" }) }

    MyScrollableColumn(
        modifier = Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
    ) {
        MyExpandableTitle(title = { Text("Expandable") }) {
            Text("Content...")
        }
        MyExpandableTitle({ Text("Expandable2") }, info = { Text("State") }) {
            Text("Content...")
        }

        MyExpandableTitle(
            { Text("Expandable3 - Primary") },
            style = rememberMyExpandableTitleStyle(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                applyColorsToContent = true,
                applyColorsToTitle = true
            )
        ) {
            Text("Content...")
        }
        MyExpandableTitle(
            { Text("Expandable4 - Primary Header") },
            style = rememberMyExpandableTitleStyle(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                applyColorsToContent = false,
                applyColorsToTitle = true
            )
        ) {
            Text("Content...")
        }
        MyExpandableTitle(
            { Text("Expandable5 - Outlined") },
            style = rememberMyExpandableTitleStyle(
                borderColor = MaterialTheme.colorScheme.outline
            )
        ) {
            Text("Content...")
        }

        MyExpandableTitle(
            { Text("Expandable5 - Outlined with primary header") },
            style = rememberMyExpandableTitleStyle(
                borderColor = MaterialTheme.colorScheme.outline,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                applyColorsToContent = false,
                applyColorsToTitle = true
            )
        ) {
            Text("Content...")
        }

        MyExpandableTitle(
            { Text("Expandable6 - Outlined with primary") },
            style = rememberMyExpandableTitleStyle(
                borderColor = MaterialTheme.colorScheme.outline,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                applyColorsToContent = true,
                applyColorsToTitle = true
            )
        ) {
            Text("Content...")
        }
    }
}