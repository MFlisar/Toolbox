package com.michaelflisar.toolbox.app.features.preferences.groups

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.michaelflisar.composepreferences.core.PreferenceCustom
import com.michaelflisar.composepreferences.core.classes.LocalPreferenceSettings
import com.michaelflisar.composepreferences.core.classes.PreferenceSettings
import com.michaelflisar.composepreferences.core.scopes.PreferenceGroupScope
import com.michaelflisar.composepreferences.core.styles.ModernStyle
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.App
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.by_name
import com.michaelflisar.toolbox.extensions.secondary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreferenceGroupScope.SettingsHeader(
    settings: PreferenceSettings,
) {
    val setup = AppSetup.get()
    val debugPrefs = setup.debugPrefs
    val appState = LocalAppState.current

    val scope = rememberCoroutineScope()

    // Header soll nicht eingerückt sein bei modernem Style!
    PreferenceCustom(
        itemStyle = LocalPreferenceSettings.current.style.defaultItemStyle.let {
            if (settings.style is ModernStyle) {
                it.copy(outerPadding = PaddingValues(vertical = 16.dp))
            } else it
        },
        onLongClick = {
            scope.launch(Platform.DispatcherIO) {
                val enabled = debugPrefs.showDeveloperSettings.read()
                debugPrefs.showDeveloperSettings.update(!enabled)
                withContext(Dispatchers.Main) {
                    appState.showToast(
                        "Developer settings ${if (enabled) "disabled" else "enabled"}!"
                    )
                }
            }
        }
    ) {
        SettingsHeaderContent(Modifier.fillMaxWidth())
    }
}

@Composable
fun SettingsHeaderContent(
    modifier: Modifier = Modifier,
) {
    val setup = AppSetup.get()
    val versionCode = setup.versionCode
    val versionName = setup.versionName
    MyColumn(
        modifier = modifier
    ) {
        MyRow {
            Image(
                painter = setup.icon(),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(setup.name),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "$versionName ($versionCode)",
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.secondary()
                )
                Text(
                    text = stringResource(Res.string.by_name, setup.developer.name),
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = LocalContentColor.current.secondary()
                )
            }
        }
    }
}