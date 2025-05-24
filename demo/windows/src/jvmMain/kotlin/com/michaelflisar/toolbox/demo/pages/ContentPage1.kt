package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.michaelflisar.composecolors.material.MaterialColor
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyButtonDefaults
import com.michaelflisar.toolbox.components.MyCheckChip
import com.michaelflisar.toolbox.components.MyCheckbox
import com.michaelflisar.toolbox.components.MyChip
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyDropdownButton
import com.michaelflisar.toolbox.components.MyDropdownButtonDefaults
import com.michaelflisar.toolbox.components.MyFlowRow
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.components.MyIconButtonDefaults
import com.michaelflisar.toolbox.components.MyNumericInput
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.components.MyTitle
import com.michaelflisar.toolbox.ui.MyScrollableColumn

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContentPage1() {

    val selectedIndex = remember { mutableStateOf(0) }
    val items = remember { mutableStateOf((1..100).map { "Item $it" }) }

    MyScrollableColumn(
        modifier = Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
    ) {
        // 1) Buttons
        MyTitle("Buttons") {
            MyRow {
                MyButton(
                    text = "Test Log",
                    onClick = {
                        L.d { "Test Log" }
                    }
                )
                MyButton(
                    text = "Test Error",
                    style = MyButtonDefaults.styleDefault(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialColor.Red200,
                            contentColor = MaterialColor.White
                        )
                    ),
                    onClick = {
                        L.e { "Test Error" }
                    }
                )
            }
        }

        // 2) Dropdowns
        MyTitle("Dropdowns") {
            MyDropdown(
                title = "List",
                items = items.value,
                selected = selectedIndex,
                filter = MyDropdown.Filter("Search") { filter, item ->
                    filter.isEmpty() || item.contains(filter, true)
                }
            )
            MyRow(
                verticalAlignment = Alignment.Top
            ) {
                MyDropdown(
                    modifier = Modifier.width(200.dp).height(64.dp).padding(top = 8.dp),
                    title = "List",
                    items = items.value,
                    selected = selectedIndex
                )
                MyNumericInput(
                    modifier = Modifier.width(96.dp),
                    title = "Number",
                    value = remember { mutableStateOf(0) }
                )
            }
        }

        // 3) Checkboxes and layout tests for checkboxes
        MyTitle("Checkboxes") {
            MyColumn {
                MyCheckbox(title = "Checkbox1", checked = remember { mutableStateOf(true) })
                MyCheckbox(
                    title = "Checkbox1 with longer title",
                    checked = remember { mutableStateOf(true) }
                )
                MyCheckbox(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Checkbox2",
                    checked = remember { mutableStateOf(true) })
                MyCheckbox(
                    modifier = Modifier.fillMaxWidth(),
                    title = {
                        Text("Checkbox2 with longer title")
                        Text("Some info", style = MaterialTheme.typography.bodySmall)
                    },
                    checked = remember { mutableStateOf(true) }
                )
                Row {
                    Text(modifier = Modifier.weight(1f), text = "Text")
                    MyCheckbox(title = "Checkbox3", checked = remember { mutableStateOf(true) })
                }
                Row {
                    Text(modifier = Modifier.weight(1f), text = "Text")
                    MyCheckbox(
                        title = "Checkbox3 with longer title",
                        checked = remember { mutableStateOf(true) }
                    )
                }
            }
        }

        // 4) Chips
        MyTitle("Chips") {
            MyColumn {
                MyFlowRow {
                    repeat(20) {
                        MyChip(
                            title = "Chip $it",
                            icon = if (it <= 10) null else {
                                { Icon(Icons.Default.Info, null) }
                            },
                            onClick = if (it <= 15) null else {
                                { /* clicked */ }
                            }
                        )
                    }
                    MyChip(title = "Blue", containerColor = Color.Blue, labelColor = Color.White)
                    MyChip(title = "Red", containerColor = Color.Red, labelColor = Color.White) {

                    }
                }
                MyRow {
                    val checked = remember { mutableStateOf(0) }
                    repeat(5) { value ->
                        MyCheckChip(
                            title = "Checked Chip ${value + 1}",
                            checked = checked.value == value
                        ) {
                            checked.value = value
                        }
                    }
                }
            }
        }

        // 5) Buttons
        MyTitle("Buttons") {
            MyFlowRow {
                MyButton(text = "Button") {}
                MyButton(text = "Outlined Button", style = MyButtonDefaults.styleOutlined()) {}
                MyButton(text = "Text Button", style = MyButtonDefaults.styleText()) {}
                MyButton(
                    text = "Filled Tonal Button",
                    style = MyButtonDefaults.styleFilledTonal()
                ) {}
                MyButton(text = "Button + Icon", icon = Icons.Default.Info) {}
                MyButton(
                    text = "Outlined Button + Icon",
                    icon = Icons.Default.Info,
                    style = MyButtonDefaults.styleOutlined()
                ) {}
                MyButton(
                    text = "Text Button + Icon",
                    icon = Icons.Default.Info,
                    style = MyButtonDefaults.styleText()
                ) {}
                MyButton(
                    text = "Filled Tonal Button",
                    icon = Icons.Default.Info,
                    style = MyButtonDefaults.styleFilledTonal()
                ) {}
            }
        }

        // 6) Icon Buttons
        MyTitle("Icon Buttons") {
            MyFlowRow {
                MyIconButton(Icons.Default.Info) {}
                MyIconButton(Icons.Default.Info, style = MyIconButtonDefaults.styleOutlined()) {}
                MyIconButton(Icons.Default.Info, style = MyIconButtonDefaults.styleFilled()) {}
            }
        }

        // 7) Dropdown Buttons
        MyTitle("Dropdown Buttons") {
            MyRow {
                val selected = remember { mutableStateOf("Item 1") }
                MyDropdownButton(
                    type = MyDropdownButtonDefaults.typeDropdown(
                        items = listOf(
                            MyDropdownButton.Entry.Button(
                                text = "Item 1",
                                onClick = { selected.value = "Item 1" }
                            ),
                            MyDropdownButton.Entry.Button(
                                text = "Item 2",
                                onClick = { selected.value = "Item 2" }
                            )
                        )
                    ),
                    content = {
                        Text(selected.value)
                    }
                )
                MyDropdownButton(
                    type = MyDropdownButtonDefaults.typeClick {
                        L.d { "Clicked" }
                    },
                    content = {
                        Text("Dropdown Button (Click Action)")
                    }
                )
                MyDropdownButton(
                    style = MyButtonDefaults.styleDefault(shape = MaterialTheme.shapes.small),
                    type = MyDropdownButtonDefaults.typeClick {
                        L.d { "Clicked" }
                    },
                    content = {
                        Text("Dropdown Button 2 (Click Action)")
                    }
                )
            }
        }

        // testing list item updates
        LaunchedEffect(Unit) {
            if (items.value.size > 50)
                items.value = items.value.subList(0, 50)
        }
    }
}