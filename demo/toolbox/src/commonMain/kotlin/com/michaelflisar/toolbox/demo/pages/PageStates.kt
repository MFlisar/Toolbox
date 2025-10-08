package com.michaelflisar.toolbox.demo.pages

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pages
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.composethemer.themes.ThemeFlatUIGreensea
import com.michaelflisar.composethemer.themes.ThemeFlatUIOrange
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.Platform
import com.michaelflisar.toolbox.app.AppSetup
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.demo.resources.Res
import com.michaelflisar.toolbox.demo.resources.page_states
import com.michaelflisar.toolbox.extensions.toIconComposable
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

class PageStatesScreenModel : ScreenModel {

    val counter = mutableIntStateOf(0)

    // Optional
    override fun onDispose() {
        // ...
    }
}

@Parcelize
object PageStatesScreen : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        title = stringResource(Res.string.page_states),
        subTitle = "Sub Title",
        icon = Icons.Default.Pages.toIconComposable()
    )

    @Composable
    override fun provideMenu(): List<MenuItem> {
        return emptyList()
    }

    @Composable
    override fun Screen() {
        val navigator = LocalNavigator.currentOrThrow
        val navigatorScreenModel =
            navigator.rememberNavigatorScreenModel { PageStatesScreenModel() }
        val screenModel = rememberScreenModel { PageStatesScreenModel() }
        Page(navigatorScreenModel, screenModel)
    }

}

@Composable
private fun Page(
    navigatorScreenModel: PageStatesScreenModel,
    screenModel: PageStatesScreenModel,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val setup = AppSetup.get()
    val scope = rememberCoroutineScope()

    MyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(all = 16.dp)
            .fillMaxSize()
    ) {
        Text("Page 2")

        val counter = rememberSaveable { mutableIntStateOf(0) }
        MyButton(
            text = "Counter (rememberSaveable): ${counter.value}",
            onClick = { counter.value++ },
        )

        MyButton(
            text = "Counter (screenModel): ${screenModel.counter.value}",
            onClick = { screenModel.counter.value++ },
        )

        MyButton(
            text = "Counter (navigatorScreenModel): ${navigatorScreenModel.counter.value}",
            onClick = { navigatorScreenModel.counter.value++ },
        )

        MyButton(
            text = "Toggle Theme",
            onClick = {
                scope.launch(Platform.DispatcherIO) {
                    if (setup.prefs.customTheme.read() == ThemeFlatUIOrange.Analogic.id) {
                        setup.prefs.customTheme.update(ThemeFlatUIGreensea.Analogic.id)
                    } else {
                        setup.prefs.customTheme.update(ThemeFlatUIOrange.Analogic.id)
                    }
                }
            }
        )
    }
}