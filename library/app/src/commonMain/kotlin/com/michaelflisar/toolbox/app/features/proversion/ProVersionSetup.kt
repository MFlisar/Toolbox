package com.michaelflisar.toolbox.app.features.proversion

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_info
import com.michaelflisar.toolbox.features.proversion.BaseAppProVersionManager
import com.michaelflisar.toolbox.features.proversion.ProState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource

sealed class ProVersionSetup {

    abstract val supported: Boolean
    abstract val proState: StateFlow<ProState>

    abstract suspend fun checkProVersion(): ProState

    data object NotSupported : ProVersionSetup() {
        override val supported = false
        private val _proState = MutableStateFlow(ProState.Yes)
        override val proState: StateFlow<ProState>
            get() = _proState

        override suspend fun checkProVersion() = proState.value
    }

    data class Supported(
        val manager: BaseAppProVersionManager,
        val action: @Composable () -> ActionItem.Action,
        val proVersionInfoText: StringResource = Res.string.dlg_pro_version_info
    ) : ProVersionSetup() {
        override val supported = true
        override val proState = manager.proState
        override suspend fun checkProVersion(): ProState = manager.checkProVersion()
    }

}