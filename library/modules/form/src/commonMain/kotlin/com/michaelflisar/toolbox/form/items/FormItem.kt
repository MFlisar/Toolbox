package com.michaelflisar.toolbox.form.items

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.form.FormConfig

sealed interface FormItem {

    val visible: Boolean
        get() = true

    val enabled: Boolean
        get() = true

    @Composable
    fun Content(config: FormConfig, parentIsEnabled: Boolean)

    sealed interface Labeled : FormItem {
        val label: String
        val subLabel: String?
        val help: FormHelp?
    }
}
