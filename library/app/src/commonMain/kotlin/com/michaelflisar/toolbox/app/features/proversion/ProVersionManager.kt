package com.michaelflisar.toolbox.app.features.proversion

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_info
import com.michaelflisar.toolbox.features.proversion.BaseAppProVersionManager
import org.jetbrains.compose.resources.StringResource

object ProVersionManager {

    private var _setup: ProVersionSetup = ProVersionSetup.NotSupported
    val setup: ProVersionSetup
        get() = _setup

    fun init(
        manager: BaseAppProVersionManager,
        action: @Composable () -> ActionItem.Action,
        proVersionInfoText: StringResource = Res.string.dlg_pro_version_info
    ) {
        _setup = ProVersionSetup.Supported(manager, action, proVersionInfoText)
    }
}