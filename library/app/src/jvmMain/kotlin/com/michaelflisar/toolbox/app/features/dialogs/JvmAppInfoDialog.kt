package com.michaelflisar.toolbox.app.features.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.BaseDialogState
import com.michaelflisar.composedialogs.core.ComposeDialogStyle
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogButtons
import com.michaelflisar.composedialogs.core.DialogDefaults
import com.michaelflisar.composedialogs.core.DialogEvent
import com.michaelflisar.composedialogs.core.DialogOptions
import com.michaelflisar.composedialogs.core.defaultDialogStyle
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.classes.Developer
import com.michaelflisar.toolbox.components.MyLabeledInformationHorizontal
import com.michaelflisar.toolbox.utils.JvmAppMeta

@Composable
fun JvmAppInfoDialog(
    state: BaseDialogState,
    // Custom - Required
    appMeta: JvmAppMeta,
    appData: AppSetup.AppData,
    developer: Developer,
    // Base Dialog - Optional
    title: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    style: ComposeDialogStyle = DialogDefaults.defaultDialogStyle(),
    buttons: DialogButtons = DialogDefaults.buttons(),
    options: DialogOptions = DialogDefaults.options(),
    onEvent: (event: DialogEvent) -> Unit = {},
) {
    if (state.visible) {
        Dialog(
            state = state,
            title = title,
            icon = icon,
            style = style, buttons = buttons,
            options = options,
            onEvent = onEvent
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val labelWidth = 200.dp

                // 1) AppMeta
                Header("App Meta")
                MyLabeledInformationHorizontal(
                    label = "App Type",
                    labelWidth = labelWidth,
                    info = appMeta.getType().info
                )
                MyLabeledInformationHorizontal(
                    label = "File",
                    labelWidth = labelWidth,
                    info = appMeta.file.absolutePath
                )

                // 2) Developer
                Header("Developer")
                MyLabeledInformationHorizontal(
                    label = "Name",
                    labelWidth = labelWidth,
                    info = developer.name
                )

                // 3) AppData
                Header("App Data")
                MyLabeledInformationHorizontal(
                    label = "Name",
                    labelWidth = labelWidth,
                    info = appData.name
                )
                MyLabeledInformationHorizontal(
                    label = "Namespace",
                    labelWidth = labelWidth,
                    info = appData.namespace
                )
                MyLabeledInformationHorizontal(
                    label = "Version Code",
                    labelWidth = labelWidth,
                    info = appData.versionCode.toString()
                )
                MyLabeledInformationHorizontal(
                    label = "Version Name",
                    labelWidth = labelWidth,
                    info = appData.versionName
                )

                // 4) Environment
                Header("Environment")
                MyLabeledInformationHorizontal(
                    label = "Java Home",
                    labelWidth = labelWidth,
                    info = System.getProperty("java.home")
                )
                MyLabeledInformationHorizontal(
                    label = "Java Runtime",
                    labelWidth = labelWidth,
                    info = "${System.getProperty("java.runtime.name")} | ${System.getProperty("java.runtime.version")}"
                )
                MyLabeledInformationHorizontal(
                    label = "Java Vendor",
                    labelWidth = labelWidth,
                    info = System.getProperty("java.vendor")
                )
                MyLabeledInformationHorizontal(
                    label = "Working Directory",
                    labelWidth = labelWidth,
                    info = System.getProperty("user.dir")
                )

            }
        }
    }
}

@Composable
private fun Header(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(4.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        textAlign = TextAlign.Center
    )
}