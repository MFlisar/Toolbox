package com.michaelflisar.toolbox.feature.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.unit.round

@Composable
fun PopupMenu(
    state: MenuState,
    modifier: Modifier = Modifier,
    setup: MenuSetup = rememberMenuSetup(),
    content: @Composable MenuScope.() -> Unit,
) {
    if (!state.isShowing)
        return

    ProvideMenuLocals(
        state = state,
        setup = setup,
        level = -1 // we still outside of the menu
    ) {
        DropdownMenu(
            modifier = modifier
                .onGloballyPositioned {
                    state.offset.value = it.positionOnScreen().round()
                }
            // springt damit falls sich die Position des s durch die sich ändernde Größe verändert!
            //.animateContentSize()
            ,
            expanded = true,
            onDismissRequest = { state.hide() },
            offset = setup.offset,
            shape = MaterialTheme.shapes.small
        ) {
            ProvideUpdatedMenuLocals(emptyList(), 0) {
                with(MenuScopeInstance) {
                    content()
                }
            }
        }
    }
}