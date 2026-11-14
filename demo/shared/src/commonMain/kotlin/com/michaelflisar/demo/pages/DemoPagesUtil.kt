package com.michaelflisar.demo.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composestyled.ui.components.StyledIcon
import com.michaelflisar.composestyled.ui.components.StyledIconButton
import com.michaelflisar.composestyled.ui.components.StyledMenuBar
import com.michaelflisar.composestyled.ui.icons.StyledIcons
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.composestyled.ui.state.StyledExpandableTopBarScrollBehavior
import com.michaelflisar.toolbox.app.LocalAppActions
import com.michaelflisar.toolbox.app.features.navigation.ToolbarConfig

@Composable
fun DemoToolbarConfig(
    title: String,
    subtitle: String? = null,
    scrollBehavior: StyledExpandableTopBarScrollBehavior? = null,
) =
    ToolbarConfig(
        title = title,
        subtitle = subtitle,
        actions = { DemoSettingsToolbarAction() },
        scrollBehavior = scrollBehavior,
    )

@Composable
private fun DemoSettingsToolbarAction() {
    val appActions = LocalAppActions.current
    StyledIconButton(onClick = { appActions.showSettingsDialog() }) {
        StyledIcon(
            icon = Icons.Default.Settings.toIconComposable(),
            contentDescription = "Settings",
        )
    }
}

@Composable
fun DemoDesktopMenuBar() {
    val status = remember { mutableStateOf("Ready") }

    StyledMenuBar {
        menu("File") {
            item(
                "New",
                onClick = { status.value = "New document" },
                leadingIcon = { StyledIcon(icon = StyledIcons.Add, contentDescription = null) },
            )
            item(
                "Open...",
                onClick = { status.value = "Open document" },
                leadingIcon = { StyledIcon(icon = StyledIcons.Edit, contentDescription = null) },
            )
            divider()
            submenu("Export") {
                item(
                    "PDF",
                    onClick = { status.value = "Exported PDF" },
                    leadingIcon = {
                        StyledIcon(
                            icon = StyledIcons.Save,
                            contentDescription = null
                        )
                    },
                )
                submenu("More formats") {
                    item("PNG", onClick = { status.value = "Exported PNG" })
                    item("SVG", onClick = { status.value = "Exported SVG" })
                }
            }
        }

        menu(
            text = "Edit",
            icon = StyledIcons.Edit,
        ) {
            item(
                "Undo",
                onClick = { status.value = "Undo" },
                leadingIcon = {
                    StyledIcon(icon = StyledIcons.NavigateBack, contentDescription = null)
                },
            )
            item(
                "Redo",
                onClick = { status.value = "Redo" },
                leadingIcon = {
                    StyledIcon(icon = StyledIcons.NavigateForward, contentDescription = null)
                },
            )
            divider()
            submenu(
                text = "Find",
                leadingIcon = { StyledIcon(icon = StyledIcons.Search, contentDescription = null) },
            ) {
                item("Find", onClick = { status.value = "Find" })
                item("Replace", onClick = { status.value = "Replace" })
            }
        }

        item(
            "New",
            onClick = { status.value = "New document" },
            icon = StyledIcons.Add,
        )
        item("Save", onClick = { status.value = "Saved" }, icon = StyledIcons.Save)
        custom { Spacer(Modifier.weight(1f)) }
        custom { StyledText(text = status.value, style = StyledTheme.typography.labelMedium) }
    }
}