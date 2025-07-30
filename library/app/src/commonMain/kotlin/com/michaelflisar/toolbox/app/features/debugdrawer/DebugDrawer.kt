package com.michaelflisar.toolbox.app.features.debugdrawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.composedebugdrawer.core.composables.DebugDrawerButton
import com.michaelflisar.composedebugdrawer.core.composables.DebugDrawerCheckbox
import com.michaelflisar.composedebugdrawer.core.composables.DebugDrawerDropdown
import com.michaelflisar.composedebugdrawer.core.composables.DebugDrawerRegion
import com.michaelflisar.composedebugdrawer.core.composables.DebugDrawerSegmentedButtons
import com.michaelflisar.composedebugdrawer.plugin.kotpreferences.getDebugLabel
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogState
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.styles.ModernStyle
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.picker.composables.ThemeColorPreview
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.Constants
import com.michaelflisar.toolbox.app.debug.DebugPrefs
import com.michaelflisar.toolbox.app.features.logging.LogManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal expect val supportsBuildAndDeviceInfos: Boolean

@Composable
internal expect fun DebugDrawerBuildInfos(
    drawerState: DebugDrawerState,
    scope: CoroutineScope,
    prefs: DebugPrefs
)

@Composable
internal expect fun DebugDrawerLumberjack(
    drawerState: DebugDrawerState,
    setup: IFileLoggingSetup,
    mailReceiver: String
)

@Composable
internal expect fun DebugDrawerDeviceInfos(drawerState: DebugDrawerState)

@Composable
fun DebugDrawer(
    drawerState: DebugDrawerState,
    customContent: @Composable (drawerState: DebugDrawerState) -> Unit = {}
) {
    val setup = CommonApp.setup
    val scope = rememberCoroutineScope()
    val debugPrefs = setup.debugPrefs

    LaunchedEffect(Unit) {
        snapshotFlow { drawerState.expandedIds() }
            .collect { ids ->
                scope.launch {
                    debugPrefs.debugDrawerExpandedIds.update(ids.toSet())
                }
            }
    }

    val showRegionInformations by debugPrefs.showRegionInformations.collectAsStateNotNull()
    val showRegionDevice by debugPrefs.showRegionDevice.collectAsStateNotNull()
    val showRegionThemes by debugPrefs.showRegionThemes.collectAsStateNotNull()
    val showRegionLogging by debugPrefs.showRegionLogging.collectAsStateNotNull()
    val showRegionData by debugPrefs.showRegionData.collectAsStateNotNull()

    val dialogSettings = rememberDialogState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Settings", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.weight(1f))
        IconButton({
            dialogSettings.show()
        }) {
            Icon(Icons.Default.Settings, null)
        }
    }


    // Informationen
    if (showRegionInformations) {
        DebugDrawerBuildInfos(drawerState = drawerState, scope = scope, prefs = debugPrefs)
    }

    // Device
    if (showRegionDevice) {
        DebugDrawerDeviceInfos(drawerState = drawerState)
    }

    // Theme - TODO: theme picker for desktop is wrong!
    if (showRegionThemes) {
        DebugDrawerRegion(
            image = { Icon(Icons.Default.Style, null) },
            label = "Theme",
            id = "style",
            collapsible = true,
            drawerState = drawerState
        ) {
            val themes = ComposeTheme.getRegisteredThemes()
            val theme = setup.prefs.theme.collectAsStateNotNull()
            val contrast = setup.prefs.contrast.collectAsStateNotNull()
            val dynamicTheme = setup.prefs.dynamicTheme.collectAsStateNotNull()
            val customTheme = setup.prefs.customTheme.collectAsStateNotNull()

            DebugDrawerSegmentedButtons(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                //icon = Icons.Outlined.ColorLens,
                selected = theme.value,
                items = ComposeTheme.BaseTheme.entries,
                labelProvider = { it.name },
                onItemSelected = {
                    scope.launch {
                        setup.prefs.theme.update(it)
                    }
                }
            )
            DebugDrawerCheckbox(
                label = "Dynamic Theme",
                checked = dynamicTheme.value,
                onCheckedChange = {
                    scope.launch {
                        setup.prefs.dynamicTheme.update(it)
                    }
                }
            )
            AnimatedVisibility(visible = !dynamicTheme.value) {
                DebugDrawerDropdown(
                    label = "Contrast",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    selected = contrast.value,
                    items = ComposeTheme.Contrast.entries,
                    labelProvider = { it.name },
                    iconProvider = {
                        val icon = when (it) {
                            ComposeTheme.Contrast.Normal -> Icons.Default.BrightnessLow
                            ComposeTheme.Contrast.Medium -> Icons.Default.BrightnessMedium
                            ComposeTheme.Contrast.High -> Icons.Default.BrightnessHigh
                            ComposeTheme.Contrast.System -> Icons.Default.PhoneAndroid
                        }
                        Icon(icon, null)
                    },
                    onItemSelected = {
                        scope.launch {
                            setup.prefs.contrast.update(it)
                        }
                    }
                )
            }
            AnimatedVisibility(visible = !dynamicTheme.value) {
                DebugDrawerDropdown(
                    label = "Theme",
                    //image = { Icon(Icons.Outlined.Style, null) },
                    selected = customTheme.value,
                    items = themes.map { it.id },
                    iconProvider = { item ->
                        themes.find { it.id == item }?.let {
                            val colorScheme =
                                it.getScheme(theme.value.isDark(), ComposeTheme.BaseContrast.Normal)
                            ThemeColorPreview(colorScheme)
                        }
                        //Image(
                        //    modifier = Modifier.size(24.dp),
                        //    painter = painter,
                        //    contentDescription = null
                        //)
                    },
                    labelProvider = { item ->
                        themes.find { it.id == item }?.fullName ?: item
                    },
                    onItemSelected = {
                        scope.launch {
                            setup.prefs.customTheme.update(it)
                        }
                    }
                )
            }
        }
    }

    // Custom
    customContent(drawerState)

    // Logging
    val fileLogger = setup.fileLogger
    if (showRegionLogging && fileLogger != null) {
        // TODO: funktioniert das auf platformen != android? intern wird nämlich feedback genutzt!!!
        // + setup sollte nullable sein
        DebugDrawerLumberjack(
            drawerState = drawerState,
            setup = fileLogger.setup,
            mailReceiver = Constants.DEVELOPER_EMAIL
        )
    }

    // Daten
    if (showRegionData && LogManager.sendRelevantFiles != null) {
        DebugDrawerRegion(
            image = { Icon(Icons.Default.InsertDriveFile, null) },
            label = "Data",
            id = "data",
            collapsible = true,
            drawerState = drawerState
        ) {
            DebugDrawerButton(
                image = { Icon(Icons.Default.Mail, null) },
                label = "Send relevant app files"
            ) {
                scope.launch {
                    LogManager.sendRelevantFiles.invoke()
                }
            }
        }
    }

    if (dialogSettings.visible) {
        DialogSettings(dialogSettings)
    }
}

@Composable
private fun DialogSettings(state: DialogState) {

    val setup = CommonApp.setup
    val debugPrefs = setup.debugPrefs

    Dialog(
        state = state,
        title = { Text("Visible Regions") }
    ) {
        PreferenceScreen(
            settings = PreferenceSettingsDefaults.settings(
                toggleBooleanOnItemClick = true,
                style = ModernStyle.create(
                    outerPadding = PaddingValues(horizontal = 0.dp),
                    innerPadding = PaddingValues(horizontal = 16.dp, 2.dp)
                )
            ),
        ) {
            listOfNotNull(
                debugPrefs.showRegionInformations.takeIf { supportsBuildAndDeviceInfos },
                debugPrefs.showRegionDevice.takeIf { supportsBuildAndDeviceInfos },
                debugPrefs.showRegionThemes,
                debugPrefs.showRegionLogging.takeIf { setup.fileLogger != null },
                debugPrefs.showRegionData
            ).forEach {
                PreferenceBool(
                    value = it.asMutableStateNotNull(),
                    style = PreferenceBool.Style.Checkbox,
                    title = it.getDebugLabel().replace("Show Region ", ""),
                    icon = {
                        Icon(
                            Icons.Outlined.Visibility,
                            contentDescription = null
                        )
                    },
                    //itemStyle = LocalPreferenceSettings.current.style.defaultItemStyle.copy(
                    //    outerPadding = PaddingValues()
                    //)
                )
            }
        }
    }
}

