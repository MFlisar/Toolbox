package com.michaelflisar.toolbox.ui.adaptive.implementations.material3

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme
import com.michaelflisar.toolbox.ui.adaptive.classes.StyledThemeDefinition
import com.michaelflisar.toolbox.ui.adaptive.components.StyledButton
import com.michaelflisar.toolbox.ui.adaptive.components.StyledIcon
import com.michaelflisar.toolbox.ui.adaptive.implementations.material3.components.Material3Button
import com.michaelflisar.toolbox.ui.adaptive.implementations.material3.components.Material3Icon
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledColors
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledPadding
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledRegistry
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledShapes
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledSizes
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledSpacing
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledTypography

/**
 * Material 3 implementation
 *
 * 1:1 M3 components
 *
 * Target: Mobile devices (Android)
 *
 * <b>ATTENTION</b>: M3 compenents are not designed to be desktop devices, their target are mobile / touch devices.
 * Therefore the M3 implementation does not fully support [StyledSizes], [StyledSpacing] and [StyledPadding] and will ignore them in most cases.
 * BUT the default values of [StyledSizes], [StyledSpacing] and [StyledPadding] are set to the M3 defaults,
 * so that all components will look like M3 components
 *
 * Colors are mapped from StyledTheme, including derived surface and inverse roles.
 * [customizeColorScheme] can override derived roles, for example tertiary colors.
 */
@Composable
fun rememberMaterial3Theme(
    // components
    button: StyledButton.Implementation = Material3Button.create(),
    icon: StyledIcon.Implementation = Material3Icon.create(),
    customizeColorScheme: ((ColorScheme) -> ColorScheme)? = null,
    // custom components can be added here
    register: (StyledRegistry.Builder.() -> Unit)? = null,
): StyledThemeDefinition {
    return remember(
        button, register, customizeColorScheme, icon
    ) {
        StyledThemeDefinition(
            name = "Material3",
            registry = StyledRegistry.create {
                // components
                register(StyledButton.Key, button)
                register(StyledIcon.Key, icon)
                // custom components
                register?.invoke(this)
            },
            root = { content ->

                val styledColors = StyledTheme.colors
                val styledTypography = StyledTheme.typography
                val styledShapes = StyledTheme.shapes

                val colorScheme = remember(styledColors, customizeColorScheme) {
                    val mapped = styledColors.toMaterial3ColorScheme()
                    customizeColorScheme?.invoke(mapped) ?: mapped
                }
                val shapes = remember(styledShapes) { styledShapes.toMaterial3Shapes() }
                val typography = remember(styledTypography) { styledTypography.toMaterial3Typography() }

                MaterialTheme(
                    shapes = shapes,
                    typography = typography,
                    colorScheme = colorScheme,
                    content = content
                )
            }
        )
    }
}

private fun StyledShapes.toMaterial3Shapes() = Shapes(
    extraSmall = extraSmall,
    small = small,
    medium = medium,
    large = large,
    extraLarge = extraLarge,
)

private fun StyledTypography.toMaterial3Typography() = Typography(
    displayLarge = headlineLarge,
    displayMedium = headlineMedium,
    displaySmall = headlineSmall,
    headlineLarge = headlineLarge,
    headlineMedium = headlineMedium,
    headlineSmall = headlineSmall,
    titleLarge = titleLarge,
    titleMedium = titleMedium,
    titleSmall = titleSmall,
    bodyLarge = bodyLarge,
    bodyMedium = bodyMedium,
    bodySmall = bodySmall,
    labelLarge = labelLarge,
    labelMedium = labelMedium,
    labelSmall = labelSmall,
)

/**
 * Existing styled pairs are preserved; tertiary uses secondary, not a status color.
 * Missing roles approximate Material tonal targets using luminance, not HCT.
 * Independently supplied light/dark accents can yield different fixed colors.
 */
private fun StyledColors.toMaterial3ColorScheme(): ColorScheme {
    val defaults = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    return defaults.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primarySurface,
        onPrimaryContainer = onPrimarySurface,
        inversePrimary = primary.atTone(if (isDarkTheme) 40f else 80f),
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondarySurface,
        onSecondaryContainer = onSecondarySurface,
        tertiary = secondary,
        onTertiary = onSecondary,
        tertiaryContainer = secondarySurface,
        onTertiaryContainer = onSecondarySurface,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceTint = primary,
        inverseSurface = surface.atTone(if (isDarkTheme) 90f else 20f),
        inverseOnSurface = surface.atTone(if (isDarkTheme) 20f else 95f),
        error = error,
        onError = onError,
        errorContainer = errorSurface,
        onErrorContainer = onErrorSurface,
        outline = outline,
        outlineVariant = outlineVariant,
        scrim = Color.Black,
        surfaceBright = surface.atTone(if (isDarkTheme) 24f else 98f),
        surfaceDim = surface.atTone(if (isDarkTheme) 6f else 87f),
        surfaceContainerLowest = surface.atTone(if (isDarkTheme) 4f else 100f),
        surfaceContainerLow = surface.atTone(if (isDarkTheme) 10f else 96f),
        surfaceContainer = surface.atTone(if (isDarkTheme) 12f else 94f),
        surfaceContainerHigh = surface.atTone(if (isDarkTheme) 17f else 92f),
        surfaceContainerHighest = surface.atTone(if (isDarkTheme) 22f else 90f),
        primaryFixed = primary.atTone(90f),
        primaryFixedDim = primary.atTone(80f),
        onPrimaryFixed = primary.atTone(10f),
        onPrimaryFixedVariant = primary.atTone(30f),
        secondaryFixed = secondary.atTone(90f),
        secondaryFixedDim = secondary.atTone(80f),
        onSecondaryFixed = secondary.atTone(10f),
        onSecondaryFixedVariant = secondary.atTone(30f),
        tertiaryFixed = secondary.atTone(90f),
        tertiaryFixedDim = secondary.atTone(80f),
        onTertiaryFixed = secondary.atTone(10f),
        onTertiaryFixedVariant = secondary.atTone(30f),
    )
}

private fun Color.atTone(tone: Float): Color {
    // CIE L* -> relative luminance; interpolation does not preserve HCT chroma.
    val f = (tone + 16f) / 116f
    val target = if (tone > 8f) f * f * f else tone / 903.2963f
    val opaque = copy(alpha = 1f)
    val current = opaque.luminance()
    val endpoint = if (target > current) Color.White else Color.Black
    var low = 0f
    var high = 1f
    repeat(24) {
        val fraction = (low + high) / 2f
        val luminance = lerp(opaque, endpoint, fraction).luminance()
        if ((target > current && luminance < target) ||
            (target <= current && luminance > target)
        ) {
            low = fraction
        } else {
            high = fraction
        }
    }
    return lerp(opaque, endpoint, (low + high) / 2f)
}