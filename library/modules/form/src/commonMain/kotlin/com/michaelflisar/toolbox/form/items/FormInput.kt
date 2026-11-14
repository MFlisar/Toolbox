package com.michaelflisar.toolbox.form.items

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.michaelflisar.composestyled.ui.components.StyledInput
import com.michaelflisar.toolbox.form.FormConfig

data class FormInput(
    override val label: String,
    val value: String,
    val onValueChange: (value: String) -> Unit,
    val minLines: Int = 1,
    val maxLines: Int = 1,
    override val subLabel: String? = null,
    override val help: FormHelp? = null,
    override val visible: Boolean = true,
    override val enabled: Boolean = true,
) : FormItem.Labeled {

    constructor(
        label: String,
        value: MutableState<String>,
        minLines: Int = 1,
        maxLines: Int = 1,
        subLabel: String? = null,
        help: FormHelp? = null,
        visible: Boolean = true,
        enabled: Boolean = true,
    ) : this(
        label = label,
        value = value.value,
        onValueChange = { value.value = it },
        minLines = minLines,
        maxLines = maxLines,
        subLabel = subLabel,
        help = help,
        visible = visible,
        enabled = enabled
    )

    @Composable
    override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
        StyledInput(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            enabled = enabled && parentIsEnabled,
            singleLine = minLines == 1 && maxLines == 1,
            minLines = minLines,
            maxLines = maxLines,
            shape = config.shape
        )
    }
}
