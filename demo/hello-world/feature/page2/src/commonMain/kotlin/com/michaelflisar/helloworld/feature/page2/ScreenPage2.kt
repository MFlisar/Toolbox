package com.michaelflisar.helloworld.feature.page2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pages
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.helloworld.core.resources.Res
import com.michaelflisar.helloworld.core.resources.page_2
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.extensions.toIconComposable
import org.jetbrains.compose.resources.stringResource

@Parcelize
object ScreenPage2 : NavScreen() {

    val title = Res.string.page_2

    @Composable
    override fun provideData() = rememberNavScreenData(
        title = stringResource(title),
        subTitle = null,
        icon = Icons.Default.Info.toIconComposable()
    )

    @Composable
    override fun provideMenu(): List<MenuItem> {
        val appState = LocalAppState.current
        val actionText = "Action for ${stringResource(title)}"
        return listOf(
            MenuItem.Group(
                text = stringResource(title),
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
private fun Page() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Hello from ${stringResource(ScreenPage2.title)}!")
    }
}