package com.michaelflisar.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.michaelflisar.composepreferences.styled.layout.PreferenceSection
import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.app.AppScope
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.MainAppSettingsDefault
import com.michaelflisar.toolbox.app.features.ads.AdsManager
import com.michaelflisar.toolbox.app.features.changelog.ChangelogUtil
import com.michaelflisar.toolbox.app.features.logging.ConsoleLoggingSetup
import com.michaelflisar.toolbox.app.features.preferences.BaseDebugPrefs
import com.michaelflisar.toolbox.app.features.update.UpdateManager
import com.michaelflisar.toolbox.demo.BuildKonfig
import com.michaelflisar.toolbox.demo.shared.resources.Res
import com.michaelflisar.toolbox.demo.shared.resources.icon
import org.jetbrains.compose.resources.painterResource

object Shared {

    /*
     * Language Picker:
     *   - res/resources.properties file erstellen
     *   - androidResources { generateLocaleConfig = true } im build.gradle.kts
     *   => sonst geht der language picker nicht
     */
    fun createSetup(
        storageSettings: Storage,
        storageDebug: Storage,
        fileLoggingSetup: IFileLoggingSetup?,
        isDebugBuild: Boolean,
        icon: @Composable () -> Painter = { appIcon(light = true) },
    ) = AppSetup(
        developer = AppSetup.Developer.MFLISAR,
        appData = AppSetup.AppData(
            versionCode = BuildKonfig.versionCode,
            versionName = BuildKonfig.versionName,
            namespace = BuildKonfig.namespace,
            name = BuildKonfig.appName,
        ),
        privacyPolicyLink = "https://www.michaelflisar.com/privacy",
        icon = icon,
        themeSupport = AppSetup.ThemeSupport(),
        prefs = Prefs(storage = storageSettings),
        debug = AppSetup.Debug(
            prefs = BaseDebugPrefs(storage = storageDebug),
            sideSheet = {
                PreferenceSection("Theming", isExpandable = true) {
                    MainAppSettingsDefault.Theming()
                }
            },
            appTesting = AppSetup.AppTesting(
                joinLink = "https://play.google.com/apps/internaltest/4699117358820319983",
                downloadLink = "https://play.google.com/store/apps/details?id=com.michaelflisar.iron&hl=en-US&ah=gXM6YZ-k2FM0dpIUkkr8SOofABo"
            )
        ),
        disableLanguagePicker = false,
        logging = AppSetup.Logging(
            file = fileLoggingSetup,
            console = ConsoleLoggingSetup(
                fixLogTag = "TB"
            )
        ),
        changelogSetup = ChangelogUtil.createSetup(Versions),
        isDebugBuild = isDebugBuild
    )

    fun init() {

        // 1) App Data ggf. updaten
        val updateManager = UpdateManager.create(Versions)
        updateManager.update(AppScope, AppSetup.get())

        // 2) inits
        ToolboxLogging.enableAll()
    }

    @Composable
    fun Init() {
        // Init function
        AdsManager.manager?.Init()
    }

    @Composable
    fun appIcon(light: Boolean): Painter {
        return painterResource(Res.drawable.icon)
    }

}