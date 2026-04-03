package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.ui.MyScrollableColumn
import com.michaelflisar.toolbox.ui.adaptive.AdaptivePrimaryButton

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
        AdaptivePrimaryButton(
            onClick = {
                appState.showToast("Primary Button clicked")
            }
        ) {
            Text("Primary Button")
        }
    }
}