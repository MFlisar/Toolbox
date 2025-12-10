package com.michaelflisar.toolbox.demo.pages.tests

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.ViewSidebar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.pane.PaneContainer
import com.michaelflisar.toolbox.feature.pane.rememberPane

@Parcelize
object PageTestPane : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        name = "Pane",
        icon = Icons.Default.ViewSidebar.toIconComposable()
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

    PaneContainer(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize(),
        left = rememberPane("Left") {
            MyColumn {
                Text("Left Pane Content")
            }
        },
        right = rememberPane("Right") {
            MyColumn {
                Text("Right Pane Content")
            }
        }
    ) {
        MyColumn(
            modifier = Modifier.padding(all = 16.dp)
        ) {
            Text("Center Content")
        }
    }
}