package com.michaelflisar.toolbox.form.items

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.michaelflisar.composestyled.ui.components.StyledPopupDropdownIndex
import com.michaelflisar.toolbox.form.FormConfig

data class FormDropdownIndex(
    override val label: String,
    val items: List<String>,
    val selectedIndex: Int,
    val onSelectionChange: (Int) -> Unit,
    override val subLabel: String? = null,
    override val help: FormHelp? = null,
    override val visible: Boolean = true,
    override val enabled: Boolean = true,
) : FormItem.Labeled {

    constructor(
        label: String,
        items: List<String>,
        selectedIndex: MutableState<Int>,
        subLabel: String? = null,
        help: FormHelp? = null,
        visible: Boolean = true,
        enabled: Boolean = true,
    ) : this(
        label = label,
        items = items,
        selectedIndex = selectedIndex.value,
        onSelectionChange = { selectedIndex.value = it },
        subLabel = subLabel,
        help = help,
        visible = visible,
        enabled = enabled
    )

    @Composable
    override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
        if (config.showSingleDropdownsAsText && items.size == 1 && selectedIndex == 0) {
            FormInfoContent(text = items.first())
            return
        }
        StyledPopupDropdownIndex(
            title = "",
            modifier = Modifier.fillMaxWidth(),
            items = items,
            selectedIndex = selectedIndex,
            onSelectionChange = { index, _ -> onSelectionChange(index) },
            enabled = enabled && parentIsEnabled,
            shape = config.shape,
            popupWidthConfig = config.popupWidthConfig
        )
    }
}
