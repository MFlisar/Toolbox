package com.michaelflisar.toolbox.app.features.proversion

import kotlinx.coroutines.flow.StateFlow

interface BaseAppProVersionManager {
    val supportsProVersion: Boolean
    val proState: StateFlow<ProState>
    suspend fun checkProVersion(): ProState
}