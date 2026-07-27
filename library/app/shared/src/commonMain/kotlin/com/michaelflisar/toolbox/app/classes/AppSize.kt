package com.michaelflisar.toolbox.app.classes

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize

data class AppSize(
    val width: Dp,
    val height: Dp,
    //val windowSizeClass: WindowSizeClass = WindowSizeClass.compute(width.value, height.value),
) {
    val landscape: Boolean
        get() = width > height
    val portrait: Boolean
        get() = !landscape
/*
    val isHeightCompact = windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
    val isWidthCompact = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT

    val isHeightMedium = windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.MEDIUM
    val isWidthMedium = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM

    val isHeightExpanded = windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.EXPANDED
    val isWidthExpanded = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

 */
}

@Composable
fun rememberAppSize(
    size: IntSize,
): AppSize {
    return AppSize(
        with(LocalDensity.current) { size.width.toDp() },
        with(LocalDensity.current) { size.height.toDp() },
        //currentWindowAdaptiveInfo().windowSizeClass
    )
}
