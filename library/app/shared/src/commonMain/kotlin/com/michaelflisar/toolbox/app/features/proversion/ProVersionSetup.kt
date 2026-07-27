package com.michaelflisar.toolbox.app.features.proversion

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.actions.IBaseAction
import com.michaelflisar.toolbox.features.proversion.BaseAppProVersionManager
import com.michaelflisar.toolbox.features.proversion.ProState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class ProVersionSetup {

    abstract val supported: Boolean
    abstract val proState: StateFlow<ProState>

    data object NotSupported : ProVersionSetup() {
        override val supported = false
        private val _proState = MutableStateFlow(ProState.Yes)
        override val proState: StateFlow<ProState>
            get() = _proState
    }

    data class Supported(
        val manager: BaseAppProVersionManager,
        val action: @Composable () -> IBaseAction,
    ) : ProVersionSetup() {
        override val supported = true
        override val proState = manager.proState
    }

}