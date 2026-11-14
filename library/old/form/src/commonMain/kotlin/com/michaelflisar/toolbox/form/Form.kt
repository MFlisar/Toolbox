package com.michaelflisar.toolbox.form

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyLabeledInformationHorizontal

@Composable
fun Form(
    fields: FormFields,
    modifier: Modifier = Modifier,
    labelWidth: Dp? = null,
) {
    MyColumn(
        modifier = modifier
    ) {
        fields.fields.forEachIndexed { index, field ->
            MyLabeledInformationHorizontal(
                label = field.label,
                labelWidth = labelWidth
            ) {
                Column(
                    modifier = Modifier.animateContentSize()
                ) {
                    field.render()
                    field.renderState()
                }
            }
        }
    }
}