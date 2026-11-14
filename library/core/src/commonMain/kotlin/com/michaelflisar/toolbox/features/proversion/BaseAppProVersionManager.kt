package com.michaelflisar.toolbox.features.proversion

import androidx.compose.runtime.Composable
import com.michaelflisar.composestyled.ui.state.VisibilityDialogState
import kotlinx.coroutines.flow.StateFlow

interface BaseAppProVersionManager {

    val proState: StateFlow<ProState>

    fun configure()
    suspend fun checkProVersion(): ProState

    @Composable
    fun PaywallScreen(dialogState: VisibilityDialogState)
}