package com.michaelflisar.toolbox.form.items

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.ui.components.StyledCheckbox
import com.michaelflisar.toolbox.form.FormConfig

data class FormCheckbox(
    override val label: String,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit,
    override val subLabel: String? = null,
    override val help: FormHelp? = null,
    override val visible: Boolean = true,
    override val enabled: Boolean = true,
) : FormItem.Labeled {

    constructor(
        label: String,
        checked: MutableState<Boolean>,
        subLabel: String? = null,
        help: FormHelp? = null,
        visible: Boolean = true,
        enabled: Boolean = true,
    ) : this(
        label = label,
        checked = checked.value,
        onCheckedChange = { checked.value = it },
        subLabel = subLabel,
        help = help,
        visible = visible,
        enabled = enabled
    )

    @Composable
    override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
        StyledCheckbox(
            modifier = Modifier.padding(end = StyledTheme.padding.medium),
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled && parentIsEnabled
        )
    }
}
