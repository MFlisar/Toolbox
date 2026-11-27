package com.michaelflisar.toolbox.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.HideSource
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material.icons.outlined.Style
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.dialogs.frequency.classes.Frequency
import com.michaelflisar.composedialogs.styled.DialogFrequency
import com.michaelflisar.composepreferences.styled.PreferenceBool
import com.michaelflisar.composepreferences.styled.PreferenceCustomItem
import com.michaelflisar.composepreferences.styled.PreferenceFilter
import com.michaelflisar.composepreferences.styled.PreferenceItem
import com.michaelflisar.composepreferences.styled.PreferenceList
import com.michaelflisar.composepreferences.styled.PreferenceOpenGroupHeaderStyle
import com.michaelflisar.composepreferences.styled.layout.PreferenceGroup
import com.michaelflisar.composepreferences.styled.layout.PreferenceScreen
import com.michaelflisar.composepreferences.styled.layout.PreferenceSection
import com.michaelflisar.composepreferences.styled.layout.rememberPreferenceScreenState
import com.michaelflisar.composepreferences.styled.navigation.PreferenceBreadcrumb
import com.michaelflisar.composepreferences.styled.rememberPreferenceFilter
import com.michaelflisar.composepreferences.styled.rememberPreferenceStyle
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.classes.CustomColors
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composestyled.isDynamicColorsSupported
import com.michaelflisar.composestyled.isSystemContrastSupported
import com.michaelflisar.composestyled.platform.platformSystemContrastLevel
import com.michaelflisar.composestyled.themes.StyledContrastLevel
import com.michaelflisar.composestyled.themes.StyledThemeAppearance
import com.michaelflisar.composestyled.themes.StyledThemeColors
import com.michaelflisar.composestyled.themes.StyledThemeContrast
import com.michaelflisar.composestyled.themes.StyledThemeVariant
import com.michaelflisar.composestyled.tokens.LocalStyledContentColor
import com.michaelflisar.composestyled.ui.components.StyledButtonDefaults
import com.michaelflisar.composestyled.ui.components.StyledInput
import com.michaelflisar.composestyled.ui.components.StyledSegmentedButtonRow
import com.michaelflisar.composestyled.ui.components.StyledSegmentedButtonRowDefaults
import com.michaelflisar.composestyled.ui.icons.StyledIcons
import com.michaelflisar.composestyled.ui.layout.StyledDialogDefaults
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.composestyled.ui.specials.StyledInformationRowHorizontal
import com.michaelflisar.composestyled.ui.state.rememberDialogState
import com.michaelflisar.kmp.platformcontext.PlatformIO
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.kotpreferences.compose.collectAsStateWithLifecycleNotNull
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.features.ads.AdsDeveloperSettings
import com.michaelflisar.toolbox.app.features.ads.AdsManager
import com.michaelflisar.toolbox.app.features.ads.AdsPrivacySettings
import com.michaelflisar.toolbox.app.features.debug.DebugOverlayDescription
import com.michaelflisar.toolbox.app.features.dialogs.buttonOK
import com.michaelflisar.toolbox.app.features.feedback.DialogFeedback
import com.michaelflisar.toolbox.app.features.feedback.FeedbackManager
import com.michaelflisar.toolbox.app.features.preferences.rememberThemeConfig
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.features.system.System
import com.michaelflisar.toolbox.app.features.system.kill
import com.michaelflisar.toolbox.app.features.system.restart
import com.michaelflisar.toolbox.app.features.toolbar.ToolbarStyle
import com.michaelflisar.toolbox.app.features.ui.ColorBox
import com.michaelflisar.toolbox.backup.BackupDefaults
import com.michaelflisar.toolbox.backup.BackupDialog
import com.michaelflisar.toolbox.backup.BackupManager
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.contrast_high
import com.michaelflisar.toolbox.core.resources.contrast_medium
import com.michaelflisar.toolbox.core.resources.contrast_normal
import com.michaelflisar.toolbox.core.resources.menu_settings
import com.michaelflisar.toolbox.core.resources.settings_auto_backup_choose_folder
import com.michaelflisar.toolbox.core.resources.settings_auto_backup_disable
import com.michaelflisar.toolbox.core.resources.settings_auto_backup_enable
import com.michaelflisar.toolbox.core.resources.settings_auto_backup_folder
import com.michaelflisar.toolbox.core.resources.settings_auto_backup_frequency
import com.michaelflisar.toolbox.core.resources.settings_auto_backup_info_details
import com.michaelflisar.toolbox.core.resources.settings_auto_backup_info_title
import com.michaelflisar.toolbox.core.resources.settings_change_language
import com.michaelflisar.toolbox.core.resources.settings_changelog
import com.michaelflisar.toolbox.core.resources.settings_contrast
import com.michaelflisar.toolbox.core.resources.settings_create_backup
import com.michaelflisar.toolbox.core.resources.settings_developer_by_name
import com.michaelflisar.toolbox.core.resources.settings_dynamic_theme
import com.michaelflisar.toolbox.core.resources.settings_dynamic_theme_selection_info_details
import com.michaelflisar.toolbox.core.resources.settings_dynamic_theme_selection_info_title
import com.michaelflisar.toolbox.core.resources.settings_feedback
import com.michaelflisar.toolbox.core.resources.settings_feedback_details
import com.michaelflisar.toolbox.core.resources.settings_filter_themes_label
import com.michaelflisar.toolbox.core.resources.settings_group_backup
import com.michaelflisar.toolbox.core.resources.settings_group_infos
import com.michaelflisar.toolbox.core.resources.settings_group_privacy
import com.michaelflisar.toolbox.core.resources.settings_group_style
import com.michaelflisar.toolbox.core.resources.settings_open_play_store
import com.michaelflisar.toolbox.core.resources.settings_open_play_store_details
import com.michaelflisar.toolbox.core.resources.settings_privacy_policy
import com.michaelflisar.toolbox.core.resources.settings_privacy_policy_details
import com.michaelflisar.toolbox.core.resources.settings_pro_version_free_upgrade_hint
import com.michaelflisar.toolbox.core.resources.settings_pro_version_is_free
import com.michaelflisar.toolbox.core.resources.settings_pro_version_is_pro_info
import com.michaelflisar.toolbox.core.resources.settings_pro_version_title
import com.michaelflisar.toolbox.core.resources.settings_restore_backup
import com.michaelflisar.toolbox.core.resources.settings_section_auto_backup
import com.michaelflisar.toolbox.core.resources.settings_section_manual_backup
import com.michaelflisar.toolbox.core.resources.settings_section_more_settings
import com.michaelflisar.toolbox.core.resources.settings_section_theme_options
import com.michaelflisar.toolbox.core.resources.settings_theme
import com.michaelflisar.toolbox.core.resources.settings_theme_collection
import com.michaelflisar.toolbox.core.resources.settings_theme_colors
import com.michaelflisar.toolbox.core.resources.settings_theme_variant
import com.michaelflisar.toolbox.core.resources.settings_toolbar_style
import com.michaelflisar.toolbox.core.resources.theme_dark
import com.michaelflisar.toolbox.core.resources.theme_light
import com.michaelflisar.toolbox.core.resources.theme_system
import com.michaelflisar.toolbox.core.resources.theme_variant_colorful
import com.michaelflisar.toolbox.core.resources.theme_variant_monochrome
import com.michaelflisar.toolbox.features.proversion.ProState
import com.michaelflisar.toolbox.icons.Crown
import com.michaelflisar.toolbox.icons.MyIcons
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.GooglePlay
import compose.icons.fontawesomeicons.solid.Crown
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

object MainAppSettings {

    enum class DisplayMode {
        List,
        //Pager
    }

    enum class HeaderIconStyle {
        Decorated,
        Plain
    }

    @Immutable
    data class Setup(
        val supportFiltering: Boolean = false,
        val breadcrumbs: Boolean = true,
        val displayMode: DisplayMode = DisplayMode.List,
        val headerIconStyle: HeaderIconStyle = HeaderIconStyle.Decorated,
    )

    val filter: PreferenceFilter
        @Composable get() = rememberPreferenceFilter()
    val preferenceState
        @Composable get() = rememberPreferenceScreenState()
    val preferenceStyle
        @Composable get() = rememberPreferenceStyle(
            openedGroupHeader = PreferenceOpenGroupHeaderStyle.Disabled,
            //outerPadding = PaddingValues(bottom = 32.dp)
        )
    val preferenceBreadcrumbRootLabel: String
        @Composable get() = stringResource(Res.string.menu_settings)
}

object MainAppSettingsDefault {

    @Composable
    fun MainPrefHeader(iconStyle: MainAppSettings.HeaderIconStyle) {

        val scope = rememberCoroutineScope()
        val setup = AppSetup.get()
        val appActions = LocalAppActions.current

        val versionCode = setup.appData.versionCode
        val versionName = setup.appData.versionName

        val headerBackground = StyledTheme.colorScheme.surfaceVariant
        val headerDecorationColor = StyledTheme.colorScheme.primary
        val headerShape = RoundedCornerShape(24.dp)

        PreferenceCustomItem(
            containerColor = Color.Transparent,
            contentColor = StyledTheme.colorScheme.onSurfaceVariant,
            contentPadding = PaddingValues(0.dp),
            shape = RectangleShape
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(headerShape)
                    .background(headerBackground)
                    .drawBehind {
                        val decorationUnit = minOf(size.width, size.height)
                        drawCircle(
                            color = headerDecorationColor.copy(alpha = 0.16f),
                            radius = decorationUnit * 0.8f,
                            center = Offset(
                                x = size.width - decorationUnit * 0.15f,
                                y = decorationUnit * 0.35f
                            )
                        )
                        drawCircle(
                            color = headerDecorationColor.copy(alpha = 0.10f),
                            radius = decorationUnit * 0.42f,
                            center = Offset(
                                x = size.width - decorationUnit * 0.1f,
                                y = size.height - decorationUnit * 0.12f
                            )
                        )
                    }
                    .combinedClickable(
                        interactionSource = null,
                        indication = null,
                        onClick = {},
                        onLongClick = {
                            scope.launch(Dispatchers.PlatformIO) {
                                val enabled = setup.debug.prefs.showDeveloperSettings.read()
                                setup.debug.prefs.showDeveloperSettings.update(!enabled)
                                appActions.showToast(
                                    "Developer settings ${if (enabled) "disabled" else "enabled"}!"
                                )
                            }
                        }
                    )
                    .padding(
                        horizontal = StyledTheme.padding.medium,
                        vertical = StyledTheme.padding.large
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(StyledTheme.spacing.medium)
                ) {
                    when (iconStyle) {
                        MainAppSettings.HeaderIconStyle.Decorated -> {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(headerShape)
                                    .background(StyledTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = setup.icon(),
                                    contentDescription = null,
                                    modifier = Modifier.size(52.dp)
                                )
                            }
                        }

                        MainAppSettings.HeaderIconStyle.Plain -> {
                            Image(
                                painter = setup.icon(),
                                contentDescription = null,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        StyledText(
                            text = setup.appData.name,
                            style = StyledTheme.typography.titleLarge
                        )
                        val developerSettingsActive = AppSetup.get().debug.prefs.showDeveloperSettings.collectAsStateWithLifecycleNotNull()
                        StyledText(
                            text = if (developerSettingsActive.value) {
                                "$versionName ($versionCode)"
                            } else versionName,
                            style = StyledTheme.typography.bodySmall,
                            color = StyledTheme.colorScheme.onSurfaceVariantSupporting

                        )
                        StyledText(
                            text = stringResource(
                                Res.string.settings_developer_by_name,
                                setup.developer.name
                            ),
                            style = StyledTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = StyledTheme.colorScheme.onSurfaceVariantSupporting
                        )
                    }
                }
            }
        }

        val proVersionSetup = ProVersionManager.setup
        if (proVersionSetup.supported) {
            val proState = proVersionSetup.proState.collectAsState().value
            val isPro = proState == ProState.Yes
            val title = if (proState == ProState.No) {
                stringResource(Res.string.settings_pro_version_is_free)
            } else {
                stringResource(Res.string.settings_pro_version_title)
            }
            val subtitle = when (proState) {
                ProState.No -> stringResource(Res.string.settings_pro_version_free_upgrade_hint)
                ProState.Yes -> stringResource(Res.string.settings_pro_version_is_pro_info)
                ProState.Unknown -> null
            }
            PreferenceItem(
                title = title,
                subtitle = subtitle,
                icon = if (isPro)
                    Icons.Default.Celebration.toIconComposable()
                else
                    MyIcons.Crown.toIconComposable(),
                onClick = if (isPro) null else {
                    { appActions.showProVersionDialog() }
                },
                contentPadding = PaddingValues(
                    horizontal = StyledTheme.padding.medium,
                    vertical = StyledTheme.padding.large
                ),
                containerColor = StyledTheme.colorScheme.primary,
                contentColor = StyledTheme.colorScheme.onPrimary,
                supportingColor = StyledTheme.colorScheme.onPrimarySupporting
            )
        }
    }

    @Composable
    fun DeveloperSettingsGroup() {

        val setup = AppSetup.get()
        val scope = rememberCoroutineScope()

        val showLogFile = rememberSaveable { mutableStateOf(false) }

        val versionCode = setup.appData.versionCode
        val versionName = setup.appData.versionName

        PreferenceGroup(
            title = "Developer Settings",
            icon = Icons.Default.Code.toIconComposable()
        ) {
            val appTesting = setup.debug.appTesting
            if (appTesting != null) {
                PreferenceSection(
                    title = "App Testing"
                ) {
                    PreferenceItem(
                        containerColor = StyledTheme.colorScheme.primary,
                        contentColor = StyledTheme.colorScheme.onPrimary,
                        icon = Icons.Outlined.GroupAdd.toIconComposable(),
                        onClick = { Platform.openUrl(appTesting.joinLink) },
                        title = "Join App Testing"
                    )
                    PreferenceItem(
                        containerColor = StyledTheme.colorScheme.primary,
                        contentColor = StyledTheme.colorScheme.onPrimary,
                        icon = Icons.Outlined.Download.toIconComposable(),
                        onClick = { Platform.openUrl(appTesting.downloadLink) },
                        title = "Download App"
                    )
                }
            }

            if (ProVersionManager.setup.supported && AdsManager.manager != null) {
                PreferenceSection(
                    title = "Pro Version"
                ) {
                    PreferenceBool(
                        value = setup.debug.prefs.forceIsProInDebug.asMutableStateNotNull(),
                        style = PreferenceBool.Style.Switch,
                        title = "Force Pro Version",
                        subtitle = "Enforce pro version in debug app",
                        icon = FontAwesomeIcons.Solid.Crown.toIconComposable()
                    )
                }
            }

            val showDebugOverlay = setup.debug.prefs.showDebugOverlay.asMutableStateNotNull()

            PreferenceSection(
                title = "Debug UI"
            ) {
                PreferenceBool(
                    value = setup.debug.prefs.showDebugDrawer.asMutableStateNotNull(),
                    style = PreferenceBool.Style.Switch,
                    title = "Debug Drawer",
                    icon = Icons.Outlined.KeyboardDoubleArrowLeft.toIconComposable(),
                )

                PreferenceBool(
                    value = showDebugOverlay,
                    style = PreferenceBool.Style.Switch,
                    title = "Debug Overlay",
                    subtitle = DebugOverlayDescription,
                    icon = Icons.Outlined.Info.toIconComposable(),
                )
            }

            PreferenceSection(
                title = "Debug Overlay Einstellungen",
                isVisible = { showDebugOverlay.value }
            ) {
                PreferenceBool(
                    value = setup.debug.prefs.showFps.asMutableStateNotNull(),
                    style = PreferenceBool.Style.Switch,
                    title = "Show FPS",
                    subtitle = "Display frame rate in the debug overlay",
                    icon = Icons.Outlined.Info.toIconComposable()
                )
            }

            if (ProVersionManager.setup.supported) {
                AdsDeveloperSettings()
            }

            /*
            if (setup.logging.file != null) {
                PreferenceSection(
                    title = "Debugging",
                   
                ) {
                    PreferenceItem(
                        onClick = { showLogFile.value = true },
                        title = "Show Log File",
                        icon = Icons.AutoMirrored.Outlined.TextSnippet.toIconComposable()
                    )
                }
            }*/

            if (System.kill != null || System.restart != null) {
                PreferenceSection(
                    title = "Restart/Kill",

                    ) {
                    if (System.restart != null) {
                        PreferenceItem(
                            onClick = {
                                System.restart?.invoke()
                            },
                            title = "Restart App",
                            icon = Icons.Outlined.RestartAlt.toIconComposable()
                        )
                    }
                    if (System.kill != null) {
                        PreferenceItem(
                            onClick = {
                                System.kill?.invoke()
                            },
                            title = "Kill App",
                            icon = Icons.Default.Stop.toIconComposable()
                        )
                    }
                }
            }

            PreferenceSection(
                title = "Visibility"
            ) {
                PreferenceItem(
                    onClick = {
                        scope.launch(Dispatchers.PlatformIO) {
                            setup.debug.prefs.showDeveloperSettings.update(false)
                        }
                    },
                    title = "Hide Developer Settings",
                    icon = Icons.Outlined.HideSource.toIconComposable()
                )
            }
        }

        if (showLogFile.value && setup.logging.file != null) {
            // TODO: aktuell ist lumberjack dialog m3 dialog!
            //LumberjackDialog(
            //    visible = showLogFile,
            //    title = "Log",
            //    setup = setup.logging.file
            //)
        }
    }

    @Composable
    fun Theming() {

        val setup = AppSetup.get()
        val themeSupport = setup.themeSupport
        val prefs = setup.prefs
        val themeCollections = themeSupport.colorsCollections
            .filter { it.themes.isNotEmpty() }
            .sortedBy { it.name.lowercase() }
        val allThemes =
            themeCollections.flatMap { it.themes }.sortedBy { it.name.lowercase() }
        val selectedThemeColors = prefs.themeColors.asMutableStateNotNull()

        val themeConfig = rememberThemeConfig()

        val dynamicTheme = prefs.dynamicTheme.asMutableStateNotNull()
        val dynamicThemingSupported =
            themeSupport.supportDynamicColors && StyledTheme.isDynamicColorsSupported

        val isDynamicThemingActive = remember {
            derivedStateOf {
                dynamicThemingSupported && dynamicTheme.value
            }
        }


        PreferenceSection(
            title = stringResource(Res.string.settings_theme),
            isVisible = {
                themeSupport.supportDarkLight ||
                        (themeSupport.supportThemeSelector && allThemes.size > 1) ||
                        !isDynamicThemingActive.value
            }
        ) {
            if (themeSupport.supportDarkLight) {
                val appearanceNames = mapOf(
                    StyledThemeAppearance.System to stringResource(Res.string.theme_system),
                    StyledThemeAppearance.Light to stringResource(Res.string.theme_light),
                    StyledThemeAppearance.Dark to stringResource(Res.string.theme_dark),
                )
                PreferenceCustomItem {
                    StyledSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                        items = StyledThemeAppearance.entries.toList(),
                        customization = StyledButtonDefaults.customize(
                            colors = CustomColors(
                                filledBackground = StyledTheme.colorScheme.background,
                                filledForeground = LocalStyledContentColor.current,
                                textForeground = LocalStyledContentColor.current
                            )
                        ),
                        selected = prefs.themeAppearance.asMutableStateNotNull(),
                        content = StyledSegmentedButtonRowDefaults.defaultRenderer(
                            mapper = { appearanceNames.getValue(it) }
                        )
                    )
                }
            }

            if (themeSupport.supportThemeSelector && allThemes.size > 1) {

                if (dynamicThemingSupported) {
                    PreferenceBool(
                        value = dynamicTheme,
                        title = stringResource(Res.string.settings_dynamic_theme),
                        icon = Icons.Outlined.FormatPaint.toIconComposable()
                    )
                    if (isDynamicThemingActive.value) {
                        PreferenceItem(
                            title = stringResource(Res.string.settings_dynamic_theme_selection_info_title),
                            subtitle = stringResource(Res.string.settings_dynamic_theme_selection_info_details),
                            icon = Icons.Outlined.Info.toIconComposable(),
                            shape = MainAppSettings.preferenceStyle.item.shape, // damit excluden wir das item aus section style handling
                            containerColor = StyledTheme.colorScheme.primary,
                            contentColor = StyledTheme.colorScheme.onPrimary,
                            supportingColor = StyledTheme.colorScheme.onPrimarySupporting
                        )
                    }
                }

                val selectedThemeCollection = remember(themeCollections, selectedThemeColors) {
                    object : MutableState<Int> {
                        override var value: Int
                            get() = themeCollections.indexOfFirst { collection ->
                                collection.themes.any { it.id == selectedThemeColors.value }
                            }.coerceAtLeast(0)
                            set(value) {
                                themeCollections.getOrNull(value)?.themes
                                    ?.minByOrNull { it.name.lowercase() }
                                    ?.let { selectedThemeColors.value = it.id }
                            }

                        override fun component1() = value
                        override fun component2(): (Int) -> Unit = { value = it }
                    }
                }

                if (themeCollections.size > 1) {
                    PreferenceList(
                        items = themeCollections.indices.toList(),
                        selected = selectedThemeCollection,
                        title = stringResource(Res.string.settings_theme_collection),
                        icon = Icons.Outlined.Style.toIconComposable(),
                        itemText = { themeCollections[it].name },
                        presentation = PreferenceList.Presentation.Dropdown(),
                        isVisible = { !isDynamicThemingActive.value }
                    )
                }

                val themes = themeCollections
                    .getOrNull(selectedThemeCollection.value)
                    ?.themes
                    ?.sortedBy { it.name.lowercase() }
                    .orEmpty()
                if (themes.size > 1) {
                    val themeNames = themes.associate { it.id to it.name }
                    val themeIcons = themes.associate {
                        it.id to @Composable { it: StyledThemeColors ->
                            val isDark = StyledTheme.colorScheme.isDarkTheme
                            val colors = it.colorful.resolve(isDark, StyledContrastLevel.Normal)
                            ColorBox(
                                color = colors.primary
                            )
                        }
                    }
                    PreferenceList(
                        items = themes.map { it.id },
                        selected = selectedThemeColors,
                        title = stringResource(Res.string.settings_theme_colors),
                        icon = Icons.Outlined.ColorLens.toIconComposable(),
                        itemText = { themeNames[it] ?: it },
                        itemLeadingContent = {
                            themeIcons[it]?.invoke(themes.first { theme -> theme.id == it })
                        },
                        presentation = PreferenceList.Presentation.Dropdown(),
                        isVisible = { !isDynamicThemingActive.value }
                    )
                }
            }

            val variantNames = mapOf(
                StyledThemeVariant.Monochrome to stringResource(Res.string.theme_variant_monochrome),
                StyledThemeVariant.Colorful to stringResource(Res.string.theme_variant_colorful),
            )
            val variantIcons = mapOf(
                StyledThemeVariant.Monochrome to @Composable {
                    val isDark = StyledTheme.colorScheme.isDarkTheme
                    ColorBox(
                        color = themeConfig.colors.monochrome.resolve(
                            isDark,
                            StyledContrastLevel.Normal
                        ).primary,
                        color2 = themeConfig.colors.monochrome.resolve(
                            isDark,
                            StyledContrastLevel.Normal
                        ).secondary
                    )
                },
                StyledThemeVariant.Colorful to @Composable {
                    val isDark = StyledTheme.colorScheme.isDarkTheme
                    ColorBox(
                        color = themeConfig.colors.colorful.resolve(
                            isDark,
                            StyledContrastLevel.Normal
                        ).primary,
                        color2 = themeConfig.colors.colorful.resolve(
                            isDark,
                            StyledContrastLevel.Normal
                        ).secondary
                    )
                }
            )
            PreferenceList(
                items = StyledThemeVariant.entries,
                selected = prefs.themeVariant.asMutableStateNotNull(),
                title = stringResource(Res.string.settings_theme_variant),
                icon = Icons.Outlined.Style.toIconComposable(),
                itemText = { variantNames.getValue(it) },
                itemLeadingContent = { variantIcons.get(it)?.invoke() },
                presentation = PreferenceList.Presentation.Dropdown(),
                isVisible = { !isDynamicThemingActive.value }
            )
        }

        PreferenceSection(
            title = stringResource(Res.string.settings_section_theme_options),
            isVisible = {
                themeSupport.supportToolbarStyles || themeSupport.supportContrast
            }
        ) {
            if (themeSupport.supportToolbarStyles) {
                val toolbarStyleNames = ToolbarStyle.entries.associateWith {
                    stringResource(it.label)
                }
                PreferenceList(
                    items = ToolbarStyle.entries,
                    selected = prefs.toolbarStyle.asMutableStateNotNull(),
                    icon = Icons.Default.Splitscreen.toIconComposable(),
                    title = stringResource(Res.string.settings_toolbar_style),
                    itemText = { toolbarStyleNames.getValue(it) },
                    presentation = PreferenceList.Presentation.Dropdown()
                )
            }

            if (themeSupport.supportContrast) {
                val isSystemSupported = StyledTheme.isSystemContrastSupported
                val defaultContrast = when (platformSystemContrastLevel()) {
                    StyledContrastLevel.Normal -> StyledThemeContrast.Normal
                    StyledContrastLevel.Medium -> StyledThemeContrast.Medium
                    StyledContrastLevel.High -> StyledThemeContrast.High
                }
                val selectedContrast = prefs.themeContrast.asMutableStateNotNull()
                val selectedContrastForDisplay =
                    remember(selectedContrast, isSystemSupported, defaultContrast) {
                        object : MutableState<StyledThemeContrast> {
                            override var value: StyledThemeContrast
                                get() = selectedContrast.value.let {
                                    if (!isSystemSupported && it == StyledThemeContrast.System) defaultContrast else it
                                }
                                set(value) {
                                    selectedContrast.value = value
                                }

                            override fun component1() = value
                            override fun component2(): (StyledThemeContrast) -> Unit =
                                { value = it }
                        }
                    }
                val contrasts = StyledThemeContrast.entries.filter {
                    isSystemSupported || it != StyledThemeContrast.System
                }
                LaunchedEffect(isSystemSupported, selectedContrast.value, defaultContrast) {
                    if (!isSystemSupported && selectedContrast.value == StyledThemeContrast.System) {
                        selectedContrast.value = defaultContrast
                    }
                }
                val names = mapOf(
                    StyledThemeContrast.System to stringResource(Res.string.theme_system),
                    StyledThemeContrast.Normal to stringResource(Res.string.contrast_normal),
                    StyledThemeContrast.Medium to stringResource(Res.string.contrast_medium),
                    StyledThemeContrast.High to stringResource(Res.string.contrast_high),
                )
                val icons = mapOf(
                    StyledThemeContrast.Normal to Icons.Default.BrightnessLow,
                    StyledThemeContrast.Medium to Icons.Default.BrightnessMedium,
                    StyledThemeContrast.High to Icons.Default.BrightnessHigh,
                    StyledThemeContrast.System to Icons.Default.PhoneAndroid,
                    null to null
                )

                PreferenceList(
                    items = contrasts,
                    selected = selectedContrastForDisplay,
                    title = stringResource(Res.string.settings_contrast),
                    icon = Icons.Outlined.SettingsBrightness.toIconComposable(),
                    itemText = { names.getValue(it) },
                    itemLeadingContent = { icons[it]?.toIconComposable() },
                    presentation = PreferenceList.Presentation.Dropdown()
                )
            }
        }
    }

    @Composable
    fun Backup() {
        val backup = BackupManager.manager ?: return
        val setup = AppSetup.get()
        val backupDialog = rememberDialogState<BackupDialog.Mode>(null)
        val frequencyDialog = rememberDialogState()
        val scope = rememberCoroutineScope()
        val autoBackupFrequency = setup.prefs.autoBackupFrequency.asMutableStateNotNull()

        PreferenceSection(
            title = stringResource(Res.string.settings_section_manual_backup)
        ) {
            PreferenceItem(
                title = stringResource(Res.string.settings_create_backup),
                icon = Icons.Outlined.CloudUpload.toIconComposable(),
                onClick = { backupDialog.show(BackupDialog.Mode.Export) }
            )
            PreferenceItem(
                title = stringResource(Res.string.settings_restore_backup),
                icon = Icons.Outlined.CloudDownload.toIconComposable(),
                onClick = { backupDialog.show(BackupDialog.Mode.Import) }
            )
        }

        if (backup.autoBackupConfig != null) {
            val backupPathData = setup.prefs.backupPathData.asMutableStateNotNull()
            val directoryPicker = rememberDirectoryPickerLauncher { directory ->
                if (directory != null) {
                    scope.launch(Dispatchers.PlatformIO) {
                        // wir wollen hier instand update des states! daher update statt value
                        setup.prefs.backupPathData.update(
                            backup.getAutoBackupDirectoryData(
                                directory
                            )
                        )
                        //backupPathData.value = backup.getAutoBackupDirectoryData(directory)
                        backup.onSettingsChanged()
                    }
                }
            }

            PreferenceSection(
                title = stringResource(Res.string.settings_section_auto_backup)
            ) {
                if (backupPathData.value.isEmpty()) {
                    PreferenceItem(
                        title = stringResource(Res.string.settings_auto_backup_info_title),
                        subtitle = stringResource(Res.string.settings_auto_backup_info_details),
                        icon = Icons.Outlined.Info.toIconComposable(),
                        shape = RectangleShape, // damit wird es aus section shape excluded
                        containerColor = Color.Transparent,
                        contentColor = LocalStyledContentColor.current
                    )
                    PreferenceItem(
                        title = stringResource(Res.string.settings_auto_backup_enable),
                        subtitle = stringResource(Res.string.settings_auto_backup_choose_folder),
                        icon = Icons.Outlined.Backup.toIconComposable(),
                        onClick = { directoryPicker.launch() }
                    )
                } else {
                    PreferenceItem(
                        title = stringResource(Res.string.settings_auto_backup_folder),
                        subtitle = backup.getAutoBackupDirectoryPath(backupPathData.value),
                        icon = Icons.Outlined.Folder.toIconComposable(),
                        onClick = { directoryPicker.launch() }
                    )
                    PreferenceItem(
                        title = stringResource(Res.string.settings_auto_backup_frequency),
                        subtitle = autoBackupFrequency.value,
                        icon = Icons.Default.Repeat.toIconComposable(),
                        onClick = { frequencyDialog.show() }
                    )
                }
            }
            if (backupPathData.value.isNotEmpty()) {
                PreferenceItem(
                    title = stringResource(Res.string.settings_auto_backup_disable),
                    icon = Icons.Default.Stop.toIconComposable(),
                    onClick = {
                        scope.launch(Dispatchers.PlatformIO) {
                            //backupPathData.value = ""
                            setup.prefs.backupPathData.update("")
                            backup.onSettingsChanged()
                        }
                    },
                    containerColor = StyledTheme.colorScheme.onBackground,
                    contentColor = StyledTheme.colorScheme.background,
                    supportingColor = StyledTheme.supportingContentColor(
                        contentColor = StyledTheme.colorScheme.background,
                        containerColor = StyledTheme.colorScheme.onBackground
                    )
                )
            }
        }

        BackupDialog(
            dialogState = backupDialog,
            exportFileName = BackupDefaults.getDefaultBackupFileName(
                appName = setup.appData.name,
                extension = backup.config.extension,
                auto = false
            ),
            files = backup.config.backupContent,
            backupManager = backup,
            fileKitDialogSettings = FileKitDialogSettings.createDefault()
        )

        if (frequencyDialog.visible) {
            val frequency = remember(autoBackupFrequency.value) {
                mutableStateOf(
                    if (autoBackupFrequency.value.isEmpty()) {
                        Frequency.Weekly(DayOfWeek.SUNDAY, LocalTime(22, 0), 1)
                    } else {
                        Frequency.deserialize(autoBackupFrequency.value)
                    }
                )
            }
            DialogFrequency(
                state = frequencyDialog,
                frequency = frequency,
                title = { StyledText(stringResource(Res.string.settings_auto_backup_frequency)) },
                buttons = StyledDialogDefaults.buttonOK {
                    scope.launch {
                        setup.prefs.autoBackupFrequency.update(frequency.value.serialize())
                        //autoBackupFrequency.value = frequency.value.serialize()
                        backup.onSettingsChanged()
                    }
                }
            )
        }
    }
}

@Composable
fun MainAppSettings(
    paddingValues: PaddingValues,
    setup: MainAppSettings.Setup = MainAppSettings.Setup(),
    custom: @Composable () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.medium)
    ) {
        // setup
        val filter = MainAppSettings.filter
        val preferenceState = MainAppSettings.preferenceState
        val preferenceStyle = MainAppSettings.preferenceStyle
        val preferenceBreadcrumbRootLabel = MainAppSettings.preferenceBreadcrumbRootLabel

        // 1) Filter (optional)
        if (setup.supportFiltering) {
            StyledInput(
                value = filter.query,
                onValueChange = { filter.query = it },
                modifier = Modifier.fillMaxWidth().padding(preferenceStyle.outerPadding),
                label = { StyledText(stringResource(Res.string.settings_filter_themes_label)) },
            )
        }

        // 2) Breadcrumbs (optional)
        if (setup.breadcrumbs) {
            PreferenceBreadcrumb(
                state = preferenceState,
                modifier = Modifier.fillMaxWidth(),
                filter = filter,
                rootLabel = preferenceBreadcrumbRootLabel
            )
        }

        // 3) Preference Screen
        PreferenceScreen(
            modifier = Modifier.weight(1f),
            state = preferenceState,
            filter = filter,
            style = preferenceStyle
        ) {
            Settings(setup, custom)
        }
    }
}


@Composable
private fun Settings(
    settings: MainAppSettings.Setup,
    custom: @Composable () -> Unit,
) {
    val setup = AppSetup.get()
    val actions = LocalAppActions.current

    val dialogFeedback = rememberDialogState()
    DialogFeedback(dialogFeedback)

    // 1) Header
    MainAppSettingsDefault.MainPrefHeader(settings.headerIconStyle)

    // 2) Style
    if (setup.themeSupport.supportsTheming()) {
        PreferenceSection(
            title = stringResource(Res.string.settings_group_style),

            ) {
            PreferenceGroup(
                title = stringResource(Res.string.settings_theme),
                icon = Icons.Outlined.Style.toIconComposable()
            ) {
                MainAppSettingsDefault.Theming()
            }
        }
    }

    // 3) Custom
    custom()

    // Built-in settings
    val showLanguage = !setup.disableLanguagePicker && Platform.openLanguagePicker != null
    val showChangelog = remember { setup.changelogSetup != null }
    val showBackup = remember { BackupManager.manager?.config?.addToPrefs == true }
    val showFeedback = remember { FeedbackManager.supported && setup.logging.file != null }
    val showMarket = Platform.openMarket != null
    val showPrivacy =
        remember {
            setup.privacyPolicyLink.isNotEmpty() ||
                    (ProVersionManager.setup.supported && AdsManager.manager != null)
        }
    val showDeveloper by setup.debug.prefs.showDeveloperSettings.collectAsStateNotNull()
    val showInfo = showChangelog || showFeedback || showMarket
    if (showLanguage || showInfo || showBackup || showPrivacy || showDeveloper) {
        PreferenceSection(
            title = stringResource(Res.string.settings_section_more_settings),

            ) {
            if (showLanguage) {
                PreferenceItem(
                    title = stringResource(Res.string.settings_change_language),
                    icon = Icons.Default.Language.toIconComposable(),
                    onClick = { Platform.openLanguagePicker?.invoke() }
                )
            }

            if (showInfo) {
                PreferenceGroup(
                    title = stringResource(Res.string.settings_group_infos),
                    icon = Icons.Outlined.Info.toIconComposable()
                ) {
                    PreferenceSection {
                        if (showChangelog) {
                            PreferenceItem(
                                title = stringResource(Res.string.settings_changelog),
                                icon = Icons.AutoMirrored.Outlined.TextSnippet.toIconComposable(),
                                onClick = actions.showChangelog,
                            )
                        }

                        if (showFeedback) {
                            PreferenceItem(
                                title = stringResource(Res.string.settings_feedback),
                                subtitle = stringResource(Res.string.settings_feedback_details),
                                icon = Icons.Default.MailOutline.toIconComposable(),
                                onClick = { dialogFeedback.show() },
                            )
                        }

                        if (showMarket) {
                            PreferenceItem(
                                title = stringResource(Res.string.settings_open_play_store),
                                icon = FontAwesomeIcons.Brands.GooglePlay.toIconComposable(),
                                subtitle = stringResource(Res.string.settings_open_play_store_details),
                                onClick = { Platform.openMarket?.invoke() }
                            )
                        }
                    }
                }
            }

            if (showBackup) {
                PreferenceGroup(
                    title = stringResource(Res.string.settings_group_backup),
                    icon = Icons.Outlined.CloudUpload.toIconComposable()
                ) {
                    MainAppSettingsDefault.Backup()
                }
            }

            if (showPrivacy) {
                PreferenceGroup(
                    title = stringResource(Res.string.settings_group_privacy),
                    icon = Icons.Outlined.PrivacyTip.toIconComposable()
                ) {
                    PreferenceSection {
                        if (setup.privacyPolicyLink.isNotEmpty()) {
                            PreferenceItem(
                                title = stringResource(Res.string.settings_privacy_policy),
                                subtitle = stringResource(Res.string.settings_privacy_policy_details),
                                icon = Icons.Outlined.PrivacyTip.toIconComposable(),
                                onClick = { Platform.openUrl(setup.privacyPolicyLink) },
                            )
                        }
                        if (ProVersionManager.setup.supported && AdsManager.manager != null) {
                            AdsPrivacySettings()
                        }
                    }
                }
            }

            if (showDeveloper)
                MainAppSettingsDefault.DeveloperSettingsGroup()
        }
    }
}