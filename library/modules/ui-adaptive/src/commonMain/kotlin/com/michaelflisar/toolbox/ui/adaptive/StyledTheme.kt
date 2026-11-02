package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.michaelflisar.toolbox.ui.adaptive.classes.StyledThemeDefinition
import com.michaelflisar.toolbox.ui.adaptive.platform.LocalPlatform
import com.michaelflisar.toolbox.ui.adaptive.platform.Platform
import com.michaelflisar.toolbox.ui.adaptive.platform.PlatformDefaults
import com.michaelflisar.toolbox.ui.adaptive.runtime.InternalComposeStyledApi
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledColors
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledContentColor
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledPadding
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledRegistry
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledShapes
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledSizes
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledSpacing
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledTextStyle
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledThemeName
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledTypography
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledColors
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledPadding
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledShapes
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledSizes
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledSpacing
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledTypography
import com.michaelflisar.toolbox.ui.adaptive.tokens.lightStyledColors

object StyledTheme {

    enum class ContentColorFallback {
        BlackWhite,
        LocalContentColor
    }

    val platform: Platform
        @Composable @ReadOnlyComposable get() = LocalPlatform.current

    val themeName: String
        @Composable @ReadOnlyComposable get() = LocalStyledThemeName.current

    val colors: StyledColors
        @Composable @ReadOnlyComposable get() = LocalStyledColors.current

    val typography: StyledTypography
        @Composable @ReadOnlyComposable get() = LocalStyledTypography.current

    val shapes: StyledShapes
        @Composable @ReadOnlyComposable get() = LocalStyledShapes.current

    val padding: StyledPadding
        @Composable @ReadOnlyComposable get() = LocalStyledPadding.current

    val spacing: StyledSpacing
        @Composable @ReadOnlyComposable get() = LocalStyledSpacing.current

    val sizes: StyledSizes
        @Composable @ReadOnlyComposable get() = LocalStyledSizes.current

    // ----------------------
    // theme properties (locals, often times overridden locally inside components)
    // ----------------------

    val currentContentColor: Color
        @Composable @ReadOnlyComposable get() = LocalStyledContentColor.current

    val currentTextStyle: TextStyle
        @Composable @ReadOnlyComposable get() = LocalStyledTextStyle.current

    // ---------------------
    // functions
    // ---------------------

    /**
     * ATTENTION: inverse may not work well with white/black colors
     * as those are used as content colors quite often...
     *
     * because of this [checkInversely] is deactivated by default
     */
    @Composable
    @ReadOnlyComposable
    fun contentColorFor(
        backgroundColor: Color,
        checkInversely: Boolean = false,
        fallback: ContentColorFallback = ContentColorFallback.LocalContentColor,
    ): Color {
        val def = listOf(
            colors.backgroundDef,
            colors.surfaceDef,
            colors.surfaceVariantDef,
            colors.primaryDef,
            colors.primarySurfaceDef,
            colors.secondaryDef,
            colors.secondarySurfaceDef,
            colors.errorDef,
            colors.errorSurfaceDef,
            colors.successDef,
            colors.successSurfaceDef,
            colors.warningDef,
            colors.warningSurfaceDef
        )
        // first - check all definitions
        // if backgroundColor is a "color", return the corresponding "onColor"
        for (d in def) {
            if (backgroundColor == d.color) {
                return d.onColor
            }
        }
        // second - check inversely
        // if backgroundColor is an "onColor", return the corresponding "color"
        if (checkInversely) {
            for (d in def) {
                if (backgroundColor == d.onColor) {
                    return d.color
                }
            }
        }
        // fallback
        return when (fallback) {
            ContentColorFallback.BlackWhite -> if (backgroundColor.isDark) {
                Color.White
            } else {
                Color.Black
            }

            ContentColorFallback.LocalContentColor -> LocalStyledContentColor.current
        }
    }
}

/**
 * the main entry point to apply a StyledTheme to your app
 */
@Composable
fun StyledTheme(
    platform: Platform,
    theme: StyledThemeDefinition,
    colors: StyledColors = lightStyledColors(),
    typography: StyledTypography = PlatformDefaults.typography(platform),
    shapes: StyledShapes = PlatformDefaults.shapes(platform),
    paddings: StyledPadding = PlatformDefaults.paddings(platform),
    spacings: StyledSpacing = PlatformDefaults.spacings(platform),
    sizes: StyledSizes = PlatformDefaults.sizes(platform),
    content: @Composable () -> Unit,
) {
    // necessary because root may change the tree and we want to make sure that the content is not completely recomposed in this case
    val latestContent = rememberUpdatedState(content)
    val movableContent = remember {
        movableContentOf {
            latestContent.value()
        }
    }

    CompositionLocalProvider(
        LocalPlatform provides platform,
        // theme
        LocalStyledThemeName provides theme.name,
        LocalStyledRegistry provides theme.registry,
        // styles
        LocalStyledColors provides colors,
        LocalStyledTypography provides typography,
        LocalStyledShapes provides shapes,
        LocalStyledPadding provides paddings,
        LocalStyledSpacing provides spacings,
        LocalStyledTextStyle provides typography.bodyMedium,
        LocalStyledContentColor provides colors.onBackground,
        LocalStyledSizes provides sizes,
    ) {
        theme.root {
            movableContent()
        }
    }
}

/**
 * Provides a custom content color and text style for the content inside this composable.
 * If the contentColor or textStyle is null or unspecified, the current values will be used.
 */
@InternalComposeStyledApi
@Composable
fun ProvideCustomStyledContentLocals(
    contentColor: Color? = null,
    textStyle: TextStyle? = null,
    content: @Composable () -> Unit,
) {
    if ((contentColor == null || contentColor == Color.Unspecified) && textStyle == null) {
        content()
        return
    }

    CompositionLocalProvider(
        LocalStyledContentColor provides (contentColor ?: StyledTheme.currentContentColor),
        LocalStyledTextStyle provides (textStyle ?: StyledTheme.currentTextStyle),
        content = content
    )
}