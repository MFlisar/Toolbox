package com.michaelflisar.toolbox.ui.adaptive.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme.ContentColorFallback
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme.colors
import com.michaelflisar.toolbox.ui.adaptive.classes.StyledLocalMissingException
import com.michaelflisar.toolbox.ui.adaptive.isDark

@Immutable
data class StyledColors(
    val isDarkTheme: Boolean,

    val backgroundDef: Definition,
    val surfaceDef: Definition,

    // "Neutral Variant" (z.B. für chips / cards / inputs)
    val surfaceVariantDef: Definition,

    // Brand
    val primaryDef: Definition,
    val primarySurfaceDef: Definition,
    val secondaryDef: Definition,
    val secondarySurfaceDef: Definition,

    // Lines
    val outline: Color,
    val outlineVariant: Color,

    // Status
    val errorDef: Definition,
    val errorSurfaceDef: Definition,
    val successDef: Definition,
    val successSurfaceDef: Definition,
    val warningDef: Definition,
    val warningSurfaceDef: Definition,

    // Disable handling
    val disableFactors: StyledDisableFactors,
) {
    @Immutable
    data class Definition(
        val color: Color,
        val onColor: Color,
    )

    // ------------------
    // direct colors accessor for convenience
    // ------------------

    val background: Color get() = backgroundDef.color
    val onBackground: Color get() = backgroundDef.onColor

    val surface: Color get() = surfaceDef.color
    val onSurface: Color get() = surfaceDef.onColor

    val surfaceVariant: Color get() = surfaceVariantDef.color
    val onSurfaceVariant: Color get() = surfaceVariantDef.onColor

    val primary: Color get() = primaryDef.color
    val onPrimary: Color get() = primaryDef.onColor

    val primarySurface: Color get() = primarySurfaceDef.color
    val onPrimarySurface: Color get() = primarySurfaceDef.onColor

    val secondary: Color get() = secondaryDef.color
    val onSecondary: Color get() = secondaryDef.onColor

    val secondarySurface: Color get() = secondarySurfaceDef.color
    val onSecondarySurface: Color get() = secondarySurfaceDef.onColor

    val error: Color get() = errorDef.color
    val onError: Color get() = errorDef.onColor

    val errorSurface: Color get() = errorSurfaceDef.color
    val onErrorSurface: Color get() = errorSurfaceDef.onColor

    val success: Color get() = successDef.color
    val onSuccess: Color get() = successDef.onColor

    val successSurface: Color get() = successSurfaceDef.color
    val onSuccessSurface: Color get() = successSurfaceDef.onColor

    val warning: Color get() = warningDef.color
    val onWarning: Color get() = warningDef.onColor

    val warningSurface: Color get() = warningSurfaceDef.color
    val onWarningSurface: Color get() = warningSurfaceDef.onColor
}



fun lightStyledColors() = StyledColors(
    isDarkTheme = false,
    backgroundDef = StyledColors.Definition(Color(0xFFFFFBFE), Color(0xFF1C1B1F)),
    surfaceDef = StyledColors.Definition(Color(0xFFFFFBFE), Color(0xFF1C1B1F)),
    surfaceVariantDef = StyledColors.Definition(Color(0xFFE7E0EC), Color(0xFF49454F)),

    primaryDef = StyledColors.Definition(Color(0xFF2F6BFF), Color(0xFFFFFFFF)),
    primarySurfaceDef = StyledColors.Definition(Color(0xFFDCE3FF), Color(0xFF00154C)),

    secondaryDef = StyledColors.Definition(Color(0xFF455D7A), Color(0xFFFFFFFF)),
    secondarySurfaceDef = StyledColors.Definition(Color(0xFFDCE8F7), Color(0xFF142238)),

    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),

    errorDef = StyledColors.Definition(Color(0xFFB3261E), Color(0xFFFFFFFF)),
    errorSurfaceDef = StyledColors.Definition(Color(0xFFF9DEDC), Color(0xFF410E0B)),

    successDef = StyledColors.Definition(Color(0xFF2E7D32), Color(0xFFFFFFFF)),
    successSurfaceDef = StyledColors.Definition(Color(0xFFC8E6C9), Color(0xFF003908)),

    warningDef = StyledColors.Definition(Color(0xFFB26A00), Color(0xFFFFFFFF)),
    warningSurfaceDef = StyledColors.Definition(Color(0xFFFFE0B2), Color(0xFF402A00)),

    disableFactors = lightDisableFactors()
)

fun darkStyledColors() = StyledColors(
    isDarkTheme = true,
    backgroundDef = StyledColors.Definition(Color(0xFF1C1B1F), Color(0xFFE6E1E5)),
    surfaceDef = StyledColors.Definition(Color(0xFF1C1B1F), Color(0xFFE6E1E5)),
    surfaceVariantDef = StyledColors.Definition(Color(0xFF49454F), Color(0xFFCAC4D0)),

    primaryDef = StyledColors.Definition(Color(0xFF7AA2FF), Color(0xFF001A43)),
    primarySurfaceDef = StyledColors.Definition(Color(0xFF253C59), Color(0xFFCFE0FF)),

    secondaryDef = StyledColors.Definition(Color(0xFFB7C9E4), Color(0xFF26364D)),
    secondarySurfaceDef = StyledColors.Definition(Color(0xFF354A65), Color(0xFFDCE8F7)),

    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),

    errorDef = StyledColors.Definition(Color(0xFFF2B8B5), Color(0xFF601410)),
    errorSurfaceDef = StyledColors.Definition(Color(0xFF8C1D18), Color(0xFFF9DEDC)),

    successDef = StyledColors.Definition(Color(0xFF81C784), Color(0xFF003908)),
    successSurfaceDef = StyledColors.Definition(Color(0xFF145A22), Color(0xFFC8E6C9)),

    warningDef = StyledColors.Definition(Color(0xFFFFC470), Color(0xFF402A00)),
    warningSurfaceDef = StyledColors.Definition(Color(0xFF5A3A00), Color(0xFFFFE0B2)),

    disableFactors = darkDisableFactors()
)

internal val LocalStyledColors =
    staticCompositionLocalOf<StyledColors> { throw StyledLocalMissingException("LocalStyledColors") }
