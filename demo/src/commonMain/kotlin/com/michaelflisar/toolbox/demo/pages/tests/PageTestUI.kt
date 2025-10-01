package com.michaelflisar.toolbox.demo.pages.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyInputButton
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.extensions.toIconComposable

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

    MyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize()
    ) {
        MyRow(
            modifier = Modifier
                .background(Color.Red)
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyDropdown(
                modifier = Modifier.width(200.dp),
                title = "Dropdown",
                items = listOf("One", "Two", "Three"),
                selected = remember { mutableStateOf(0) },
                style = MyDropdown.Style.OutlinedButton
            )
            MyInput(
                modifier = Modifier.width(128.dp),
                title = "Input",
                value = remember { mutableStateOf("") },
            )
            MyInputButton(
                title = "Button",
                modifier = Modifier.width(128.dp),
                value = "Text...",
                onClick = {
                    appState.showToast("InputButton clicked")
                }
            )
        }
        MyRow {
            MyDropdown(
                modifier = Modifier.width(128.dp),
                title = "Button Style",
                items = listOf("One", "Two", "Three"),
                selected = remember { mutableStateOf(0) }
            )
        }
    }

}