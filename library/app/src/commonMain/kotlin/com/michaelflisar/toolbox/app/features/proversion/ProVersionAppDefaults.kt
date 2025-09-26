package com.michaelflisar.toolbox.app.features.proversion

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.actions.ActionItem
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.menu_go_pro
import com.michaelflisar.toolbox.drawables.Crown
import com.michaelflisar.toolbox.extensions.toIconComposable
import org.jetbrains.compose.resources.stringResource

object ProVersionAppDefaults {

    fun actionItem() = @Composable {
        val appState = LocalAppState.current
        ActionItem.Action(
            title = stringResource(Res.string.menu_go_pro),
            icon = Crown.toIconComposable(),
            action = {
                appState.showProVersionDialog.show()
            }
        )
    }


}