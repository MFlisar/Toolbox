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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.HideSource
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
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
import com.michaelflisar.composedialogs.core.DialogStateNoData
import com.michaelflisar.composedialogs.core.rememberDialogState
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
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.AppScope
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.backup.ContentPreferencesAsSubPreference
import com.michaelflisar.toolbox.app.features.preferences.groups.PreferenceSettingsTheme
import com.michaelflisar.toolbox.app.features.preferences.groups.SettingsHeader
import com.michaelflisar.toolbox.app.features.preferences.groups.SettingsHeaderButtons
import com.michaelflisar.toolbox.app.features.preferences.groups.SettingsProVersionHeader
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.features.scaffold.LocalPlatformStyle
import com.michaelflisar.toolbox.app.platform.ResolvedPlatformStyle
import com.michaelflisar.toolbox.app2.features.system.System
import com.michaelflisar.toolbox.app2.features.system.kill
import com.michaelflisar.toolbox.app2.features.system.restart
import com.michaelflisar.toolbox.backup.BackupManager
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.menu_settings
import com.michaelflisar.toolbox.core.resources.settings_change_language
import com.michaelflisar.toolbox.core.resources.settings_changelog
import com.michaelflisar.toolbox.core.resources.settings_group_about
import com.michaelflisar.toolbox.core.resources.settings_group_language
import com.michaelflisar.toolbox.core.resources.settings_group_others
import com.michaelflisar.toolbox.core.resources.settings_group_others_details
import com.michaelflisar.toolbox.core.resources.settings_group_privacy
import com.michaelflisar.toolbox.core.resources.settings_open_play_store
import com.michaelflisar.toolbox.core.resources.settings_open_play_store_details
import com.michaelflisar.toolbox.core.resources.settings_privacy_policy
import com.michaelflisar.toolbox.core.resources.settings_privacy_policy_details
import com.michaelflisar.toolbox.theme.padding
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

sealed class AppPreferencesStyle {

    abstract val addThemeSetting: Boolean

    class Default internal constructor(
        override val addThemeSetting: Boolean,
        val customContent: @Composable (PreferenceGroupScope.() -> Unit)?,
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
        customPageName: String? = null,
        customContent: @Composable (PreferenceGroupScope.() -> Unit)? = null,
    ): AppPreferencesStyle {
        val platformStyle = LocalPlatformStyle.current
        return when (platformStyle) {
            ResolvedPlatformStyle.Mobile -> styleDefault(
                addThemeSettings = addThemeSettings,
                customContent = customContent
            )

            ResolvedPlatformStyle.Desktop,
            ResolvedPlatformStyle.Web,
                -> {
                val page = customPageName?.let {
                    if (customContent == null)
                        throw RuntimeException("customPageName is not null, please provide a non null customContent lambda!")
                    AppPreferencesStyle.Pager.Page(customPageName) { customContent() }
                }
                stylePager(
                    addThemeSettings = addThemeSettings,
                    customPages = listOfNotNull(page)
                )
            }
        }
    }

    @Composable
    fun styleDefault(
        addThemeSettings: Boolean,
        customContent: @Composable (PreferenceGroupScope.() -> Unit)? = null,
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
    val showProVersionDialog = rememberDialogState()

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

                RegionHeader(settings, buttons, showProVersionDialog)

                // --------------------
                // Region 1 - Content
                // --------------------

                if (style.addThemeSetting)
                    PreferenceSettingsTheme(true)
                style.customContent?.invoke(this)

                // --------------------
                // Region 2 - Sprache
                // --------------------

                RegionLanguage(setup)

                // --------------------
                // Region 3 - Über
                // --------------------

                RegionAbout(setup, showLogFile)
            }
        }

        is AppPreferencesStyle.Pager -> {
            val pagerState = rememberPagerState(pageCount = { style.customPages.size + 1 })
            val scope = rememberCoroutineScope()

            Column(
                modifier = modifier
            ) {
                SecondaryTabRow(
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
                                modifier = Modifier.padding(MaterialTheme.padding.default)
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
                            RegionHeader(settings, buttons, showProVersionDialog)
                            if (style.addThemeSetting)
                                PreferenceSettingsTheme(true)
                            RegionLanguage(setup)
                            RegionAbout(setup, showLogFile)
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

    if (showLogFile.value && setup.fileLoggingSetup != null) {
        LumberjackDialog(
            visible = showLogFile,
            title = "Log",
            setup = setup.fileLoggingSetup
        )
    }

    ProVersionManager.Paywall(showProVersionDialog)
}

@Composable
private fun PreferenceGroupScope.RegionHeader(
    settings: PreferenceSettings,
    buttons: List<SettingsHeaderButtons.Button>,
    showProVersionDialog: DialogStateNoData,
) {
    SettingsHeader(settings)
    SettingsProVersionHeader(showProVersionDialog)
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
) {

    val appState = LocalAppState.current
    val debugPrefs = setup.debugPrefs
    val changelogState = appState.changelogState
    val proVersionManager = ProVersionManager.setup
    val showChangelog = remember { setup.changelogSetup != null }
    val showBackup = remember { BackupManager.manager?.config?.addToPrefs == true }
    val showPrivacy =
        remember { setup.privacyPolicyLink.isNotEmpty() || proVersionManager.supported }
    val showDeveloper by debugPrefs.showDeveloperSettings.collectAsStateNotNull()

    if (showChangelog || showBackup || showPrivacy || showDeveloper) {
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

            if (Platform.openMarket != null) {
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

                if (setup.fileLoggingSetup != null) {
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

                    }
                }
                val kill = System.kill
                val restart = System.restart
                if (kill != null || restart != null) {
                    PreferenceSection(
                        title = "Restart/Kill"
                    ) {
                        if (restart != null) {
                            PreferenceButton(
                                onClick = {
                                    restart()
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
                                    kill()
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
                            AppScope.launch(Platform.DispatcherIO) {
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