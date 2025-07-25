package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pages
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.CommonApp
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.debugdrawer.LocalDebugDrawerState
import com.michaelflisar.toolbox.app.features.device.CurrentDevice
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.lastNavItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.app.platform.kill
import com.michaelflisar.toolbox.app.platform.localContext
import com.michaelflisar.toolbox.app.platform.restart
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyButtonDefaults
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyFlowRow
import com.michaelflisar.toolbox.components.MyLabeledInformationHorizontal
import com.michaelflisar.toolbox.components.MyTitle
import com.michaelflisar.toolbox.demo.resources.Res
import com.michaelflisar.toolbox.demo.resources.page_home
import com.michaelflisar.toolbox.extensions.toIconComposable
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Parcelize
object PageHomeScreen : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        title = stringResource(Res.string.page_home),
        subTitle = null,
        icon = Icons.Default.Home.toIconComposable()
    )

    @Composable
    override fun provideMenu(): List<MenuItem> {
        val appState = LocalAppState.current
        val actionText = "Page Home Action"
        return listOf(
            MenuItem.Group(
                text = stringResource(com.michaelflisar.toolbox.demo.resources.Res.string.page_home),
                icon = Icons.Default.Pages.toIconComposable(),
                items = listOf(
                    MenuItem.Item(
                        text = actionText,
                        icon = Icons.Default.RunCircle.toIconComposable(),
                    ) {
                        appState.showSnackbar("$actionText clicked")
                    }
                )
            )
        )
    }

    @Composable
    override fun Screen() {
        Page()
    }

}

@Composable
private fun Page(
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val appState = LocalAppState.current
    val setup = CommonApp.setup
    val scope = rememberCoroutineScope()
    val drawerState = LocalDebugDrawerState.current

    val navigator = LocalNavigator.currentOrThrow
    val lastNavItem = navigator.lastNavItem

    MyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize()
    ) {
        val labelWidth = 192.dp
        MyTitle(
            "Platform Information",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        MyLabeledInformationHorizontal(
            label = "Device",
            labelWidth = labelWidth
        ) {
            Text("${CurrentDevice.base}::${CurrentDevice.name}")
        }

        val context = Platform.localContext()
        MyTitle(
            "Test Actions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        MyFlowRow {
            MyButton(
                onClick = {
                    scope.launch {
                        if (setup.debugPrefs.showDebugDrawer.read())
                            drawerState.drawerState.open()
                        else
                            appState.showSnackbar("Debug Drawer is disabled in the settings!")
                    }
                },
                style = MyButtonDefaults.styleOutlined()
            ) {
                Text("Open Drawer")
            }
            if (Platform.restart != null) {
                MyButton(
                    onClick = {
                        Platform.restart?.invoke(context)
                    },
                    style = MyButtonDefaults.styleOutlined()
                ) {
                    Text("Restart App")
                }
            }
            if (Platform.kill != null) {
                MyButton(
                    onClick = {
                        Platform.kill?.invoke(context)
                    },
                    style = MyButtonDefaults.styleOutlined()
                ) {
                    Text("Kill App")
                }
            }
        }
    }
}