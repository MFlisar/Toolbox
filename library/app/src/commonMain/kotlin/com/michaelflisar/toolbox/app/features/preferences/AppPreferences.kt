package com.michaelflisar.toolbox.app.features.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
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
import com.michaelflisar.composepreferences.core.scopes.PreferenceScope
import com.michaelflisar.composepreferences.core.styles.ModernStyle
import com.michaelflisar.composepreferences.kotpreferences.asDependency
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.features.ads.AdsManager
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.backup.ContentPreferencesAsSubPreference
import com.michaelflisar.toolbox.app.features.device.BaseDevice
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.app.features.feedback.FeedbackManager
import com.michaelflisar.toolbox.app.features.preferences.groups.PreferenceSettingsTheme
import com.michaelflisar.toolbox.app.features.preferences.groups.SettingsHeader
import com.michaelflisar.toolbox.app.features.preferences.groups.SettingsHeaderButtons
import com.michaelflisar.toolbox.app.features.preferences.groups.SettingsProVersionHeader
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.platform.kill
import com.michaelflisar.toolbox.app.platform.localContext
import com.michaelflisar.toolbox.app.platform.restart
import com.michaelflisar.toolbox.backup.BackupManager
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.menu_settings
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
internal expect fun PreferenceScope.PreferenceRegionAds()

@Composable
internal expect fun PreferenceGroupScope.PreferenceRegionAdsDeveloper()

sealed class AppPreferencesStyle {

    abstract val addThemeSetting: Boolean

    class Default internal constructor(
        override val addThemeSetting: Boolean,
        val customContent: @Composable PreferenceGroupScope.() -> Unit,
    ) : AppPreferencesStyle()

    class Pager internal constructor(
        override val addThemeSetting: Boolean,
        val labelDefaultPage: String,
        val contentPadding: PaddingValues = PaddingValues(0.dp),
        val customPages: List<Page>,
    ) : AppPreferencesStyle() {
        class Page(
            val title: String,
            val content: @Composable PreferenceGroupScope.() -> Unit,
        )
    }
}

object AppPreferencesDefaults {

    @Composable
    fun styleDeviceDefault(
        addThemeSettings: Boolean,
        customPageName: String,
        customContent: @Composable PreferenceGroupScope.() -> Unit,
    ): AppPreferencesStyle {
        return when (CurrentDevice.base) {
            BaseDevice.Mobile -> styleDefault(
                addThemeSettings = addThemeSettings,
                customContent = customContent
            )

            BaseDevice.Desktop,
            BaseDevice.Web,
                -> stylePager(
                addThemeSettings = addThemeSettings,
                customPages = listOf(
                    AppPreferencesStyle.Pager.Page(customPageName) { customContent() }
                )
            )
        }
    }

    @Composable
    fun styleDefault(
        addThemeSettings: Boolean,
        customContent: @Composable PreferenceGroupScope.() -> Unit,
    ): AppPreferencesStyle {
        return remember(addThemeSettings, customContent) {
            AppPreferencesStyle.Default(addThemeSettings, customContent)
        }
    }

    @Composable
    fun stylePager(
        addThemeSettings: Boolean,
        labelDefaultPage: String = stringResource(Res.string.menu_settings),
        contentPadding: PaddingValues = PaddingValues(0.dp),
        customPages: List<AppPreferencesStyle.Pager.Page>,
    ): AppPreferencesStyle.Pager {
        return remember(addThemeSettings, labelDefaultPage, contentPadding, customPages) {
            AppPreferencesStyle.Pager(
                addThemeSettings,
                labelDefaultPage,
                contentPadding,
                customPages
            )
        }
    }

}

@Composable
fun AppPreferences(
    style: AppPreferencesStyle,
    modifier: Modifier = Modifier,
    buttons: List<SettingsHeaderButtons.Button> = emptyList(),
    onPreferenceStateChanged: (state: PreferenceState) -> Unit,
    handleBackPress: Boolean = true,
) {

    val settings = PreferenceSettingsDefaults.settings(
        toggleBooleanOnItemClick = true,
        style = ModernStyle.create()
    )

    SettingsContent(
        modifier,
        settings,
        onPreferenceStateChanged,
        handleBackPress,
        buttons,
        style
    )

}

@Composable
internal fun SettingsContent(
    modifier: Modifier,
    settings: PreferenceSettings,
    onPreferenceStateChanged: (state: PreferenceState) -> Unit,
    handleBackPress: Boolean,
    buttons: List<SettingsHeaderButtons.Button> = emptyList(),
    style: AppPreferencesStyle,
) {
    val setup = AppSetup.get()

    val showLogFile = rememberSaveable { mutableStateOf(false) }
    val showAttachLogFile = rememberDialogState()

    //val backup = remember { setup.backup }

    when (style) {
        is AppPreferencesStyle.Default -> {
            val state = rememberPreferenceState()
            LaunchedEffect(Unit) {
                onPreferenceStateChanged(state)
            }
            PreferenceScreen(
                modifier = modifier,
                settings = settings,
                state = state,
                handleBackPress = handleBackPress
            ) {
                // --------------------
                // Region 0 - Header
                // --------------------

                RegionHeader(settings, buttons)

                // --------------------
                // Region 1 - Content
                // --------------------

                if (style.addThemeSetting)
                    PreferenceSettingsTheme(true)
                style.customContent(this)

                // --------------------
                // Region 2 - Sprache
                // --------------------

                RegionLanguage(setup)

                // --------------------
                // Region 3 - Über
                // --------------------

                RegionAbout(setup, showLogFile, showAttachLogFile)
            }
        }

        is AppPreferencesStyle.Pager -> {
            val pagerState = rememberPagerState(pageCount = { style.customPages.size + 1 })
            val scope = rememberCoroutineScope()

            Column(
                modifier = modifier
            ) {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(pagerState.pageCount) { page ->
                        Tab(
                            selected = pagerState.currentPage == page,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(page)
                                }
                            }
                        ) {
                            Text(
                                text = if (page == 0) style.labelDefaultPage else style.customPages[page - 1].title,
                                modifier = Modifier.padding(LocalStyle.current.paddingDefault)
                            )
                        }
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    contentPadding = style.contentPadding,
                    verticalAlignment = Alignment.Top
                ) { page ->

                    val state = rememberPreferenceState()
                    LaunchedEffect(state.currentLevel, pagerState.currentPage) {
                        if (page == pagerState.currentPage) {
                            onPreferenceStateChanged(state)
                        }
                    }

                    if (page == 0) {
                        PreferenceScreen(
                            modifier = Modifier.fillMaxSize(),
                            settings = settings,
                            state = state,
                            handleBackPress = handleBackPress
                        ) {
                            RegionHeader(settings, buttons)
                            if (style.addThemeSetting)
                                PreferenceSettingsTheme(true)
                            RegionLanguage(setup)
                            RegionAbout(setup, showLogFile, showAttachLogFile)
                        }
                    } else {
                        val customPage = style.customPages[page - 1]
                        PreferenceScreen(
                            modifier = Modifier.fillMaxSize(),
                            settings = settings,
                            state = state,
                            handleBackPress = handleBackPress
                        ) {
                            customPage.content(this)
                        }
                    }
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

    if (FeedbackManager.supported && setup.fileLogger != null && showAttachLogFile.visible) {
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
                        DialogButtonType.Positive -> FeedbackManager.sendFeedback(
                            setup.fileLogger.setup,
                            emptyList(),
                            true,
                        )

                        DialogButtonType.Negative -> FeedbackManager.sendFeedback(
                            setup.fileLogger.setup,
                            emptyList(),
                            false
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

@Composable
private fun PreferenceGroupScope.RegionHeader(
    settings: PreferenceSettings,
    buttons: List<SettingsHeaderButtons.Button>,
) {
    SettingsHeader(settings)
    SettingsProVersionHeader()
    SettingsHeaderButtons(buttons)
}

@Composable
private fun PreferenceGroupScope.RegionLanguage(
    setup: AppSetup,
) {
    if (Platform.openLanguagePicker != null && !setup.disableLanguagePicker) {
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
}

@Composable
private fun PreferenceGroupScope.RegionAbout(
    setup: AppSetup,
    showLogFile: MutableState<Boolean>,
    showAttachLogFile: DialogStateNoData,
) {

    val scope = rememberCoroutineScope()
    val appState = LocalAppState.current
    val debugPrefs = setup.debugPrefs
    val changelogState = appState.changelogState
    val proVersionManager = ProVersionManager.setup
    val adsManager = AdsManager.manager

    val showChangelog = remember { setup.changelogSetup != null }
    val showBackup = remember { BackupManager.manager?.config?.addToPrefs == true }
    val showFeedback = remember { FeedbackManager.supported && setup.fileLogger != null }
    val showPrivacy =
        remember { setup.privacyPolicyLink.isNotEmpty() || proVersionManager.supported }
    val showDeveloper by debugPrefs.showDeveloperSettings.collectAsStateNotNull()

    if (showChangelog || showBackup || showFeedback || showPrivacy || showDeveloper) {
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

            if (BackupManager.manager?.config?.addToPrefs == true) {
                ContentPreferencesAsSubPreference()
            }

            // --------------------
            // Region 3.3 - Sonstiges
            // --------------------

            if ((FeedbackManager.supported && setup.fileLogger != null) || Platform.openMarket != null) {
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
                        if (FeedbackManager.supported && setup.fileLogger != null) {
                            PreferenceButton(
                                onClick = {
                                    if (L.isEnabled()) {
                                        showAttachLogFile.show()
                                    } else {
                                        FeedbackManager.sendFeedback(
                                            setup.fileLogger.setup,
                                            emptyList(),
                                            false
                                        )
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

            if (setup.privacyPolicyLink.isNotEmpty() || proVersionManager.supported) {
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
                        if (setup.privacyPolicyLink.isNotEmpty()) {
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
                        }
                        if (adsManager != null && proVersionManager.supported) {
                            PreferenceRegionAds()
                        }
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

                    if (proVersionManager.supported) {
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

                        if (FeedbackManager.supported) {
                            PreferenceButton(
                                onClick = {
                                    scope.launch {
                                        FeedbackManager.sendRelevantFiles()
                                    }
                                },
                                title = "Send All App Files As Mail",
                                subtitle = "Cache/DB Directory + Logs",
                                icon = { Icon(Icons.Outlined.Mail, contentDescription = null) }
                            )
                        }
                    }
                }
                if (adsManager != null && proVersionManager.supported) {
                    PreferenceRegionAdsDeveloper()
                }
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
}