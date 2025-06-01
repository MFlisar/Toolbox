package com.michaelflisar.lumberjack.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyChip
import com.michaelflisar.toolbox.components.MyChipsMultiRow
import com.michaelflisar.toolbox.components.MyChipsSingleRow
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyMultiDropdown
import com.michaelflisar.toolbox.components.MyMultiSegmentedButtonRow
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.components.MySegmentedButtonRow
import com.michaelflisar.toolbox.components.MySegmentedControl
import com.michaelflisar.toolbox.components.MyTitle

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
suspend fun main() {

    CanvasBasedWindow("Demo", canvasElementId = "ComposeTarget") {
        val counter = remember { mutableIntStateOf(0) }
        MaterialTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text("Demo", modifier = Modifier.padding(8.dp))
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier.padding(paddingValues).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MyButton(
                        onClick = {
                            counter.value += 1
                        },
                        text = "Counter: ${counter.value}"
                    )

                    MyColumn(
                        modifier = Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
                    ) {
                        val items = listOf("1", "2", "3")
                        val selected = remember { mutableStateOf(0) }
                        val selectedMulti = remember { mutableStateOf(emptyList<String>()) }

                        MySegmentedControl(items = items, selectedIndex = selected)
                        MyChipsSingleRow(items = items, selectedIndex = selected)
                        MyDropdown(title = "Select", items = items, selected = selected)
                        MySegmentedButtonRow(items = items, selected = selected)

                        MyChipsMultiRow(items = items, selected = selectedMulti)
                        MyMultiDropdown(title = "Select", items = items, selected = selectedMulti)
                        MyMultiSegmentedButtonRow(items = items, selected = selectedMulti)

                        MyTitle("Chip Row")
                        MyRow(itemSpacing = 4.dp) {
                            items.forEach {
                                MyChip(title = it, modifier = Modifier.widthIn(min = 48.dp), onClick = {})
                            }
                        }
                    }
                }
            }
        }
    }
}