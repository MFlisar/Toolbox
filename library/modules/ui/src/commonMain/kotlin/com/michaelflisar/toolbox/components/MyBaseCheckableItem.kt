package com.michaelflisar.toolbox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import com.michaelflisar.toolbox.components
import com.michaelflisar.toolbox.spacing

@Composable
internal fun MyBaseCheckableItem(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)?,
    checked: Boolean,
    style: TextStyle = LocalTextStyle.current,
    padding: PaddingValues = MaterialTheme.components.button.contentPadding,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.components.button.shape)
            .clickable(
                enabled = enabled,
            ) { onCheckedChange.invoke(!checked) }
            .padding(padding)
            .width(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title != null) {
            Column(
                modifier = Modifier.weight(1f).padding(end = MaterialTheme.spacing.default)
            ) {
                CompositionLocalProvider(LocalTextStyle provides style) {
                    title()
                }
            }
        }
        content()
    }
}

@Composable
internal fun MyBaseCheckableItemWrapperOnly(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)?,
    style: TextStyle = LocalTextStyle.current,
    padding: PaddingValues = MaterialTheme.components.button.contentPadding,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.components.button.shape)
            .padding(padding)
            .width(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title != null) {
            Column(
                modifier = Modifier.weight(1f).padding(end = MaterialTheme.spacing.default)
            ) {
                CompositionLocalProvider(LocalTextStyle provides style) {
                    title()
                }
            }
        }
        content()
    }
}