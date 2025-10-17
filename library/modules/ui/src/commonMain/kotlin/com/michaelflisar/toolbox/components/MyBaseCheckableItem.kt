package com.michaelflisar.toolbox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import com.michaelflisar.toolbox.LocalTheme

@Composable
internal fun MyBaseCheckableItem(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)?,
    checked: Boolean,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .then(
                if (onCheckedChange != null) {
                    Modifier.clickable { onCheckedChange.invoke(!checked) }
                } else Modifier)
            .padding(LocalTheme.current.padding.default)
            .width(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(LocalTheme.current.spacing.default),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title != null) {
            Column(
                modifier = Modifier.weight(1f).padding(end = LocalTheme.current.spacing.default)
            ) {
                CompositionLocalProvider(LocalTextStyle provides style) {
                    title()
                }
            }
        }
        content()
    }
}