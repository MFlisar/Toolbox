package com.michaelflisar.toolbox.app.features.proversion

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import kotlinx.coroutines.flow.StateFlow

interface BaseAppProVersionManager {
    val supportsProVersion: Boolean
    val proState: StateFlow<ProState>
    suspend fun checkProVersion(): ProState

    @Composable
    fun actionProVersion(): ActionItem.Action?
}