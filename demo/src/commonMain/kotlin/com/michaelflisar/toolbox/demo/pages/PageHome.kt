package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyButtonDefaults
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyLabeledInformationHorizontal
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.components.MyTitle
import kotlinx.coroutines.launch

object PageHomeScreen : Screen {
    @Composable
    override fun Content() {
        PageHome()
    }
}

@Composable
private fun PageHome(
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val appState = LocalAppState.current
    val scope = rememberCoroutineScope()
    val drawerState = LocalDebugDrawerState.current

    MyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
    ) {
        val labelWidth = 192.dp
        MyTitle("Platform Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        MyLabeledInformationHorizontal(label = "Base", labelWidth = labelWidth) { Text(CurrentDevice.base.name) }
        MyLabeledInformationHorizontal(label = "Platform", labelWidth = labelWidth) { Text(CurrentDevice.name) }

        MyTitle("Test Actions", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        MyRow {
            MyButton(
                onClick = { appState.toolbar.title.value = "New Title" },
                style = MyButtonDefaults.styleOutlined()
            ) {
                Text("Change Title")
            }
            MyButton(
                onClick = {
                    scope.launch(Platform.DispatcherIO) {
                        CommonApp.setup.prefs.lastShownVersionForChangelog.update(1L)
                    }
                },
                style = MyButtonDefaults.styleOutlined()
            ) {
                Text("Set lastShownVersionForChangelog to 1")
            }
            MyButton(
                onClick = {
                    scope.launch {
                        drawerState.drawerState.open()
                    }
                },
                style = MyButtonDefaults.styleOutlined()
            ) {
                Text("Open Drawer")
            }
        }

        val lastShownVersionForChangelog = CommonApp.setup.prefs.lastShownVersionForChangelog.collectAsStateNotNull()
        MyTitle("Infos", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        MyLabeledInformationHorizontal(label = "lastShownVersionForChangelog", labelWidth = labelWidth) { Text(lastShownVersionForChangelog.value.toString()) }

    }
}