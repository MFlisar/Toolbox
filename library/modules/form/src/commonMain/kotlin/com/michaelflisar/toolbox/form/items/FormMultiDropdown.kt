package com.michaelflisar.toolbox.form.items

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.michaelflisar.composestyled.ui.components.StyledPopupDropdownMultiSelect
import com.michaelflisar.composestyled.ui.components.StyledPopupDropdownMultiSelectDefaults
import com.michaelflisar.toolbox.form.FormConfig
import com.michaelflisar.toolbox.form.FormDefaults

data class FormMultiDropdown(
    override val label: String,
    val items: List<String>,
    val selected: List<String>,
    val onSelectionChange: (List<String>) -> Unit,
    val sortSelectionByItems: Boolean = true,
    val formatter: @Composable (selected: List<String>) -> String = FormDefaults.multiDropdownFormatter,
    override val subLabel: String? = null,
    override val help: FormHelp? = null,
    override val visible: Boolean = true,
    override val enabled: Boolean = true,
) : FormItem.Labeled {

    constructor(
        label: String,
        items: List<String>,
        selected: MutableState<List<String>>,
        sortSelectionByItems: Boolean = true,
        formatter: @Composable (selected: List<String>) -> String = FormDefaults.multiDropdownFormatter,
        subLabel: String? = null,
        help: FormHelp? = null,
        visible: Boolean = true,
        enabled: Boolean = true,
    ) : this(
        label = label,
        items = items,
        selected = selected.value,
        onSelectionChange = { selected.value = it },
        formatter = formatter,
        sortSelectionByItems = sortSelectionByItems,
        subLabel = subLabel,
        help = help,
        visible = visible,
        enabled = enabled
    )

    @Composable
    override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
        StyledPopupDropdownMultiSelect<String>(
            title = "",
            modifier = Modifier.fillMaxWidth(),
            items = items,
            selected = selected,
            onSelectionChange = { _, items -> onSelectionChange(items) },
            enabled = enabled && parentIsEnabled,
            shape = config.shape,
            popupWidthConfig = config.popupWidthConfig,
            sortSelectionByItems = sortSelectionByItems,
            content = StyledPopupDropdownMultiSelectDefaults.defaultRenderer(
                formatter = formatter
            )
        )
    }
}
