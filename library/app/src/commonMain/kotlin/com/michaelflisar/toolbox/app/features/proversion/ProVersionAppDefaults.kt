package com.michaelflisar.toolbox.app.features.proversion

import androidx.compose.runtime.Composable
import com.michaelflisar.toolbox.app.features.actions.IBaseAction
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.menu_go_pro
import com.michaelflisar.toolbox.drawables.Crown
import com.michaelflisar.toolbox.extensions.toIconComposable
import org.jetbrains.compose.resources.stringResource

object ProVersionAppDefaults {

    @Composable
    fun actionItem(): IBaseAction {
        val appState = LocalAppState.current
        return MenuItem.Item(
            text = stringResource(Res.string.menu_go_pro),
            icon = Crown.toIconComposable(),
            onClick = { appState.showProVersionDialog.show() }
        )
    }

}