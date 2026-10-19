package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyCheckChip
import com.michaelflisar.toolbox.components.MyCheckbox
import com.michaelflisar.toolbox.components.MyChip
import com.michaelflisar.toolbox.components.MyChipsRow
import com.michaelflisar.toolbox.components.MyChipsRowMultiSelect
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyDropdownButton
import com.michaelflisar.toolbox.components.MyDropdownDefaults
import com.michaelflisar.toolbox.components.MyDropdownIndex
import com.michaelflisar.toolbox.components.MyDropdownMenu
import com.michaelflisar.toolbox.components.MyDropdownMultiSelect
import com.michaelflisar.toolbox.components.MyDropdownMultiSelectDefaults
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyInputButton
import com.michaelflisar.toolbox.components.MyNumberPicker
import com.michaelflisar.toolbox.components.MyNumericInput
import com.michaelflisar.toolbox.components.MyOutlinedButton
import com.michaelflisar.toolbox.components.MyPicker
import com.michaelflisar.toolbox.components.MyPopupInfo
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.components.MySegmentedButtonRow
import com.michaelflisar.toolbox.components.MySegmentedButtonRowMultiSelect
import com.michaelflisar.toolbox.components.MySwitch
import com.michaelflisar.toolbox.components.MyTitle
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.numbers.rememberMyNumberParser
import com.michaelflisar.toolbox.numbers.rememberMyNumberValidator
import com.michaelflisar.toolbox.ui.MyScrollableColumn

@Parcelize
object PageTestUI : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        name = "UI",
        icon = Icons.Default.Visibility.toIconComposable()
    )

    @Composable
    override fun Screen() {
        Page()
    }

}

@Composable
private fun Page(
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val scope = rememberCoroutineScope()

    MyScrollableColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize()
    ) {
        TestRow(
            "1) MyDropdown + MyMultiDropdown"
        )
        {
            val modifier = Modifier//.width(200.dp)
            ReferenceInput()
            MyDropdownIndex(
                modifier = modifier,
                title = "Dropdown (Button)",
                items = listOf("One", "Two", "Three"),
                selectedIndex = remember { mutableStateOf(0) },
                style = MyDropdownDefaults.styleButton(),
                popupWidthConfig = MyDropdownMenu.WidthConfig.Auto()
            )
            MyDropdownIndex(
                modifier = modifier,
                title = "Dropdown (Button + MatchParent Dropdown Width Mode)",
                items = listOf("One", "Two", "Three"),
                selectedIndex = remember { mutableStateOf(0) },
                style = MyDropdownDefaults.styleButton(),
                popupWidthConfig = MyDropdownMenu.WidthConfig.MatchParent()
            )
            MyDropdownIndex(
                modifier = modifier,
                title = "Dropdown (OutlinedButton)",
                items = listOf("One", "Two", "Three"),
                selectedIndex = remember { mutableStateOf(0) },
                style = MyDropdownDefaults.styleOutlinedButton()
            )
            MyDropdownMultiSelect(
                modifier = modifier,
                title = "MultiDropdown (OutlinedButton)",
                items = listOf("One", "Two", "Three"),
                selected = remember { mutableStateOf(emptyList<String>()) },
                style = MyDropdownDefaults.styleOutlinedButton(),
                popupWidthConfig = MyDropdownMenu.WidthConfig.Auto()
            )
            //MyMultiDropdown(
            //    modifier = modifier,
            //    title = "MultiDropdown (OutlinedButton + MatchParent Dropdown Width Mode)",
            //    items = listOf("One", "Two", "Three"),
            //    selected = remember { mutableStateOf(emptyList<String>()) },
            //    style = MyDropdownDefaults.styleOutlinedButton(),
            //    popupWidthConfig = MyDropdownMenu.WidthConfig.MatchParent()
            //)
            MyDropdownMultiSelect(
                modifier = modifier,
                title = "MultiDropdown (Button)",
                items = List(100) { "Item $it" },
                selected = remember { mutableStateOf(emptyList<String>()) },
                style = MyDropdownDefaults.styleButton(),
                popupWidthConfig = MyDropdownMenu.WidthConfig.Auto(),
                content = MyDropdownMultiSelectDefaults.defaultRenderer(
                    mapper = { item -> "Item: $item" },
                    dropdownMapper = { item -> "Item: $item" }
                )
            )
            //MyMultiDropdown(
            //    modifier = modifier,
            //    title = "MultiDropdown (Button + MatchParent Dropdown Width Mode)",
            //    items = listOf("One", "Two", "Three"),
            //    selected = remember { mutableStateOf(emptyList<String>()) },
            //    style = MyDropdownDefaults.styleButton(),
            //    popupWidthConfig = MyDropdownMenu.WidthConfig.MatchParent()
            //)
            MyDropdownIndex(
                modifier = modifier,
                title = "Dropdown (Button)",
                items = List(100) { "Item $it" },
                selectedIndex = remember { mutableStateOf(0) },
                style = MyDropdownDefaults.styleButton(),
                popupWidthConfig = MyDropdownMenu.WidthConfig.Auto(),
                filter = MyDropdown.Filter(
                    label = "Filter",
                ) { filter, item -> item.contains(filter, ignoreCase = true) },
                content = {
                    when (it) {
                        is MyDropdown.RenderContext.Selection -> {
                            Text("Selected: ${it.selection}")
                        }
                        is MyDropdown.RenderContext.DropdownItem<*> -> {
                            Text("Item: ${it.item} - Selected: ${it.selected}", fontWeight = if (it.selected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            )
        }
        TestRow(
            "2) MyDropdownButton"
        )
        {
            ReferenceInput()
            val modifier = Modifier//.width(200.dp)
            MyDropdownButton(
                modifier = modifier,
                onClick = {
                    appState.showToast("DropdownButton clicked")
                }
            ) {
                Text("DropdownButton")
            }
        }
        TestRow(
            "3) MyDropdown + MyMultiDropdown"
        )
        {
            val modifier = Modifier//.width(400.dp)
            ReferenceInput()
            MyDropdownIndex(
                modifier = modifier,
                title = "Button Style - Index basierend",
                items = listOf("One", "Two", "Three"),
                selectedIndex = remember { mutableStateOf(0) }
            )

            MyDropdown(
                modifier = modifier,
                title = "Button Style - Value basierend",
                items = listOf("One", "Two", "Three"),
                selected = remember { mutableStateOf("One") }
            )
            MyDropdownMultiSelect(
                modifier = modifier,
                title = "Dropdown (Button)",
                items = listOf("One", "Two", "Three"),
                selected = remember { mutableStateOf(emptyList<String>()) },
                popupWidthConfig = MyDropdownMenu.WidthConfig.Auto()
            )
            MyDropdownMultiSelect(
                modifier = modifier,
                title = "Dropdown (Button + MatchParent Dropdown Width Mode)",
                items = listOf("One", "Two", "Three"),
                selected = remember { mutableStateOf(emptyList<String>()) },
                popupWidthConfig = MyDropdownMenu.WidthConfig.MatchParent()
            )
        }
        TestRow(
            "4) Buttons + Chips"
        )
        {
            val modifier = Modifier//.width(400.dp)
            ReferenceInput()
            MyInputButton(
                modifier = modifier,
                title = "Button",
                value = "Text...",
                onClick = {
                    appState.showToast("InputButton clicked")
                }
            )
            MyButton(
                modifier = modifier,
                text = "Button",
                onClick = {
                    appState.showToast("Button clicked")
                }
            )
            MyOutlinedButton(
                modifier = modifier,
                text = "Outlined Button",
                onClick = {
                    appState.showToast("Outlined Button clicked")
                }
            )
            MyIconButton(
                modifier = modifier,
                icon = Icons.Default.Visibility.toIconComposable(),
                onClick = {
                    appState.showToast("IconButton clicked")
                }
            )
            MyCheckChip(
                modifier = modifier,
                title = "Check Chip",
                state = remember { mutableStateOf(false) }
            )
            MyChip(
                modifier = modifier,
                title = "Chip",
                onClick = {
                    appState.showToast("Chip clicked")
                }
            )

        }

        TestRow(
            "5) Chips Row",
            scrollable = false
        )
        {
            val modifier = Modifier//.width(400.dp)
            MyChipsRow(
                modifier = modifier,
                items = listOf("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight"),
                selected = remember { mutableStateOf(emptyList<String>()) }
            )
            MyChipsRowMultiSelect(
                modifier = modifier,
                items = listOf("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight"),
                selected = remember { mutableStateOf(emptyList<String>()) }
            )
        }

        TestRow(
            "6) Segmented Row",
            scrollable = false
        )
        {
            val modifier = Modifier//.width(400.dp)
            MySegmentedButtonRow(
                modifier = modifier,
                items = listOf("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight"),
                selected = remember { mutableStateOf(emptyList<String>()) }
            )
            MySegmentedButtonRowMultiSelect(
                modifier = modifier,
                items = listOf("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight"),
                selected = remember { mutableStateOf(emptyList<String>()) }
            )
        }

        TestRow(
            "7) Others"
        )
        {
            val modifier = Modifier//.width(400.dp)
            ReferenceInput()

            val number = remember { mutableIntStateOf(5) }
            val validator = rememberMyNumberValidator(number.value, 1, 10)
            val parser = rememberMyNumberParser(number.value, 1, 1)
            MyNumberPicker(
                modifier = modifier,
                validator = validator,
                parser = parser,
                label = "Number Picker",
                value = number,
                selectAllOnFocus = true
            )

            MyNumericInput(
                modifier = modifier,
                title = "Numeric Input",
                value = remember { mutableStateOf(5) },
                defaultValue = 0
            )
        }

        TestRow(
            "8) Picker + Popup Info + Switch + Checkbox",
            scrollable = true
        )
        {
            val modifier = Modifier//.width(400.dp)
            ReferenceInput()

            MyPicker(
                modifier = modifier, //.width(100.dp), // test ob text abgeschnitten wird und icon sichtbar bleibt
                text = "Mein File.xml",
                icon = Icons.Default.AttachFile.toIconComposable(),
                onStartPicker = {
                    appState.showToast("Picker clicked")
                }
            )

            val showInfo = remember { mutableStateOf(false) }
            MyIconButton(
                onClick = { showInfo.value = true }
            ) {
                Icon(Icons.Outlined.Info, null)
                MyPopupInfo(
                    modifier = Modifier.padding(8.dp),
                    expanded = showInfo,
                    title = "Titel",
                    text = "Info Text"
                )
            }

            MySwitch(
                modifier = modifier,
                title = "Switch (Toggle on Icon Click Only)",
                checked = remember { mutableStateOf(false) },
                onCheckedChange = {
                    appState.showToast("Switch changed to $it")
                },
                toggleOnIconClickOnly = true
            )
            MySwitch(
                modifier = modifier,
                title = "Switch (Toggle on click anywhere)",
                checked = remember { mutableStateOf(false) },
                onCheckedChange = {
                    appState.showToast("Switch changed to $it")
                },
                toggleOnIconClickOnly = false
            )
            MyCheckbox(
                modifier = modifier,
                title = "Checkbox (Toggle on Icon Click Only)",
                checked = remember { mutableStateOf(false) },
                onCheckedChange = {
                    appState.showToast("Checkbox changed to $it")
                },
                toggleOnIconClickOnly = true
            )
            MyCheckbox(
                modifier = modifier,
                title = "Checkbox (Toggle on click anywhere)",
                checked = remember { mutableStateOf(false) },
                onCheckedChange = {
                    appState.showToast("Checkbox changed to $it")
                },
                toggleOnIconClickOnly = false
            )
        }
    }
}

// --------------------
// Hilfs Composables
// --------------------

@Composable
fun TestRow(
    title: String,
    scrollable: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    MyTitle(text = title)
    MyRow(
        verticalAlignment = Alignment.Bottom,
        modifier = if (scrollable) Modifier.horizontalScroll(rememberScrollState()) else Modifier,
        content = content
    )
}

@Composable
fun ReferenceInput() {
    MyInput(
        modifier = Modifier.width(100.dp),
        title = "Input",
        value = remember { mutableStateOf("REFERENZ") },
        enabled = false
    )
}