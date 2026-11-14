package com.michaelflisar.toolbox.form.items

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.michaelflisar.composestyled.ui.components.StyledPopupDropdown
import com.michaelflisar.composestyled.ui.components.StyledPopupDropdownDefaults
import com.michaelflisar.toolbox.form.FormConfig

data class FormDropdown<T : Any>(
    override val label: String,
    val items: List<T>,
    val mapper: @Composable (T) -> String,
    val selected: T,
    val onSelectionChange: (T) -> Unit,
    override val subLabel: String? = null,
    override val help: FormHelp? = null,
    override val visible: Boolean = true,
    override val enabled: Boolean = true,
) : FormItem.Labeled {

    constructor(
        label: String,
        items: List<T>,
        mapper: @Composable (T) -> String,
        selected: MutableState<T>,
        subLabel: String? = null,
        help: FormHelp? = null,
        visible: Boolean = true,
        enabled: Boolean = true,
    ) : this(
        label = label,
        items = items,
        mapper = mapper,
        selected = selected.value,
        onSelectionChange = { selected.value = it },
        subLabel = subLabel,
        help = help,
        visible = visible,
        enabled = enabled
    )

    @Composable
    override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
        if (config.showSingleDropdownsAsText && items.size == 1 && selected == items.first()) {
            FormInfoContent(text = mapper(items.first()))
            return
        }
        StyledPopupDropdown(
            title = "",
            modifier = Modifier.fillMaxWidth(),
            items = items,
            selected = selected,
            onSelectionChange = { _, item -> onSelectionChange(item) },
            enabled = enabled && parentIsEnabled,
            shape = config.shape,
            popupWidthConfig = config.popupWidthConfig,
            content = StyledPopupDropdownDefaults.defaultRenderer(
                mapper = { mapper(it) }
            )
        )
    }
}
