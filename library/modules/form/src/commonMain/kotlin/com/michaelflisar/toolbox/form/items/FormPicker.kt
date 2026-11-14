package com.michaelflisar.toolbox.form.items

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.composestyled.classes.IconComposable
import com.michaelflisar.composestyled.ui.components.StyledPicker
import com.michaelflisar.toolbox.form.FormConfig

data class FormPicker(
    override val label: String,
    val text: String,
    val icon: IconComposable,
    override val subLabel: String? = null,
    override val help: FormHelp? = null,
    override val visible: Boolean = true,
    override val enabled: Boolean = true,
    val onClick: () -> Unit,
) : FormItem.Labeled {

    @Composable
    override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
        StyledPicker(
            value = text,
            icon = icon,
            onStartPicker = onClick,
            modifier = Modifier.fillMaxWidth(),
            label = "",
            enabled = enabled && parentIsEnabled
        )
    }
}
