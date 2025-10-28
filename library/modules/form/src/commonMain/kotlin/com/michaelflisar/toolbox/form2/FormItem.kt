package com.michaelflisar.toolbox.form2

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.components.MyColumn

@Composable
fun FormItem(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    MyColumn(
        modifier = modifier,
    ) {
        Text(text = label, style = MaterialTheme.typography.titleSmall)
        content()
    }
}