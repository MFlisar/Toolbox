package com.michaelflisar.toolbox.demo.pages.tests

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pages
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material.icons.filled.RunningWithErrors
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.composedialogs.core.DispatcherIO
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyIconButton
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.table.Table
import com.michaelflisar.toolbox.table.TableDefaults
import com.michaelflisar.toolbox.table.TableHeader
import com.michaelflisar.toolbox.table.TableSearchBar
import com.michaelflisar.toolbox.table.data.ColumnWidth
import com.michaelflisar.toolbox.table.data.TableClickHandler
import com.michaelflisar.toolbox.table.data.rememberTableState
import com.michaelflisar.toolbox.table.definitions.Cell
import com.michaelflisar.toolbox.table.definitions.Column
import com.michaelflisar.toolbox.table.definitions.Filter
import com.michaelflisar.toolbox.table.definitions.Header
import com.michaelflisar.toolbox.table.definitions.rememberTableColumns
import com.michaelflisar.toolbox.table.definitions.rememberTableDefinition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Parcelize
object PageTestUI : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        title = "UI",
        subTitle = null,
        icon = Icons.Default.Visibility.toIconComposable()
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
    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize(),
    ) {
        MyRow {
            MyDropdown(
                title = "Test",
                items = listOf("One", "Two", "Three"),
                selected = mutableStateOf(0)
            )
        }
    }

}