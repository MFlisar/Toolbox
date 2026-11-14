package com.michaelflisar.toolbox.ui.adaptive.classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme

fun Color.asColorToken(): ColorToken = ColorToken.Fixed(this)

@Stable
sealed interface ColorToken {

    @Composable
    @ReadOnlyComposable
    fun resolve(): Color

    @Immutable
    data class Fixed(
        val color: Color,
        val alpha: Float? = null,
    ) : ColorToken {

        @Composable
        @ReadOnlyComposable
        override fun resolve(): Color = if (alpha != null) color.copy(alpha = alpha) else color

    }

    enum class Token : ColorToken {
        Primary,
        OnPrimary,
        Secondary,
        OnSecondary,
        Background,
        OnBackground,
        Surface,
        OnSurface,
        Error,
        OnError,
        Outline,
        OutlineVariant
        ;

        @Composable
        @ReadOnlyComposable
        override fun resolve(): Color {
            return when (this) {
                Primary -> StyledTheme.colors.primary
                OnPrimary -> StyledTheme.colors.onPrimary
                Secondary -> StyledTheme.colors.secondary
                OnSecondary -> StyledTheme.colors.onSecondary
                Background -> StyledTheme.colors.background
                OnBackground -> StyledTheme.colors.onBackground
                Surface -> StyledTheme.colors.surface
                OnSurface -> StyledTheme.colors.onSurface
                Error -> StyledTheme.colors.error
                OnError -> StyledTheme.colors.onError
                Outline -> StyledTheme.colors.outline
                OutlineVariant -> StyledTheme.colors.outlineVariant
            }
        }
    }
}