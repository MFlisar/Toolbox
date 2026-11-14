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
import com.michaelflisar.toolbox.theme.LocalComponentOptions
import com.michaelflisar.toolbox.theme.LocalComponentStyles
import com.michaelflisar.toolbox.theme.spacing

@Composable
internal fun MyBaseCheckableItem(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)?,
    checked: Boolean,
    style: TextStyle = LocalTextStyle.current,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    MyBaseCheckableItemImpl(
        modifier = modifier,
        title = title,
        checked = checked,
        style = style,
        enabled = enabled,
        onCheckedChange = onCheckedChange,
        content = content
    )
}

@Composable
internal fun MyBaseCheckableItemWrapperOnly(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)?,
    style: TextStyle = LocalTextStyle.current,
    content: @Composable () -> Unit,
) {
    MyBaseCheckableItemImpl(
        modifier = modifier,
        title = title,
        checked = false,
        style = style,
        enabled = true,
        onCheckedChange = null,
        content = content
    )
}

@Composable
private fun MyBaseCheckableItemImpl(
    modifier: Modifier,
    title: @Composable (() -> Unit)?,
    checked: Boolean,
    style: TextStyle,
    enabled: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .then(
                if (onCheckedChange != null) {
                    Modifier
                        .clip(LocalComponentOptions.current.button.shape)
                        .clickable(
                            enabled = enabled,
                        ) { onCheckedChange.invoke(!checked) }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = LocalComponentStyles.current.checkableItem.horizontalPadding)
            .width(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title != null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = LocalComponentStyles.current.checkableItem.verticalPadding)
            ) {
                CompositionLocalProvider(LocalTextStyle provides style) {
                    title()
                }
            }
        }
        content()
    }
}