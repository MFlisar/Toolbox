package com.michaelflisar.toolbox.demo.pages.tests

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.PreferenceSection
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.classes.rememberPreferenceState
import com.michaelflisar.composepreferences.core.styles.ModernStyle
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composepreferences.screen.button.PreferenceButton
import com.michaelflisar.composepreferences.screen.color.PreferenceColor
import com.michaelflisar.composepreferences.screen.date.PreferenceDate
import com.michaelflisar.composepreferences.screen.input.PreferenceInputNumber
import com.michaelflisar.composepreferences.screen.input.PreferenceInputText
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.michaelflisar.composepreferences.screen.list.PreferenceListMulti
import com.michaelflisar.composepreferences.screen.number.PreferenceNumber
import com.michaelflisar.composepreferences.screen.time.PreferenceTime
import com.michaelflisar.kotpreferences.compose.asMutableStateNotNull
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.core.interfaces.Storage
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.app.features.preferences.Preferences
import com.michaelflisar.toolbox.app.features.preferences.createStorage
import com.michaelflisar.toolbox.extensions.toIconComposable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Parcelize
object PageTestPreferences : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        title = "Preferences",
        subTitle = null,
        icon = Icons.Default.Settings.toIconComposable()
    )

    @Composable
    override fun provideMenu(): List<MenuItem> = emptyList()

    @Composable
    override fun Screen() {
        Page()
    }

}

@Composable
private fun Page(
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val prefs = Prefs.INSTANCE

    val style = ModernStyle.create()
    val settings =
        PreferenceSettingsDefaults.settings(style = style, toggleBooleanOnItemClick = true)
    val state = rememberPreferenceState()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            PageTestPreferences.provideData().value.title,
            style = MaterialTheme.typography.titleMedium
        )

        PreferenceScreen(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            settings = settings,
            state = state
        ) {
            PreferenceSection(
                title = "Boolean"
            ) {
                PreferenceBool(
                    style = PreferenceBool.Style.Checkbox,
                    value = prefs.bool.asMutableStateNotNull(),
                    title = "Bool Checkbox",
                )
                PreferenceBool(
                    style = PreferenceBool.Style.Switch,
                    value = prefs.bool.asMutableStateNotNull(),
                    title = "Bool Switch"
                )
            }
            PreferenceSection(
                title = "Input"
            ) {
                PreferenceInputText(
                    value = prefs.input.asMutableStateNotNull(),
                    title = "Input Text",
                )
                PreferenceInputNumber(
                    value = prefs.inputNumber.asMutableStateNotNull(),
                    title = "Input Number",
                )
            }
            PreferenceSection(
                title = "Number"
            ) {
                PreferenceNumber(
                    style = PreferenceNumber.Style.Slider(),
                    value = prefs.inputNumber2.asMutableStateNotNull(),
                    min = 0,
                    max = 100,
                    stepSize = 1,
                    title = "Number (Slider)",
                )
                PreferenceNumber(
                    style = PreferenceNumber.Style.Picker,
                    value = prefs.inputNumber2.asMutableStateNotNull(),
                    min = 0,
                    max = 100,
                    stepSize = 1,
                    title = "Number (Picker)",
                )
            }
            PreferenceSection(
                title = "List"
            ) {
                val listItems = listOf("Item 1", "Item 2", "Item 3")
                PreferenceList(
                    style = PreferenceList.Style.Dialog(),
                    value = prefs.listSingle.asMutableStateNotNull(
                        mapper = { listItems.get(it) },
                        unmapper = { listItems.indexOf(it) }
                    ),
                    items = listItems,
                    title = "List Single Select (Dialog)"
                )
                PreferenceList(
                    style = PreferenceList.Style.Spinner,
                    value = prefs.listSingle.asMutableStateNotNull(
                        mapper = { listItems.get(it) },
                        unmapper = { listItems.indexOf(it) }
                    ),
                    items = listItems,
                    title = "List Single Select (Spinner)"
                )
                PreferenceListMulti(
                    value = prefs.listMulti.asMutableStateNotNull(
                        mapper = { it.map { listItems.get(it) } },
                        unmapper = { it.map { listItems.indexOf(it) }.toSet() }
                    ),
                    items = listItems,
                    title = "List Multi Select"
                )
            }
            PreferenceSection(
                title = "Others"
            ) {
                PreferenceButton(
                    onClick = {
                        // ... TODO: common snackbar oder toast handler in base app!
                        // eventuell mittels einem globalen Toast Handler??? und CompisitionLocal für Snackbar
                    },
                    title = "Button"
                )
                PreferenceColor(
                    value = prefs.color.asMutableStateNotNull(
                        mapper = { Color(it) },
                        unmapper = { it.toArgb() }
                    ),
                    title = "Color"
                )
                PreferenceDate(
                    value = prefs.date.asMutableStateNotNull(
                        mapper = { LocalDate.fromEpochDays(it) },
                        unmapper = { it.toEpochDays().toInt() }
                    ),
                    title = "Date",
                )
                PreferenceTime(
                    value = prefs.time.asMutableStateNotNull(
                        mapper = { LocalTime.fromSecondOfDay(it) },
                        unmapper = { it.toSecondOfDay() }
                    ),
                    title = "Time",
                )
            }
        }
    }
}

class Prefs(storage: Storage) : SettingsModel(storage) {

    companion object {
        val INSTANCE: Prefs by lazy {
            Prefs(Preferences.createStorage("tests"))
        }
    }
    val bool by boolPref()
    val color by intPref()
    val date by intPref()
    val time by intPref()
    val input by stringPref()
    val inputNumber by intPref(0)
    val inputNumber2 by intPref(0)
    val listSingle by intPref(0)
    val listMulti by intSetPref(setOf(0))
}