package com.michaelflisar.toolbox.extensions

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.toolbox.IconComposable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

fun ImageVector.toIconComposable(tint: Color = Color.Unspecified): IconComposable {
    return this.let {
        @Composable { contentDescription, modifier, tint2 ->
            Icon(
                imageVector = this,
                contentDescription = contentDescription,
                modifier = modifier,
                tint = tint.takeIf { it != Color.Unspecified } ?: tint2)
        }
    }
}

fun Painter.toIconComposable(tint: Color = Color.Unspecified): IconComposable {
    return this.let {
        @Composable { contentDescription, modifier, tint2 ->
            Icon(
                this,
                contentDescription = contentDescription,
                modifier = modifier,
                tint = tint.takeIf { it != Color.Unspecified } ?: tint2)
        }
    }
}

fun DrawableResource.toIconComposable(tint: Color = Color.Unspecified): IconComposable {
    return this.let {
        @Composable { contentDescription, modifier, tint2 ->
            Icon(
                painterResource(this),
                contentDescription = contentDescription,
                modifier = modifier,
                tint = tint.takeIf { it != Color.Unspecified } ?: tint2)
        }
    }
}

@Composable
fun Icon(
    icon: IconComposable,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    icon.invoke(contentDescription, modifier, tint)
}