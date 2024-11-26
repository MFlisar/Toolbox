package com.michaelflisar.toolbox.windowsapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.composables.MyDropdown
import com.michaelflisar.toolbox.ToolboxDefaults
import com.michaelflisar.toolbox.windowsapp.tmp.UISetting
import com.michaelflisar.toolbox.composables.MyCheckbox
import com.michaelflisar.toolbox.composables.MyInput
import com.michaelflisar.toolbox.composables.MyNumericInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO:
// durch ComposePreferences ersetzen sobald dies auch zu KMP konvertiert wurde!
@Composable
fun DesktopSettings(
    modifier: Modifier = Modifier,
    settings: List<UISetting<*>>
) {
    val itemModifier = Modifier.fillMaxWidth()
    val scope = rememberCoroutineScope()
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
    ) {
        settings.forEach { setting ->
            when (setting) {
                is UISetting.Bool -> {
                    MyCheckbox(
                        modifier = itemModifier,
                        title = setting.label,
                        checked = setting.setting.value,
                        onCheckedChange = {
                            scope.launch(Dispatchers.IO) {
                                setting.setting.update(it)
                            }
                        }
                    )
                }

                is UISetting.List -> {
                    MyDropdown(
                        modifier = itemModifier,
                        title = setting.label,
                        items = setting.items,
                        mapper = { it },
                        selected = setting.setting.value,
                        onSelectionChanged = {
                            scope.launch(Dispatchers.IO) {
                                setting.setting.update(it)
                            }
                        }
                    )
                }

                is UISetting.Text -> {
                    MyInput(
                        modifier = itemModifier,
                        title = setting.label,
                        value = setting.setting.value,
                        onValueChange = {
                            scope.launch(Dispatchers.IO) {
                                setting.setting.update(it)
                            }
                        }
                    )
                }

                is UISetting.Integer -> {
                    MyNumericInput(
                        modifier = itemModifier,
                        title = setting.label,
                        value = setting.setting.value,
                        onValueChange = {
                            if (it != null) {
                                scope.launch(Dispatchers.IO) {
                                    setting.setting.update(it)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}