package com.michaelflisar.toolbox.app.features.proversion

import androidx.compose.runtime.Composable
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composestyled.ui.components.StyledIcon
import com.michaelflisar.composestyled.ui.components.StyledPopupMenuScope
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.toolbox.app.LocalAppActions
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.menu_go_pro
import com.michaelflisar.toolbox.icons.Crown
import com.michaelflisar.toolbox.icons.MyIcons
import org.jetbrains.compose.resources.stringResource

object ProVersionAppDefaults {

    @Composable
    fun menuItem(scope: StyledPopupMenuScope) {
        val appState = LocalAppActions.current
        scope.item(
            text = { StyledText(stringResource(Res.string.menu_go_pro)) },
            leadingIcon = { StyledIcon(MyIcons.Crown.toIconComposable()) },
            onClick = { appState.showProVersionDialog() }
        )
    }

}