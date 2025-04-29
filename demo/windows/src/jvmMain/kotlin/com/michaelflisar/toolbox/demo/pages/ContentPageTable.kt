package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.demo.TableDataEntry
import com.michaelflisar.toolbox.form.FormDialog
import com.michaelflisar.toolbox.form.rememberFormFieldCustom
import com.michaelflisar.toolbox.form.rememberFormFields
import com.michaelflisar.toolbox.isDark
import com.michaelflisar.toolbox.table.Setup
import com.michaelflisar.toolbox.table.Table
import com.michaelflisar.toolbox.table.TableTitle
import com.michaelflisar.toolbox.table.definitions.rememberTableDefinition

@Composable
fun ContentPageTable() {

    val showEditDialog = rememberDialogState<Int>(null)
    val tableDefinition = rememberTableDefinition(
        columns = TableDataEntry.columns(),
        keyProvider = { it.id }
    )
    val filtered = remember { mutableStateOf(-1) }
    val fullTableData = remember { TableDataEntry.defaultData().toMutableList() }
    val entities = remember { mutableStateOf(fullTableData) }

    // Table
    MyColumn(
        Modifier.fillMaxSize().padding(LocalStyle.current.paddingContent)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onPrimary
        ) {
            TableTitle(
                definition = tableDefinition,
                items = entities.value,
                modifier = Modifier.fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp),
                filtered = filtered
            )
        }
        Table(
            modifier = Modifier.weight(1f),
            definition = tableDefinition,
            items = entities.value,
            setup = Setup(
                clickType = Setup.ClickType.RowClick(
                    onRowClicked = { index, item ->
                        showEditDialog.show(item.id)
                    }
                ),
                emptyText = "Table is empty or filter is filtering all rows..."
            ),
            onFilterChanged = {
                if (entities.value.isNotEmpty()) {
                    filtered.value = it.size
                }
            }
        )
    }

    // Edit Dialog
    if (showEditDialog.visible) {

        val itemId = showEditDialog.requireData()
        val item = entities.value.find { it.id == itemId }!!
        val itemIndex = entities.value.indexOfFirst { it.id == itemId }

        // Felder
        val fieldID = TableDataEntry.fieldID(item)
        val fieldChecked = TableDataEntry.fieldChecked(item)
        val fieldName = TableDataEntry.fieldName(item)
        val fieldAge = TableDataEntry.fieldAge(item)
        val fieldColor = TableDataEntry.fieldColor(item)
        val fieldDescription = TableDataEntry.fieldDescription(item)

        val selectedColor =
            remember { derivedStateOf { fieldColor.value.value.let { TableDataEntry.ColorEnum.entries[it] } } }
        val fieldCustom = rememberFormFieldCustom("Custom Field", selectedColor.value) {
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(it.color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Color: ${it.name}",
                    color = if (it.color.isDark()) Color.White else Color.Black
                )
            }
        }
        val fields = rememberFormFields(
            listOf(
                fieldID,
                fieldChecked,
                fieldName,
                fieldAge,
                fieldColor,
                fieldDescription,
                fieldCustom
            )
        )

        FormDialog(
            state = showEditDialog,
            name = "TableDataEntry",
            fields = fields,
            labelWidth = 120.dp,
            onSave = {
                // save
                val newItem = TableDataEntry(
                    item.id,
                    fieldChecked.value.value,
                    fieldName.value.value,
                    fieldAge.value.value,
                    fieldColor.value.value.let { TableDataEntry.ColorEnum.entries[it] },
                    fieldDescription.value.value
                )
                println("Save: $newItem")
                entities.value.set(itemIndex, newItem)
            }, onDelete = {
                // delete
                entities.value.removeAt(itemIndex)
            }
        )
    }
}