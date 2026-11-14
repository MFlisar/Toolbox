package com.michaelflisar.toolbox.form.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.form.FormConfig

data class FormCustom(
    val content: @Composable (FormConfig, Boolean) -> Unit,
    override val visible: Boolean = true,
    override val enabled: Boolean = true,
) : FormItem {

    @Composable
    override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = config.rowPaddingStart)
        ) {
            content(config, parentIsEnabled && enabled)
        }
    }
}
