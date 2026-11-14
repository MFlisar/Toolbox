package com.michaelflisar.toolbox.form.items

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.tokens.LocalStyledContentColor
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.toolbox.form.FormConfig

data class FormInfo(
    override val label: String,
    val info: String,
    override val subLabel: String? = null,
    override val help: FormHelp? = null,
    override val visible: Boolean = true,
    override val enabled: Boolean = true,
) : FormItem.Labeled {

    @Composable
    override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
        FormInfoContent(
            text = info,
            modifier = Modifier.padding(vertical = StyledTheme.spacing.small)
        )
    }
}

@Composable
internal fun FormInfoContent(
    text: String,
    modifier: Modifier = Modifier,
) {
    StyledText(
        text = text,
        modifier = Modifier.fillMaxWidth().then(modifier),
        color = LocalStyledContentColor.current
    )
}
