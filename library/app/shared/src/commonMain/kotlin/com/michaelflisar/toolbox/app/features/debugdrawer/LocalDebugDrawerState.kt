package com.michaelflisar.toolbox.app.features.debugdrawer

import androidx.compose.runtime.compositionLocalOf
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState

val LocalDebugDrawerState = compositionLocalOf<DebugDrawerState> { throw RuntimeException("DebugDrawerState not initialised!") }