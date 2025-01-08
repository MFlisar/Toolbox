package com.michaelflisar.toolbox.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MyButton(
    text: String,
    icon: ImageVector? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = colors,
        enabled = enabled
    ) {
        ButtonContent(text, icon, iconRotation, iconTint)
    }
}

@Composable
fun MyOutlinedButton(
    text: String,
    icon: ImageVector? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        colors = colors,
        enabled = enabled
    ) {
        ButtonContent(text, icon, iconRotation, iconTint)
    }
}

@Composable
fun MyTextButton(
    text: String,
    icon: ImageVector? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        colors = colors,
        enabled = enabled
    ) {
        ButtonContent(text, icon, iconRotation, iconTint)
    }
}

@Composable
fun MyFilledTonalButton(
    text: String,
    icon: ImageVector? = null,
    iconRotation: Float = 0f,
    iconTint: Color? = null,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
        colors = colors,
        enabled = enabled
    ) {
        ButtonContent(text, icon, iconRotation, iconTint)
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector?,
    iconRotation: Float = 0f,
    iconTint: Color? = null
) {
    if (icon == null) {
        Text(text)
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp).rotate(iconRotation),
                tint = iconTint ?: LocalContentColor.current
            )
            Text(text)
        }
    }
}