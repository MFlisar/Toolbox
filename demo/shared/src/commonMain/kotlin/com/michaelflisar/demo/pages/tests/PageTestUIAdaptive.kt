package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyFlowRow
import com.michaelflisar.toolbox.components.MyText
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.ui.MyScrollableColumn
import com.michaelflisar.toolbox.ui.adaptive.AdaptiveBottomBar
import com.michaelflisar.toolbox.ui.adaptive.AdaptiveBottomBarItem
import com.michaelflisar.toolbox.ui.adaptive.AdaptiveButton
import com.michaelflisar.toolbox.ui.adaptive.AdaptiveNavigationBar
import com.michaelflisar.toolbox.ui.adaptive.AdaptiveSwitch

@Parcelize
object PageTestUIAdaptive : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        name = "UI (Adaptive)",
        icon = Icons.Default.Visibility.toIconComposable()
    )

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

    MyScrollableColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize()
    ) {
        MyText(text = "Adaptive Buttons")
        MyFlowRow {
            AdaptiveButton.Variant.entries.forEach {
                AdaptiveButton(
                    variant = it,
                    onClick = { appState.showToast("${it.name} Button clicked") }
                ) {
                    Text("${it.name} Button")
                }
            }
        }
        MyText(text = "Adaptive BottomBar")
        AdaptiveBottomBar(
            items = listOf(
                AdaptiveBottomBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = "Home",
                    selected = true,
                    onClick = { appState.showToast("Home clicked") }
                ),
                AdaptiveBottomBarItem(
                    icon = { Icon(Icons.Default.Search, null) },
                    label = "Search",
                    selected = false,
                    onClick = { appState.showToast("Search clicked") }
                ),
                AdaptiveBottomBarItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = "Profile",
                    selected = false,
                    onClick = { appState.showToast("Profile clicked") }
                )
            )
        )
        MyText(text = "Adaptive Navigation")
        AdaptiveNavigationBar(
            title = "Navigation Title",
            onBack = { appState.showToast("Navigation clicked") },
            actions = {
                // TODO:
                AdaptiveButton(
                    variant = AdaptiveButton.Variant.Default,
                    onClick = { appState.showToast("Action clicked") }
                ) {
                    Text("Action")
                }
            }
        )
        MyText("Adaptive Switch")
        MyFlowRow {
            AdaptiveSwitch.Variant.entries.forEach {
                val checked = remember { mutableStateOf(false) }
                AdaptiveSwitch(
                    variant = it,
                    checked = checked.value,
                    onCheckedChange = { checked.value = it; }
                )
            }
        }
    }
}