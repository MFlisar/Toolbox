package com.michaelflisar.toolbox.form2

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.components.MyColumn


@Composable
fun FormContainer(
    scrollable: Boolean = false,
    content: @Composable FormScope.() -> Unit,
) {
    MyColumn(
        modifier = if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier
    ) {
        FormScopeImpl.content()
    }
}