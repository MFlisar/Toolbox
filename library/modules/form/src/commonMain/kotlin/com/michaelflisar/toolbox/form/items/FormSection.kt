package com.michaelflisar.toolbox.form.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.toolbox.form.FormConfig
import com.michaelflisar.toolbox.form.FormSectionStyle

@Composable
private fun FormRow(
    verticalPadding: Dp = 0.dp,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding)
            .clip(shape)
            .background(backgroundColor)
            .padding(contentPadding)
    ) {
        content()
    }
}

data class FormSection(
    val title: String,
    override val visible: Boolean = true,
) : FormItem {

    @Composable
    override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
        FormRow(
            verticalPadding = config.sectionStyle.verticalPadding,
            shape = when (config.sectionStyle) {
                is FormSectionStyle.Row -> config.sectionStyle.shape
                is FormSectionStyle.Text -> RectangleShape
            },
            backgroundColor = when (config.sectionStyle) {
                is FormSectionStyle.Row -> config.sectionStyle.backgroundColor
                is FormSectionStyle.Text -> Color.Unspecified
            },
            contentPadding = when (config.sectionStyle) {
                is FormSectionStyle.Row -> config.sectionStyle.contentPadding
                is FormSectionStyle.Text -> PaddingValues()
            }
        ) {
            StyledText(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = config.sectionStyle.textStyle,
                color = config.sectionStyle.textColor,
            )
        }
    }
}
