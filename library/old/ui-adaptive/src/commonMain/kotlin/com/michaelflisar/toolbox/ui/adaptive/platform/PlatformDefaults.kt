package com.michaelflisar.toolbox.ui.adaptive.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledPadding
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledShapes
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledSizes
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledSpacing
import com.michaelflisar.toolbox.ui.adaptive.tokens.StyledTypography

object PlatformDefaults {

    @Composable
    fun typography(platform: Platform): StyledTypography {
        return remember(platform) {
            Defaults.find(platform).typography
        }
    }

    @Composable
    fun shapes(platform: Platform): StyledShapes {
        return remember(platform) {
            Defaults.find(platform).shapes
        }
    }

    @Composable
    fun paddings(platform: Platform): StyledPadding {
        return remember(platform) {
            Defaults.find(platform).paddings
        }
    }

    @Composable
    fun spacings(platform: Platform): StyledSpacing {
        return remember(platform) {
            Defaults.find(platform).spacings
        }
    }

    @Composable
    fun sizes(platform: Platform): StyledSizes {
        return remember(platform) {
            Defaults.find(platform).sizes
        }
    }

    @Stable
    data class Defaults(
        val paddings: StyledPadding,
        val spacings: StyledSpacing,
        val sizes: StyledSizes,
        val typography: StyledTypography,
        val shapes: StyledShapes,
    ) {
        companion object {
            fun find(platform: Platform) = when (platform) {
                Platform.Android -> Android
                Platform.IOS -> IOS
                is Platform.Desktop -> when (platform.os) {
                    Platform.Desktop.OS.Windows,
                    Platform.Desktop.OS.Unknown,
                        -> Windows

                    Platform.Desktop.OS.Mac -> Mac
                }

                Platform.Web -> Web
            }
        }
    }

    internal object Mobile {

        val Paddings = StyledPadding(
            small = 4.dp,
            medium = 8.dp,
            large = 16.dp,
            content = 16.dp,
            cardContent = 8.dp
        )

        val Spacings = StyledSpacing(
            small = 4.dp,
            medium = 8.dp,
            large = 16.dp,
        )

        val Shapes = StyledShapes.create(
            listOf(4.dp, 8.dp, 12.dp, 16.dp, 28.dp)
        )
    }

    internal object Desktop {

        val Paddings = StyledPadding(
            small = 2.dp,
            medium = 6.dp,
            large = 12.dp,
            content = 12.dp,
            cardContent = 8.dp
        )

        val Spacings = StyledSpacing(
            small = 2.dp,
            medium = 6.dp,
            large = 12.dp
        )

        val Sizes = StyledSizes(
            minimumInteractiveSmallSize = 20.dp, // HIG – other platforms don't specify a value
            minimumInteractiveSize = 28.dp, // HIG – other platforms don't specify a value
            iconSize = 20.dp
        )

        val Shapes = StyledShapes.create(
            listOf(2.dp, 4.dp, 6.dp, 8.dp, 12.dp)
        )

    }

    val Android = Defaults(
        paddings = Mobile.Paddings,
        spacings = Mobile.Spacings,
        sizes = StyledSizes(
            minimumInteractiveSmallSize = 32.dp,  // the size of a chip in material 3
            minimumInteractiveSize = 48.dp, // M3
            iconSize = 24.dp
        ),
        typography = StyledTypography.create(
            headline = listOf(32, 28, 24),
            title = listOf(22, 16, 14),
            body = listOf(16, 14, 12),
            label = listOf(14, 12, 11),
            headlineFontWeight = FontWeight.Normal,
            titleFontWeight = FontWeight.Medium,
            bodyFontWeight = FontWeight.Normal,
            labelFontWeight = FontWeight.Medium,
        ).let {
            it.copy(titleLarge = it.titleLarge.copy(fontWeight = FontWeight.Normal))
        },
        shapes = Mobile.Shapes
    )

    val IOS = Defaults(
        paddings = Mobile.Paddings,
        spacings = Mobile.Spacings,
        sizes = StyledSizes(
            minimumInteractiveSmallSize = 28.dp, // HIG
            minimumInteractiveSize = 44.dp, // HIG
            iconSize = 24.dp
        ),
        typography = StyledTypography.create(
            headline = listOf(34, 28, 22),
            title = listOf(20, 17, 16),
            body = listOf(19, 17, 15),
            label = listOf(13, 12, 11),
            headlineFontWeight = FontWeight.Normal,
            titleFontWeight = FontWeight.Normal,
            bodyFontWeight = FontWeight.Normal,
            labelFontWeight = FontWeight.Normal,
        ).let {
            it.copy(titleMedium = it.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        },
        shapes = Mobile.Shapes
    )

    val Windows = Defaults(
        paddings = Desktop.Paddings,
        spacings = Desktop.Spacings,
        sizes = Desktop.Sizes,
        typography = StyledTypography.create(
            headline = listOf(40, 32, 28),
            title = listOf(24, 20, 16),
            body = listOf(16, 14, 12),
            label = listOf(14, 12, 12),
            headlineFontWeight = FontWeight.SemiBold,
            titleFontWeight = FontWeight.SemiBold,
            bodyFontWeight = FontWeight.Normal,
            labelFontWeight = FontWeight.Normal,
        ),
        shapes = Desktop.Shapes
    )

    val Mac = Defaults(
        paddings = Desktop.Paddings,
        spacings = Desktop.Spacings,
        sizes = Desktop.Sizes,
        typography = StyledTypography.create(
            headline = listOf(34, 28, 22),
            title = listOf(20, 17, 16),
            body = listOf(15, 13, 11),
            label = listOf(13, 12, 11),
            headlineFontWeight = FontWeight.Normal,
            titleFontWeight = FontWeight.Normal,
            bodyFontWeight = FontWeight.Normal,
            labelFontWeight = FontWeight.Normal,
        ).let {
            it.copy(titleMedium = it.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        },
        shapes = Desktop.Shapes
    )

    val Web = Defaults(
        paddings = Desktop.Paddings,
        spacings = Desktop.Spacings,
        sizes = Desktop.Sizes,
        typography = StyledTypography.create(
            headline = listOf(36, 30, 24),
            title = listOf(22, 20, 18),
            body = listOf(18, 16, 14),
            label = listOf(14, 13, 12),
            headlineFontWeight = FontWeight.Bold,
            titleFontWeight = FontWeight.Bold,
            bodyFontWeight = FontWeight.Normal,
            labelFontWeight = FontWeight.Normal,
        ),
        shapes = Desktop.Shapes
    )
}