package com.michaelflisar.toolbox.form2

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.components.MyDropdown

@Composable
fun <T> FormSpinner(
    title: String,
    value: T,
    items: List<T & Any>,
    mapper: @Composable (item: T) -> String,
    mapperDropdown: @Composable (item: T) -> String = mapper,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onValueChanged: (value: T) -> Unit,
) {
    val value = remember { mutableStateOf(value) }
    LaunchedEffect(Unit) {
        snapshotFlow { value.value }.collect { onValueChanged(it) }
    }
    FormSpinner(title, value, items, mapper, mapperDropdown, modifier)
}

@Composable
fun <T> FormSpinner(
    title: String,
    value: MutableState<T>,
    items: List<T & Any>,
    mapper: @Composable (item: T) -> String,
    mapperDropdown: @Composable (item: T) -> String = mapper,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    FormItem(
        label = title,
        modifier = modifier
    ) {
        MyDropdown(
            modifier = Modifier.fillMaxWidth(),
            title = "",
            items = items,
            selected = value,
            mapper = mapper,
            mapperDropdown = mapperDropdown
        )
    }
}
/*
@PreviewCurrent
@Composable
fun PreviewFormSpinner() {
    IronPreviewTheme {
        val selected = remember {
            mutableStateOf(Units.Weight.KG)
        }
        FormContainer {
            FormSpinner(
                title = "Text",
                value = selected,
                items = Units.Weight.entries,
                mapper = { item, _ -> item.label }
            )
        }
    }
}*/