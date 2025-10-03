package com.michaelflisar.toolbox.demo.pages.tests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.DialogState
import com.michaelflisar.composedialogs.core.DialogStateNoData
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.composedialogs.dialogs.color.DialogColor
import com.michaelflisar.composedialogs.dialogs.color.rememberDialogColor
import com.michaelflisar.composedialogs.dialogs.date.DialogDate
import com.michaelflisar.composedialogs.dialogs.date.rememberDialogDate
import com.michaelflisar.composedialogs.dialogs.frequency.DialogFrequency
import com.michaelflisar.composedialogs.dialogs.frequency.classes.Frequency
import com.michaelflisar.composedialogs.dialogs.frequency.rememberDialogFrequency
import com.michaelflisar.composedialogs.dialogs.info.DialogInfo
import com.michaelflisar.composedialogs.dialogs.input.DialogInput
import com.michaelflisar.composedialogs.dialogs.list.DialogList
import com.michaelflisar.composedialogs.dialogs.menu.DialogMenu
import com.michaelflisar.composedialogs.dialogs.number.DialogNumberPicker
import com.michaelflisar.composedialogs.dialogs.number.NumberPickerSetup
import com.michaelflisar.composedialogs.dialogs.progress.DialogProgress
import com.michaelflisar.composedialogs.dialogs.time.DialogTime
import com.michaelflisar.composedialogs.dialogs.time.rememberDialogTime
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.form.FormDialog
import com.michaelflisar.toolbox.form.fields.rememberFormFieldCheckbox
import com.michaelflisar.toolbox.form.fields.rememberFormFieldDropdown
import com.michaelflisar.toolbox.form.fields.rememberFormFieldInfo
import com.michaelflisar.toolbox.form.fields.rememberFormFieldNumber
import com.michaelflisar.toolbox.form.fields.rememberFormFieldText
import com.michaelflisar.toolbox.form.rememberFormFields
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

@Parcelize
object PageTestDialogs : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        title = "Dialogs",
        subTitle = null,
        icon = Icons.Default.Window.toIconComposable()
    )

    @Composable
    override fun provideMenu(): List<MenuItem> = emptyList()

    @Composable
    override fun Screen() {
        Page()
    }

}

@Composable
private fun Page(
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    // TODO: diese Tests in einem (nicht dokumentierten) Module in Dialogs hinterlegen? dann kann man diese leicht auch hier nutzen...
    val dialogs = listOf(
        rememberDialogTest("Info") { name, state ->
            DialogInfo(
                state = state,
                info = "This is an info dialog",
                title = { Text(name) }
            )
        },
        rememberDialogTest("Input") { name, state ->
            DialogInput(
                state = state,
                value = remember { mutableStateOf("") },
                title = { Text(name) }
            )
        },
        rememberDialogTest("Number") { name, state ->
            DialogNumberPicker(
                state = state,
                value = remember { mutableStateOf(0) },
                setup = NumberPickerSetup(0, 100, 1),
                title = { Text(name) }
            )
        },
        rememberDialogTest("Date") { name, state ->
            DialogDate(
                state = state,
                date = rememberDialogDate(),
                title = { Text(name) }
            )
        },
        rememberDialogTest("Time") { name, state ->
            DialogTime(
                state = state,
                time = rememberDialogTime(),
                title = { Text(name) }
            )
        },
        rememberDialogTest("Color") { name, state ->
            DialogColor(
                state = state,
                color = rememberDialogColor(Color.Red),
                title = { Text(name) }
            )
        },
        rememberDialogTest("List") { name, state ->
            val selected = remember { mutableStateOf<Int?>(null) }
            val items = listOf("Item 1", "Item 2", "Item 3")
            DialogList(
                state = state,
                items = items,
                itemIdProvider = { items.indexOf(it) },
                selectionMode = DialogList.SelectionMode.SingleSelect(
                    selected = selected,
                    selectOnRadioButtonClickOnly = false
                ),
                itemContents = DialogList.ItemDefaultContent(
                    text = { it }
                ),
                title = { Text(name) }
            )
        },
        rememberDialogTest("Menu") { name, state ->
            val items = listOf(
                com.michaelflisar.composedialogs.dialogs.menu.MenuItem.Item(
                    title = "Item 1",
                    description = "Description 1",
                    onClick = {}
                )
            )
            DialogMenu(
                state = state,
                items = items,
                title = { Text(name) }
            )
        },
        rememberDialogTest("Progress") { name, state ->
            DialogProgress(
                state = state,
                title = { Text(name) }
            )
        },
        rememberDialogTest("Frequency") { name, state ->
            val frequency =
                rememberDialogFrequency(Frequency.Weekly(DayOfWeek.MONDAY, LocalTime(12, 0), 1))
            DialogFrequency(
                state = state,
                frequency = frequency,
                title = { Text(name) }
            )
        },
        rememberDialogTest("Form") { name, state ->

            val field1 = rememberFormFieldInfo("Info", "This is some info")
            val field2 = rememberFormFieldText("Text", "Some text")
            val field3 = rememberFormFieldNumber("Number", 5)
            val field4 = rememberFormFieldCheckbox("Checkbox", false)
            val field5 = rememberFormFieldDropdown("Liste", 0, listOf("A", "B", "C"))
            FormDialog(
                state = state,
                name = name,
                labelWidth = 200.dp,
                fields = rememberFormFields(listOf(field1, field2, field3, field4, field5)),
                onSave = {
                    val text = field2.value
                    val number = field3.value
                    val bool = field4.value
                    val list = field5.value
                    println("text: $text, number: $number, bool: $bool, list: $list" )
                },
                onDelete = {

                }

            )
        },
    )

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            PageTestDialogs.provideData().value.title,
            style = MaterialTheme.typography.titleMedium
        )
        dialogs.forEach {
            Button(
                onClick = { it.state.show() }
            ) {
                Text(it.name)
            }
            it.Render()
        }
    }
}

@Composable
private fun rememberDialogTest(
    name: String,
    dialog: @Composable (name: String, state: DialogState) -> Unit,
) = DialogTest(
    name = name,
    state = rememberDialogState(),
    dialog = dialog
)

private class DialogTest(
    val name: String,
    val state: DialogStateNoData,
    val dialog: @Composable (name: String, state: DialogState) -> Unit,
) {
    @Composable
    fun Render() {
        if (state.visible)
            dialog(name, state)
    }
}