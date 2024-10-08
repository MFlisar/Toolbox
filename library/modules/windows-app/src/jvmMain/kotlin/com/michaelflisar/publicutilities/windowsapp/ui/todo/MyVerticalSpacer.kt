package com.michaelflisar.publicutilities.windowsapp.ui.todo

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme

@Composable
fun MyVerticalSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.width(AppTheme.ITEM_SPACING))
}