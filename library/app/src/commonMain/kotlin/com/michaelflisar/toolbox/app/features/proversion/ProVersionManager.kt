package com.michaelflisar.toolbox.app.features.proversion

import androidx.compose.runtime.Composable
import com.michaelflisar.composestyled.ui.components.StyledPopupMenuScope
import com.michaelflisar.composestyled.ui.state.VisibilityDialogState
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
        menuItem: @Composable StyledPopupMenuScope.() -> Unit,
    ) {
        _setup = ProVersionSetup.Supported(manager, menuItem)
        manager.configure()
    }

    @Composable
    fun Paywall(showProVersionDialog: VisibilityDialogState) {
        if (setup.supported && showProVersionDialog.visible) {
            (setup as? ProVersionSetup.Supported)?.manager?.PaywallScreen(showProVersionDialog)
        }
    }
}