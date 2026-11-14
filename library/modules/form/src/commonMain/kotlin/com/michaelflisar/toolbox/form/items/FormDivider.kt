package com.michaelflisar.toolbox.form.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.ui.primitives.StyledHorizontalDivider
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.toolbox.form.FormConfig

data class FormDivider(
    val text: String? = null,
    override val visible: Boolean = true,
    override val enabled: Boolean = true,
) : FormItem {

    @Composable
    override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
        val style = config.dividerStyle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = config.rowPaddingStart)
                .padding(vertical = style.verticalSpacing),
            horizontalArrangement = Arrangement.spacedBy(style.textSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (style.textAlignment != Alignment.Start) {
                if (style.thickness > 0.dp) {
                    StyledHorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = style.color,
                        thickness = style.thickness
                    )
                } else {
                    Box(modifier = Modifier.weight(1f))
                }
            }
            if (text?.isNotEmpty() == true) {
                StyledText(
                    text = text,
                    style = style.textStyle,
                    color = style.textColor
                )
            }
            if (style.textAlignment != Alignment.End) {
                if (style.thickness > 0.dp) {
                    StyledHorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = style.color,
                        thickness = style.thickness
                    )
                } else {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
