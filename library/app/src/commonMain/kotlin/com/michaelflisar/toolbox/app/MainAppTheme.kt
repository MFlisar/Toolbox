package com.michaelflisar.toolbox.app

import androidx.compose.runtime.Composable
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.StyledThemeConfig
import com.michaelflisar.composestyled.classes.StyledThemeProvider
import com.michaelflisar.composestyled.platform.Platform
import com.michaelflisar.composestyled.platform.PlatformDefaults
import com.michaelflisar.composestyled.platform.currentPlatform
import com.michaelflisar.composestyled.registry.StyledRegistry
import com.michaelflisar.composestyled.tokens.StyledPadding
import com.michaelflisar.composestyled.tokens.StyledShapes
import com.michaelflisar.composestyled.tokens.StyledSizes
import com.michaelflisar.composestyled.tokens.StyledSpacing
import com.michaelflisar.composestyled.tokens.StyledTypography
import com.michaelflisar.toolbox.app.features.preferences.rememberThemeConfig
import com.michaelflisar.toolbox.app.features.theme.themeProvider

@Composable
fun MainAppTheme(
    platform: Platform = currentPlatform(),
    provider: StyledThemeProvider = themeProvider(),
    config: StyledThemeConfig = rememberThemeConfig(provider),
    typography: StyledTypography = PlatformDefaults.typography(platform),
    shapes: StyledShapes = PlatformDefaults.shapes(platform),
    paddings: StyledPadding = PlatformDefaults.paddings(platform),
    spacings: StyledSpacing = PlatformDefaults.spacings(platform),
    sizes: StyledSizes = PlatformDefaults.sizes(platform),
    behavior: StyledTheme.Behavior = StyledTheme.Behavior(
        showScrollbars = PlatformDefaults.showScrollbars(platform),
    ),
    customization: (@Composable StyledRegistry.Builder.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    StyledTheme(
        platform = platform,
        provider = provider,
        config = config,
        typography = typography,
        shapes = shapes,
        paddings = paddings,
        spacings = spacings,
        sizes = sizes,
        behavior = behavior,
        customization = customization,
        content = content,
    )
}
