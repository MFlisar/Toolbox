package com.michaelflisar.toolbox.app.features.preferences

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.More
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.HideSource
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedebugdrawer.core.DebugDrawerState
import com.michaelflisar.composedialogs.core.DialogButton
import com.michaelflisar.composedialogs.core.DialogButtonType
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogEvent
import com.michaelflisar.composedialogs.core.DialogStateNoData
import com.michaelflisar.composedialogs.core.rememberDialogState
import com.michaelflisar.composedialogs.dialogs.info.DialogInfo
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.PreferenceSection
import com.michaelflisar.composepreferences.core.PreferenceSubScreen
import com.michaelflisar.composepreferences.core.classes.PreferenceSettings
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.classes.PreferenceState
import com.michaelflisar.composepreferences.core.classes.rememberPreferenceState
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.composepreferences.core.styles.ModernStyle
import com.michaelflisar.composepreferences.kotpreferences.asDependency
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.backup.ContentPreferencesAsSubPreference
import com.michaelflisar.toolbox.app.features.logging.LogManager
import com.michaelflisar.toolbox.app.features.preferences.groups.SettingsHeader
import com.michaelflisar.toolbox.app.features.preferences.groups.SettingsHeaderButtons
import com.michaelflisar.toolbox.app.features.preferences.groups.SettingsProVersionHeader
import com.michaelflisar.toolbox.app.platform.kill
import com.michaelflisar.toolbox.app.platform.localContext
import com.michaelflisar.toolbox.app.platform.restart
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.no
import com.michaelflisar.toolbox.core.resources.settings_change_language
import com.michaelflisar.toolbox.core.resources.settings_changelog
import com.michaelflisar.toolbox.core.resources.settings_dialog_attach_log_file_label
import com.michaelflisar.toolbox.core.resources.settings_dialog_attach_log_file_text
import com.michaelflisar.toolbox.core.resources.settings_feedback
import com.michaelflisar.toolbox.core.resources.settings_feedback_details
import com.michaelflisar.toolbox.core.resources.settings_group_about
import com.michaelflisar.toolbox.core.resources.settings_group_language
import com.michaelflisar.toolbox.core.resources.settings_group_others
import com.michaelflisar.toolbox.core.resources.settings_group_others_details
import com.michaelflisar.toolbox.core.resources.settings_group_privacy
import com.michaelflisar.toolbox.core.resources.settings_open_play_store
import com.michaelflisar.toolbox.core.resources.settings_open_play_store_details
import com.michaelflisar.toolbox.core.resources.settings_privacy_policy
import com.michaelflisar.toolbox.core.resources.settings_privacy_policy_details
import com.michaelflisar.toolbox.core.resources.yes
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.GooglePlay
import compose.icons.fontawesomeicons.solid.Crown
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal expect fun LumberjackDialog(
    visible: MutableState<Boolean>,
    title: String,
    setup: IFileLoggingSetup,
)

@Composable
fun BaseAppPreferences(
    modifier: Modifier = Modifier,
    showProVersionDialog: DialogStateNoData = rememberDialogState(),
    buttons: List<SettingsHeaderButtons.Button> = emptyList(),
    preferenceState: PreferenceState = rememberPreferenceState(),
    handleBackPress: Boolean,
    customContent: @Composable PreferenceGroupScope.() -> Unit,
) {

    val settings = PreferenceSettingsDefaults.settings(
        toggleBooleanOnItemClick = true,
        style = ModernStyle.create()
    )

    SettingsContent(
        modifier,
        settings,
        preferenceState,
        handleBackPress,
        showProVersionDialog,
        buttons,
        customContent
    )

}

@Composable
internal fun SettingsContent(
    modifier: Modifier,
    settings: PreferenceSettings,
    state: PreferenceState,
    handleBackPress: Boolean,
    showProVersionDialog: DialogStateNoData,
    buttons: List<SettingsHeaderButtons.Button> = emptyList(),
    customContent: @Composable() (PreferenceGroupScope.() -> Unit),
) {
    val setup = CommonApp.setup
    val appState = LocalAppState.current
    val debugPrefs = setup.debugPrefs

    val showLogFile = rememberSaveable { mutableStateOf(false) }
    val showAttachLogFile = rememberDialogState()
    val scope = rememberCoroutineScope()
    //val backup = remember { setup.backup }
    val changelogState = appState.changelogState

    PreferenceScreen(
        modifier = modifier,
        settings = settings,
        state = state,
        handleBackPress = handleBackPress
    ) {

        val versionCode = setup.versionCode
        val versionName = setup.versionName

        // --------------------
        // Region 0 - Header
        // --------------------

        SettingsHeader(settings, versionCode.toLong(), versionName)
        SettingsProVersionHeader(showProVersionDialog)
        SettingsHeaderButtons(buttons)

        // --------------------
        // Region 1 - Content
        // --------------------

        customContent()

        // --------------------
        // Region 2 - Sprache
        // --------------------

        if (Platform.openLanguagePicker != null && setup.supportLanguagePicker) {
            PreferenceSection(
                title = stringResource(Res.string.settings_group_language)
            ) {
                PreferenceButton(
                    onClick = {
                        Platform.openLanguagePicker?.invoke()
                    },
                    title = stringResource(Res.string.settings_change_language),
                    icon = { Icon(Icons.Default.Language, contentDescription = null) }
                )
            }
        }

        // --------------------
        // Region 3 - Über
        // --------------------

        PreferenceSection(
            title = stringResource(Res.string.settings_group_about)
        ) {

            // --------------------
            // Region 3.1 - Informations
            // --------------------

            if (setup.changelogSetup != null) {
                PreferenceButton(
                    onClick = {
                        changelogState.show()
                    },
                    title = stringResource(Res.string.settings_changelog),
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.TextSnippet,
                            contentDescription = null
                        )
                    }
                )
            }

            // --------------------
            // Region 3.2 - Export/Import
            // --------------------

            if (setup.backupSupport?.addToPrefs == true) {
                ContentPreferencesAsSubPreference(setup.backupSupport,  setup.name())
            }

            // --------------------
            // Region 3.3 - Sonstiges
            // --------------------

            if ((Platform.sendFeedback != null && setup.fileLogger != null) || Platform.openMarket != null) {
                PreferenceSubScreen(
                    title = stringResource(Res.string.settings_group_others),
                    subtitle = stringResource(Res.string.settings_group_others_details),
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.More,
                            contentDescription = null,
                            modifier = Modifier.rotate(180f)
                        )
                    }
                ) {

                    PreferenceSection(
                        title = stringResource(Res.string.settings_group_others)
                    ) {
                        if (Platform.sendFeedback != null && setup.fileLogger != null) {
                            PreferenceButton(
                                onClick = {
                                    if (L.isEnabled()) {
                                        showAttachLogFile.show()
                                    } else {
                                        Platform.sendFeedback?.invoke(false, setup.fileLogger.setup)
                                    }
                                },
                                title = stringResource(Res.string.settings_feedback),
                                subtitle = stringResource(Res.string.settings_feedback_details),
                                icon = { Icon(Icons.Outlined.Mail, contentDescription = null) }
                            )
                        }
                        if (Platform.openMarket != null) {
                            PreferenceButton(
                                onClick = {
                                    Platform.openMarket?.invoke()
                                },
                                title = stringResource(Res.string.settings_open_play_store),
                                subtitle = stringResource(Res.string.settings_open_play_store_details),
                                icon = {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = FontAwesomeIcons.Brands.GooglePlay,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // --------------------
            // Region 3.4 - Privacy
            // --------------------

            PreferenceSubScreen(
                title = stringResource(Res.string.settings_group_privacy),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.PrivacyTip,
                        contentDescription = null
                    )
                }
            ) {
                PreferenceSection(
                    title = stringResource(Res.string.settings_group_privacy)
                ) {
                    PreferenceButton(
                        onClick = {
                            Platform.openUrl(setup.privacyPolicyLink)
                        },
                        title = stringResource(Res.string.settings_privacy_policy),
                        subtitle = stringResource(Res.string.settings_privacy_policy_details),
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.PrivacyTip,
                                contentDescription = null
                            )
                        }
                    )
                    if (setup.proVersionManager.supportsProVersion) {
                        /*
                                            if (appState.adManager?.privacyOptionsRequired() == true) {
                                                PreferenceButton(
                                                    onClick = {
                                                        appState.adManager?.showPrivacyOptionsForm(context.requireActivity())
                                                    },
                                                    title = stringResource(Res.string.settings_consent_settings),
                                                    subtitle = stringResource(Res.string.settings_gdpr_consent_show_dialog),
                                                    icon = {
                                                        Icon(
                                                            Icons.Outlined.PrivacyTip,
                                                            contentDescription = null
                                                        )
                                                    }
                                                )
                                            }

                     */
                    }
                }
            }

            // --------------------
            // Region 4 - Developer Menu
            // --------------------

            PreferenceSubScreen(
                title = "Developer Settings",
                icon = {
                    Icon(
                        Icons.Default.Code,
                        contentDescription = null
                    )
                },
                visible = debugPrefs.showDeveloperSettings.asDependency { it }
            ) {

                PreferenceSection(
                    title = "Developer Settings"
                ) {
                    PreferenceBool(
                        value = debugPrefs.showDebugOverlay.asMutableStateNotNull(),
                        style = PreferenceBool.Style.Checkbox,
                        title = "Debug Overlay",
                        icon = {
                            Icon(
                                Icons.Outlined.BugReport,
                                contentDescription = null
                            )
                        }
                    )
                    PreferenceBool(
                        value = debugPrefs.visualDebug.asMutableStateNotNull(),
                        style = PreferenceBool.Style.Checkbox,
                        title = "Visual Debug",
                        icon = { Icon(Icons.Outlined.BugReport, contentDescription = null) }
                    )

                    PreferenceBool(
                        value = debugPrefs.advancedLogs.asMutableStateNotNull(),
                        style = PreferenceBool.Style.Checkbox,
                        title = "Advanced Logs",
                        icon = { Icon(Icons.Default.Description, contentDescription = null) }
                    )

                    if (setup.debugDrawer != null) {
                        PreferenceBool(
                            value = debugPrefs.showDebugDrawer.asMutableStateNotNull(),
                            style = PreferenceBool.Style.Checkbox,
                            title = "Debug Drawer",
                            icon = {
                                Icon(
                                    Icons.Outlined.KeyboardDoubleArrowLeft,
                                    contentDescription = null
                                )
                            }
                        )
                    }

                    if (setup.proVersionManager.supportsProVersion) {
                        PreferenceBool(
                            value = debugPrefs.forceIsProInDebug.asMutableStateNotNull(),
                            style = PreferenceBool.Style.Checkbox,
                            title = "Force Pro Version",
                            subtitle = "Enforce pro version in debug app",
                            icon = {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    imageVector = FontAwesomeIcons.Solid.Crown,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }

                if (setup.fileLogger != null) {
                    PreferenceSection(
                        title = "Debugging"
                    ) {
                        PreferenceButton(
                            onClick = { showLogFile.value = true },
                            title = "Show Log File",
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Outlined.TextSnippet,
                                    contentDescription = null
                                )
                            }
                        )

                        if (LogManager.sendRelevantFiles != null) {
                            PreferenceButton(
                                onClick = {
                                    scope.launch {
                                        LogManager.sendRelevantFiles()
                                    }
                                },
                                title = "Send All App Files As Mail",
                                subtitle = "Cache/DB Directory + Logs",
                                icon = { Icon(Icons.Outlined.Mail, contentDescription = null) }
                            )
                        }
                    }
                }
                // TODO
                /*
                if (setup.ads != null) {
                    PreferenceSection(
                        title = "Ads"
                    ) {
                        PreferenceButton(
                            onClick = {
                                appState.adManager?.resetConsent()
                            },
                            title = "Consent zurücksetzen",
                            icon = {
                                Icon(
                                    Icons.Outlined.HideSource,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }*/
                // TODO
                val platformContext = Platform.localContext()
                val kill = Platform.kill
                val restart = Platform.restart
                if (kill != null || restart != null) {
                    PreferenceSection(
                        title = "Restart/Kill"
                    ) {
                        if (restart != null) {
                            PreferenceButton(
                                onClick = {
                                    restart(platformContext)
                                },
                                title = "Restart App",
                                icon = {
                                    Icon(
                                        Icons.Outlined.RestartAlt,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                        if (kill != null) {
                            PreferenceButton(
                                onClick = {
                                    kill(platformContext)
                                },
                                title = "Kill App",
                                icon = {
                                    Icon(
                                        Icons.Default.Stop,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }

                PreferenceSection(
                    title = "Visibility"
                ) {
                    PreferenceButton(
                        onClick = {
                            scope.launch(Platform.DispatcherIO) {
                                debugPrefs.showDeveloperSettings.update(false)
                            }
                        },
                        title = "Hide Developer Settings",
                        icon = { Icon(Icons.Outlined.HideSource, contentDescription = null) }
                    )
                }
            }
        }
    }

    if (showLogFile.value && setup.fileLogger != null) {
        LumberjackDialog(
            visible = showLogFile,
            title = "Log",
            setup = setup.fileLogger.setup
        )
    }

    if (Platform.sendFeedback != null && setup.fileLogger != null && showAttachLogFile.visible) {
        DialogInfo(
            state = showAttachLogFile,
            icon = { Icon(Icons.Outlined.Mail, contentDescription = null) },
            infoLabel = stringResource(Res.string.settings_dialog_attach_log_file_label),
            info = stringResource(Res.string.settings_dialog_attach_log_file_text),
            title = {
                Text(stringResource(Res.string.settings_feedback))
            },
            buttons = DialogDefaults.buttons(
                positive = DialogButton(stringResource(Res.string.yes)),
                negative = DialogButton(stringResource(Res.string.no))
            )
        ) {
            when (it) {
                is DialogEvent.Button -> {
                    when (it.button) {
                        DialogButtonType.Positive -> Platform.sendFeedback?.invoke(
                            true,
                            setup.fileLogger.setup
                        )

                        DialogButtonType.Negative -> Platform.sendFeedback?.invoke(
                            false,
                            setup.fileLogger.setup
                        )
                    }
                }

                DialogEvent.Dismissed -> {
                    // nichts
                }
            }
        }
    }
}