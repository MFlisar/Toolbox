package com.michaelflisar.toolbox.ui.adaptive.implementations

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.michaelflisar.toolbox.ui.adaptive.ProvideCustomStyledContentLocals
import com.michaelflisar.toolbox.ui.adaptive.runtime.InternalComposeStyledApi

/**
 * setzt locals für Material als auch Styled!
 */
@OptIn(InternalComposeStyledApi::class)
@Composable
internal fun CopyMaterialThemeLocals(
    contentColor: Color? = null,
    textStyle: TextStyle? = null,
    content: @Composable () -> Unit,
) {
    if (contentColor == null && textStyle == null) {
        content()
        return
    }

    ProvideCustomStyledContentLocals(
        contentColor = contentColor,
        textStyle = LocalTextStyle.current
    ) {
        content()
    }
}

/**
 * setzt locals für Material als auch Styled!
 */
@OptIn(InternalComposeStyledApi::class)
@Composable
internal fun CopyStyledLocals(
    outline: Color? = null,
    outlineVariant: Color? = null,
    contentColor: Color? = null,
    textStyle: TextStyle? = null,
    content: @Composable () -> Unit,
) {
    if (outlineVariant == null && outline == null && contentColor == null && textStyle == null) {
        content()
        return
    }
    CompositionLocalProvider(
        LocalContentColor provides (contentColor ?: LocalContentColor.current),
        LocalTextStyle provides (textStyle ?: LocalTextStyle.current)
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.let {
                it.copy(
                    outline = outline ?: it.outline,
                    outlineVariant = outlineVariant ?: it.outlineVariant,
                )
            }
        ) {
            content()
        }
    }
}