package com.michaelflisar.toolbox.app.features.proversion

import androidx.compose.runtime.Composable
import com.michaelflisar.composedialogs.core.DialogStateNoData
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.features.proversion.BaseAppProVersionManager
import com.michaelflisar.toolbox.features.proversion.ProState
import kotlinx.coroutines.flow.StateFlow

object ProVersionManager {

    private var _setup: ProVersionSetup = ProVersionSetup.NotSupported
    val setup: ProVersionSetup
        get() = _setup

    val proState: StateFlow<ProState>
        get() = setup.proState

    fun init(
        manager: BaseAppProVersionManager,
        action: @Composable () -> ActionItem.Action,
    ) {
        _setup = ProVersionSetup.Supported(manager, action)
        manager.configure()
    }

    @Composable
    fun Paywall(showProVersionDialog: DialogStateNoData) {
        if (setup.supported && showProVersionDialog.visible)
            (setup as? ProVersionSetup.Supported)?.manager?.PaywallScreen(showProVersionDialog)
    }
}