package com.michaelflisar.toolbox.app.features.debugdrawer

import androidx.compose.runtime.compositionLocalOf
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.toolbox.app.features.appstate.AppState

val LocalDebugDrawerState = compositionLocalOf<DebugDrawerState> { throw RuntimeException("DebugDrawerState not initialised!") }