package com.michaelflisar.toolbox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.icons.Empty
import com.michaelflisar.toolbox.icons.MyIcons
import com.michaelflisar.toolbox.theme.LocalComponentOptions
import com.michaelflisar.toolbox.theme.LocalComponentStyles

@Composable
fun MyPicker(
    text: String,
    icon: IconComposable,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    enabled: Boolean = true,
    onStartPicker: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        MyInput(
            value = text,
            modifier = Modifier,
            maxLines = maxLines,
            enabled = enabled,
            shape = LocalComponentOptions.current.textField.outlinedShape,
            showClearIcon = true,
            clearIcon = MyIcons.Empty
        )

        Box(
            Modifier
                .matchParentSize()
                .clip(LocalComponentOptions.current.textField.outlinedShape)
                .clickable(
                    enabled = false,
                    role = Role.Button,
                    onClick = {}
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(
                    end = LocalComponentStyles.current.textField.outlinedContentPadding.calculateEndPadding(
                        LocalLayoutDirection.current
                    )
                )
                .clip(CircleShape)
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onStartPicker
                )
                .size(LocalComponentStyles.current.textField.trailingIconTotalSize)
                .padding(LocalComponentStyles.current.textField.trailingIconPadding),
            contentAlignment = Alignment.Center
        ) {
            com.michaelflisar.toolbox.extensions.Icon(
                icon = icon,
                modifier = Modifier
                    .size(LocalComponentStyles.current.textField.trailingIconSize)
            )
        }
    }
}