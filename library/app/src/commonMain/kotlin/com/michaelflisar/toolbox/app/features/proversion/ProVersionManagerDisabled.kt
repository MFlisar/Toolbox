package com.michaelflisar.toolbox.app.features.proversion

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ProVersionManagerDisabled : BaseAppProVersionManager {

    override val supportsProVersion = false

    private val _proState = MutableStateFlow(ProState.Yes)
    override val proState: StateFlow<ProState>
        get() = _proState

    override suspend fun checkProVersion(): ProState {
        return ProState.Yes
    }

    @Composable
    override fun actionProVersion(): ActionItem.Action? = null
}