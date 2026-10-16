package com.michaelflisar.toolbox.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.michaelflisar.toolbox.components

@Composable
fun MyPicker(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    enabled: Boolean = true,
    onStartPicker: () -> Unit,
) {
    MyPicker(
        text = text,
        icon = { size ->
            MyIcon(
                imageVector = icon,
                contentDescription = null,
                size = size
            )
        },
        modifier = modifier,
        maxLines = maxLines,
        enabled = enabled,
        onStartPicker = onStartPicker
    )
}

@Composable
fun MyPicker(
    text: String,
    icon: @Composable (size: Dp) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    enabled: Boolean = true,
    onStartPicker: () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        MyInput(
            value = text,
            modifier = Modifier,
            maxLines = maxLines,
            enabled = enabled,
            shape = MaterialTheme.components.textField.outlinedShape,
            showClearIcon = false
        )

        Box(
            Modifier
                .matchParentSize()
                .clip(MaterialTheme.components.textField.outlinedShape)
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
                    end = MaterialTheme.components.textField.outlinedContentPadding.calculateEndPadding(
                        LocalLayoutDirection.current
                    )
                )
                .clip(CircleShape)
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onStartPicker
                )
                .size(MaterialTheme.components.textField.trailingIconSize + MaterialTheme.components.textField.trailingIconPadding)
                .padding(MaterialTheme.components.textField.trailingIconPadding),
            contentAlignment = Alignment.Center
        ) {
            icon(MaterialTheme.components.textField.trailingIconSize)
        }
    }
}