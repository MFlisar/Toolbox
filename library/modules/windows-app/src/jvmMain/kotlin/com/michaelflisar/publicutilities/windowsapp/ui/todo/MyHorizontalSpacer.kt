package com.michaelflisar.publicutilities.windowsapp.ui.todo

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme

@Composable
fun MyHorizontalSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(AppTheme.ITEM_SPACING))
}