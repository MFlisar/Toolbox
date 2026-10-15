package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.form3.FormDefaults
import com.michaelflisar.toolbox.form3.FormHelp
import com.michaelflisar.toolbox.form3.FormItem
import com.michaelflisar.toolbox.form3.MyForm
import com.michaelflisar.toolbox.form3.rememberFormConfig
import com.michaelflisar.toolbox.padding

@Parcelize
object PageTestForm : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        name = "Form",
        icon = Icons.Default.TextFields.toIconComposable()
    )

    @Composable
    override fun Screen() {
        Page()
    }

}

private enum class Mode {
    Copy, Change, Info
}

private enum class TransferMode {
    Copy, Move
}

@Composable
private fun Page() {

    val labelStyleBefore = FormDefaults.labelStyleBefore(
        width = 200.dp,
        textStyle = MaterialTheme.typography.bodyMedium,
        subTextStyle = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
        textColor = MaterialTheme.colorScheme.onSurface,
        subTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val labelStyleAbove = FormDefaults.labelStyleAbove(
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        subTextStyle = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
        textColor = MaterialTheme.colorScheme.onSurface,
        subTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    val sectionStyleText = FormDefaults.sectionStyleText()
    val sectionStyleRow = FormDefaults.sectionStyleRow()

    val dividerStyle = FormDefaults.dividerStyle(
        color = MaterialTheme.colorScheme.primary,
        textColor = MaterialTheme.colorScheme.primary,
        thickness = 0.dp,
        textAlignment = Alignment.Start
    )

    val config = rememberFormConfig(
        labelStyle = labelStyleBefore,
        sectionStyle = sectionStyleRow,
        dividerStyle = dividerStyle,
        spacing = 8.dp,
        showSingleDropdownsAsText = true
    )

    val existingFolders = remember { mutableStateOf(listOf<String>()) }
    val currentJunction = remember { mutableStateOf<String?>(null) }
    val selectedMode = remember { mutableStateOf(Mode.Copy) }
    val selectedTerminalServer = remember { mutableStateOf(0) }
    val selectedVWVersion = remember { mutableStateOf(0) }
    val selectedExistingFolder = remember { mutableStateOf<String?>(null) }
    val folderName = remember { mutableStateOf("Folder") }
    val copyMode = remember { mutableStateOf(TransferMode.Copy) }
    val copyLocalWorkspace = remember { mutableStateOf(false) }
    val copyFilesFromVWInstallation = remember { mutableStateOf(false) }
    val flagsDataContent = remember { mutableStateOf("") }
    val activate = remember { mutableStateOf(false) }
    val activateDependency = remember { mutableStateOf(false) }

    val items = listOf(
        FormItem.Section(
            title = "Informationen",
            items = listOf(
                FormItem.Info(
                    label = "Neuester Release Ordner",
                    info = existingFolders.value.lastOrNull() ?: "--",
                ),
                FormItem.Info(
                    label = "Aktuelle Junction",
                    info = currentJunction.value ?: "--",
                    help = FormHelp(
                        title = "Junction",
                        text = "Die Junction ist ein Verweis auf den Ordner, der aktuell von VW verwendet wird. Wenn du die Junction anpasst, dann wird VW den neuen Ordner verwenden."
                    )
                ),
                FormItem.Info(
                    label = "Aktuelle Junction - mit langem Titel und Hilfe... und noch mehr Text, damit man sieht, wie das aussieht",
                    info = currentJunction.value ?: "--",
                    help = FormHelp(
                        title = "Junction",
                        text = "Die Junction ist ein Verweis auf den Ordner, der aktuell von VW verwendet wird. Wenn du die Junction anpasst, dann wird VW den neuen Ordner verwenden."
                    )
                )
            )
        ),
        FormItem.Section(
            title = "Aktion",
            items = listOf(
                FormItem.Dropdown(
                    label = "Modus",
                    items = Mode.entries,
                    mapper = { it.name },
                    selected = selectedMode
                ),
                FormItem.DropdownIndex(
                    label = "Terminal Server",
                    items = listOf(
                        "TS1",
                        "TS2",
                        "TS3"
                    ),
                    selectedIndex = selectedTerminalServer
                ),
                FormItem.DropdownIndex(
                    label = "VW Version",
                    items = listOf(
                        "VW19",
                        "VW21",
                        "VW25"
                    ),
                    selectedIndex = selectedVWVersion
                )
            )
        )
    ) + FormItem.Section(
        "Konfiguration",
        items = when (selectedMode.value) {
            Mode.Copy -> listOf(
                FormItem.Input(
                    label = "Ordner Name",
                    value = folderName
                ),
                FormItem.Dropdown(
                    label = "Kopiermodus",
                    items = TransferMode.entries,
                    mapper = { it.name },
                    selected = copyMode
                ),
                FormItem.Checkbox(
                    label = "Lokalen Workspace kopieren",
                    checked = copyLocalWorkspace
                ),
                FormItem.Checkbox(
                    label = "Dateien aus VW Installation kopieren",
                    subLabel = "DLLs, ...",
                    checked = copyFilesFromVWInstallation
                ),
                FormItem.Input(
                    label = "flags.data Inhalt",
                    value = flagsDataContent,
                    minLines = 2,
                    maxLines = 5
                )
            )

            Mode.Change -> listOf(
                FormItem.Dropdown(
                    label = "Ordner wählen",
                    items = existingFolders.value,
                    mapper = { it ?: "" },
                    selected = selectedExistingFolder
                )
            )

            Mode.Info -> emptyList()
        },
        visible = selectedMode.value != Mode.Info
    ) + FormItem.Section(
        "Sonstiges",
        items = when (selectedMode.value) {
            Mode.Copy -> listOf(
                FormItem.Checkbox(
                    label = "Aktivieren",
                    subLabel = "Junction anpassen + weitere Daten nach VW kopieren",
                    checked = activate
                ),
                FormItem.Checkbox(
                    label = "Dependency",
                    subLabel = "Depends on 'Aktivieren'",
                    checked = activateDependency,
                    visible = activate.value
                )
            )

            Mode.Change -> emptyList()
            Mode.Info -> emptyList()
        },
        visible = selectedMode.value == Mode.Copy
    ) + FormItem.Section(
        "Test",
        items = listOf(
            FormItem.MultiDropdown(
                label = "Multi Dropdown",
                items = TransferMode.entries.map { it.name },
                selected = remember { mutableStateOf(listOf<String>()) },
                formatter = {
                    if (it.isEmpty()) {
                        "Nichts ausgewählt"
                    } else {
                        "${it.size} ausgewählt (" + it.joinToString(", ") + ")"
                    }
                }
            ),
            FormItem.Divider(text = "Divider mit Text"),
            FormItem.CustomLabeled(
                label = "Benutzerdefiniert",
                content = { _, _ ->
                    MyRow {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Benutzerdefinierter Inhalt in Labeled FormItem",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            ),
            FormItem.Custom(
                content = { _, _ ->
                    MyRow {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Benutzerdefinierter Full-Row-Inhalt",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        ),
        visible = selectedMode.value == Mode.Copy
    )
    MyForm(
        config = config,
        items = items,
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.padding.content)
        //scrollable = false
    )
}