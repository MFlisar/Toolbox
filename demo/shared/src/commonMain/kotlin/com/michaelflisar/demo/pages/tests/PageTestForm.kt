package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.FolderOpen
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveInfo
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composestyled.ui.components.StyledButton
import com.michaelflisar.composestyled.ui.layout.StyledDialog
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.composestyled.ui.specials.StyledExpandableSection
import com.michaelflisar.composestyled.ui.state.rememberDialogState
import com.michaelflisar.demo.pages.DemoToolbarConfig
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.form.FormDefaults
import com.michaelflisar.toolbox.form.MyForm
import com.michaelflisar.toolbox.form.rememberFormConfig
import com.michaelflisar.toolbox.form.items.FormCheckbox
import com.michaelflisar.toolbox.form.items.FormCustom
import com.michaelflisar.toolbox.form.items.FormCustomLabeled
import com.michaelflisar.toolbox.form.items.FormDivider
import com.michaelflisar.toolbox.form.items.FormDropdown
import com.michaelflisar.toolbox.form.items.FormDropdownIndex
import com.michaelflisar.toolbox.form.items.FormHelp
import com.michaelflisar.toolbox.form.items.FormInfo
import com.michaelflisar.toolbox.form.items.FormInput
import com.michaelflisar.toolbox.form.items.FormMultiDropdown
import com.michaelflisar.toolbox.form.items.FormMultiDropdownIndex
import com.michaelflisar.toolbox.form.items.FormPicker
import com.michaelflisar.toolbox.form.items.FormSection
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.serialization.Serializable

@Serializable
data object PageTestForm : NavScreen {

    @Composable
    override fun Content(
        paddingValues: PaddingValues,
        adaptiveInfo: State<StyledAdaptiveInfo>,
    ) = PageTestFormContent(paddingValues)

    @Composable
    override fun Toolbar() = DemoToolbarConfig("Form")
}

private data class PlanOption(val name: String)

@Composable
private fun PageTestFormContent(
    paddingValues: PaddingValues
) {

    val name = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val notes = rememberSaveable { mutableStateOf("") }
    val showEmail = rememberSaveable { mutableStateOf(false) }
    val selectedPlan = remember { mutableStateOf(PlanOption("Standard")) }
    val selectedCountryIndex = rememberSaveable { mutableStateOf(0) }
    val selectedInterests = remember { mutableStateOf(listOf("Compose")) }
    val selectedNotificationIndices = remember { mutableStateOf(listOf(0)) }
    val selectedSingleOptionIndex = rememberSaveable { mutableStateOf(0) }
    val labelLayoutIndex = rememberSaveable { mutableStateOf(0) }
    val sectionStyleIndex = rememberSaveable { mutableStateOf(0) }
    val showSingleDropdownsAsText = rememberSaveable { mutableStateOf(false) }
    val formSettingsExpanded = rememberSaveable { mutableStateOf(true) }
    val customActionCount = rememberSaveable { mutableStateOf(0) }
    val selectedFile = remember { mutableStateOf<PlatformFile?>(null) }
    val selectedFolder = remember { mutableStateOf<PlatformFile?>(null) }
    val submitDialogState = rememberDialogState<String>(null)

    val plans = remember {
        listOf(
            PlanOption("Basic"),
            PlanOption("Standard"),
            PlanOption("Premium"),
        )
    }
    val countries = listOf("Germany", "France", "Japan")
    val interests = listOf("Compose", "Kotlin", "Multiplatform", "Design")
    val notificationOptions = listOf("Product updates", "Tips", "Release notes")
    val labelLayouts = listOf("Beside field", "Above field")
    val sectionStyles = listOf("Text heading", "Filled row")
    val fileKitDialogSettings = remember { FileKitDialogSettings.createDefault() }
    val filePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.File(extensions = listOf("pdf", "txt")),
        dialogSettings = fileKitDialogSettings,
    ) { selectedFile.value = it }
    val directoryPickerLauncher = rememberDirectoryPickerLauncher(
        dialogSettings = fileKitDialogSettings,
    ) { selectedFolder.value = it }

    val formConfig = rememberFormConfig(
        labelStyle = if (labelLayoutIndex.value == 1) {
            FormDefaults.labelStyleAbove()
        } else {
            FormDefaults.labelStyleBefore()
        },
        sectionStyle = if (sectionStyleIndex.value == 1) {
            FormDefaults.sectionStyleRow()
        } else {
            FormDefaults.sectionStyleText()
        },
        showSingleDropdownsAsText = showSingleDropdownsAsText.value,
    )
    val defaultItemKey = remember { FormDefaults.simpleKeyProvider() }

    val items = listOf(
        FormSection("Profile"),
        FormInput(
            label = "Name",
            value = name,
            subLabel = "Required",
            help = FormHelp("Enter the name that should appear in the submission."),
        ),
        FormCheckbox(
            label = "Include an email address",
            checked = showEmail,
        ),
        FormInput(
            label = "Email",
            value = email,
            visible = showEmail.value,
            enabled = showEmail.value,
            help = FormHelp("This field is shown conditionally."),
        ),
        FormInput(
            label = "Notes",
            value = notes,
            minLines = 2,
            maxLines = 4,
            subLabel = "Optional",
        ),
        FormDropdown(
            label = "Plan",
            items = plans,
            mapper = { it.name },
            selected = selectedPlan,
            help = FormHelp("Generic dropdown with a custom option type."),
        ),
        FormDropdownIndex(
            label = "Country",
            items = countries,
            selectedIndex = selectedCountryIndex,
        ),
        FormDropdownIndex(
            label = "Single option",
            items = listOf("Only available option"),
            selectedIndex = selectedSingleOptionIndex,
        ),
        FormMultiDropdown(
            label = "Interests",
            items = interests,
            selected = selectedInterests,
            formatter = { selected -> selected.joinToString(" + ") },
        ),
        FormMultiDropdownIndex(
            label = "Notifications",
            items = notificationOptions,
            selectedIndices = selectedNotificationIndices,
        ),
        FormDivider("File selection"),
        FormPicker(
            label = "File",
            text = selectedFile.value?.path ?: "Choose a PDF or text file",
            icon = Icons.Default.AttachFile.toIconComposable(),
            onClick = { filePickerLauncher.launch() },
        ),
        FormPicker(
            label = "Folder",
            text = selectedFolder.value?.path ?: "Choose a folder",
            icon = Icons.Default.FolderOpen.toIconComposable(),
            onClick = { directoryPickerLauncher.launch() },
        ),
        FormSection("Custom form items"),
        FormCustomLabeled(
            label = "Custom labeled item",
            content = { _, enabled ->
                StyledText(if (enabled) "Content from a custom composable" else "Disabled")
            },
        ),
        FormCustom(
            content = { _, enabled ->
                StyledButton(enabled = enabled, onClick = { customActionCount.value++ }) {
                    StyledText("Custom item (${customActionCount.value})")
                }
            },
        ),
        FormSection("Live preview"),
        FormInfo(
            label = "Name",
            info = name.value.ifBlank { "No name entered" },
        ),
        FormInfo(
            label = "File",
            info = selectedFile.value?.path ?: "No file selected",
        ),
        FormInfo(
            label = "Folder",
            info = selectedFolder.value?.path ?: "No folder selected",
        ),
    )

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(paddingValues)
            .padding(StyledTheme.padding.content),
        verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.medium),
    ) {
        StyledExpandableSection(
            modifier = Modifier
                .fillMaxWidth(),
            title = { StyledText("Form settings") },
            expanded = formSettingsExpanded,
        ) {
            MyForm(
                items = listOf(
                    FormDropdownIndex(
                        label = "Label layout",
                        items = labelLayouts,
                        selectedIndex = labelLayoutIndex,
                    ),
                    FormDropdownIndex(
                        label = "Section style",
                        items = sectionStyles,
                        selectedIndex = sectionStyleIndex,
                    ),
                    FormCheckbox(
                        label = "Show single-option dropdowns as text",
                        checked = showSingleDropdownsAsText,
                    ),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = StyledTheme.spacing.medium),
                scrollable = false,
            )
        }
        MyForm(
            items = items,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            config = formConfig,
            itemKey = { item ->
                if (item is FormCustom) "custom:action" else defaultItemKey(item)
            },
        )
        StyledButton(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                submitDialogState.show(
                    listOf(
                        "Name: ${name.value.ifBlank { "(empty)" }}",
                        "Email: ${if (showEmail.value) email.value.ifBlank { "(empty)" } else "(not included)"}",
                        "Plan: ${selectedPlan.value.name}",
                        "Country: ${countries[selectedCountryIndex.value]}",
                        "Interests: ${selectedInterests.value.joinToString().ifBlank { "(none)"} }",
                        "Notifications: ${selectedNotificationIndices.value.map(notificationOptions::get).joinToString().ifBlank { "(none)" }}",
                        "File: ${selectedFile.value?.path ?: "(none)"}",
                        "Folder: ${selectedFolder.value?.path ?: "(none)"}",
                        "Notes: ${notes.value.ifBlank { "(none)" }}",
                    ).joinToString("\n")
                )
            },
        ) {
            StyledText("Submit")
        }
    }

    if (submitDialogState.visible) {
        val result = submitDialogState.requireData()
        StyledDialog(
            state = submitDialogState,
            title = { StyledText("Submitted form") },
            buttons = StyledDialog.Buttons(
                positive = StyledDialog.Button(
                    label = "Close",
                    onClick = { submitDialogState.dismiss() },
                ),
            ),
        ) { dialogPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dialogPadding)
                    .padding(StyledTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.small),
            ) {
                result.lines().forEach { line ->
                    StyledText(line)
                }
            }
        }
    }
}
