package com.michaelflisar.toolbox.ui.adaptive.classes


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.toolbox.ui.adaptive.components.StyledIcon
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledContentColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

typealias IconComposable = @Composable (contentDescription: String?, modifier: Modifier, tint: Color) -> Unit

fun ImageVector.toIconComposable(
    tint: Color = Color.Unspecified,
    disableTint: Boolean = false,
): IconComposable {
    return this.let {
        @Composable { contentDescription, modifier, tint2 ->
            StyledIcon(
                imageVector = this,
                contentDescription = contentDescription,
                modifier = modifier,
                tint = (tint.takeIf { it != Color.Unspecified } ?: tint2).takeIf { !disableTint }
                    ?: Color.Unspecified
            )
        }
    }
}

fun Painter.toIconComposable(
    tint: Color = Color.Unspecified,
    disableTint: Boolean = false,
): IconComposable {
    return this.let {
        @Composable { contentDescription, modifier, tint2 ->
            StyledIcon(
                painter = this,
                contentDescription = contentDescription,
                modifier = modifier,
                tint = (tint.takeIf { it != Color.Unspecified } ?: tint2).takeIf { !disableTint }
                    ?: Color.Unspecified
            )
        }
    }
}

fun DrawableResource.toIconComposable(
    tint: Color = Color.Unspecified,
    disableTint: Boolean = false,
): IconComposable {
    return this.let {
        @Composable { contentDescription, modifier, tint2 ->
            StyledIcon(
                painter = painterResource(this),
                contentDescription = contentDescription,
                modifier = modifier,
                tint = (tint.takeIf { it != Color.Unspecified } ?: tint2).takeIf { !disableTint }
                    ?: Color.Unspecified
            )
        }
    }
}

@Composable
fun IconComposable(
    icon: IconComposable,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = LocalStyledContentColor.current,
) {
    icon.invoke(contentDescription, modifier, tint)
}