package com.michaelflisar.publicutilities.windowsapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.publicutilities.windowsapp.classes.AppTheme
import com.michaelflisar.publicutilities.windowsapp.classes.LocalAppState
import com.michaelflisar.publicutilities.windowsapp.settings.UISetting
import com.michaelflisar.publicutilities.windowsapp.ui.todo.MyCheckbox
import com.michaelflisar.publicutilities.windowsapp.ui.todo.MyDropdown
import com.michaelflisar.publicutilities.windowsapp.ui.todo.MyInput

@Composable
fun DesktopSettings(
    modifier: Modifier = Modifier,
    settings: List<UISetting<*, *>>
) {
    val appState = LocalAppState.current
    val settings = settings.filter { !it.internal }
    val itemModifier = Modifier.fillMaxWidth()
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.ITEM_SPACING)
    ) {
        settings
            .forEachIndexed { index, setting ->
                when (setting) {
                    is UISetting.Bool -> {
                        MyCheckbox(
                            modifier = itemModifier,
                            setting.label,
                            setting.getState(appState)
                        )
                    }

                    is UISetting.List -> {
                        MyDropdown<String>(
                            modifier = itemModifier,
                            title = setting.label,
                            items = setting.items,
                            mapper = { it },
                            selected = setting.getState(appState)
                        )
                    }

                    is UISetting.Text -> {
                        MyInput(modifier = itemModifier, setting.label, setting.getState(appState))
                    }

                    is UISetting.Integer -> {
                        MyInput(
                            modifier = itemModifier,
                            setting.label,
                            setting.getState(appState).value.toString()
                        ) {
                            setting.updateValue(appState, it.toIntOrNull() ?: 0)
                        }
                    }
                }
            }
    }
}