package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreenContainer
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyRow
import com.michaelflisar.toolbox.demo.pages.tests.PageTestPane
import com.michaelflisar.toolbox.demo.pages.tests.PageTestTable
import com.michaelflisar.toolbox.extensions.Render
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.ui.MyScrollableColumn

@Parcelize
object PageTestsRootScreenContainer : NavScreenContainer(rootScreen = PageTestsRootScreen)

@Parcelize
object PageTestsRootScreen : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        title = "Tests",
        subTitle = null,
        icon = Icons.Default.List.toIconComposable()
    )

    @Composable
    override fun provideMenu(): List<MenuItem> {
        val appState = LocalAppState.current
        val actionText = "Page Tests Root Action"
        return listOf(
            MenuItem.Group(
                text = "Root Test Actions",
                icon = Icons.Default.List.toIconComposable(),
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
    MyScrollableColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize()
    ) {
        val tests = listOf(
            PageTestTable,
            PageTestPane
        )
        tests.forEach { TestRow(it) }
    }
}

@Composable
private fun TestRow(
    test: NavScreen
) {
    val navigator = LocalNavigator.currentOrThrow
    MyRow(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                navigator.push(test)
            }
            .padding(all = 8.dp)
    ) {
        val data = test.provideData().value
        data.icon?.Render()
        Text(data.title)
    }
}