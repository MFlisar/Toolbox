package com.michaelflisar.toolbox.form2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyPopupInfo
import com.michaelflisar.toolbox.components.MyTitle

object FormHeader {

    class SpinnerData<T>(
        val items: List<T>,
        val selectedItem: T,
        val mapper: (T) -> String,
        val onItemSelected: (T) -> Unit,
    )

    class InfoData(
        val title: String,
        val info: String,
    )
}

@Composable
fun FormHeader(
    title: String,
    info: FormHeader.InfoData? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Header<Any>(title, info, modifier)
}

@Composable
fun <T> FormHeader(
    title: String,
    info: FormHeader.InfoData? = null,
    spinner: FormHeader.SpinnerData<T>,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Header(title, info, modifier, spinner = spinner)
}

@Composable
fun <T> FormHeader(
    title: String,
    info: FormHeader.InfoData? = null,
    checkbox: MutableState<Boolean>,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Header<Any>(title, info, modifier, checkbox = checkbox)
}

@Composable
private fun <T> Header(
    title: String,
    info: FormHeader.InfoData?,
    modifier: Modifier = Modifier.fillMaxWidth(),
    spinner: FormHeader.SpinnerData<T>? = null,
    checkbox: MutableState<Boolean>? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyTitle(title)
            if (info != null) {
                val showInfo = remember { mutableStateOf(false) }
                IconButton(
                    onClick = { showInfo.value = true }
                ) {
                    Icon(Icons.Outlined.Info, null)
                    MyPopupInfo(
                        modifier = Modifier.padding(8.dp),
                        expanded = showInfo,
                        title = info.title,
                        text = info.info
                    )
                }
            }
        }
        if (spinner != null) {
            val selected = remember { mutableStateOf(spinner.selectedItem) }
            MyDropdown(
                modifier = Modifier.weight(1f),
                items = spinner.items,
                selected = spinner.selectedItem,
                mapper = { spinner.mapper(it) },
                color = MaterialTheme.colorScheme.surface,
                backgroundColor = MaterialTheme.colorScheme.onSurface,
                onSelectionChanged = { item: T ->
                    selected.value = item
                    spinner.onItemSelected.invoke(item)
                }
            )
        }
        if (checkbox != null) {
            Checkbox(checked = checkbox.value, onCheckedChange = { checkbox.value = it })
        }
    }
}
/*
@PreviewCurrent
@Composable
fun PreviewFormHeader() {
    IronPreviewTheme {
        val goalType = remember { mutableStateOf(TargetSetNumber.Type.Value) }
        FormContainer {
            FormHeader(
                stringResource(Res.string.goal_sets),
                FormHeader.InfoData("Goal", "TODO"),
                FormHeader.SpinnerData(
                    TargetSetNumber.Type.entries,
                    goalType.value,
                    { it.getLabel() }
                ) {
                    goalType.value = it
                    // TODO: persist...
                }
            )
        }
    }
}*/