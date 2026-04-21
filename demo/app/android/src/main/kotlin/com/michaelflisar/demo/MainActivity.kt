package com.michaelflisar.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.ads.AdManager
import com.michaelflisar.toolbox.ads.AdsBanner
import com.michaelflisar.toolbox.ads.BANNER_DEFAULT
import com.michaelflisar.toolbox.app.AndroidActivity
import com.michaelflisar.toolbox.app.AndroidApplication
import com.michaelflisar.toolbox.app.AndroidContainer
import com.michaelflisar.toolbox.app.features.ads.AdsManager
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeAndScaleTransition
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorTransitionPlatformStyle
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.features.proversion.ProState

class MainActivity : AndroidActivity() {

    @Composable
    override fun Content() {

        // Init function
        Shared.Init()

        AndroidApplication(
            screen = Shared.page1
        ) { navigator ->

            // theme + root (drawer state, app state) are available now

            // Scaffold
            AndroidContainer {
                Shared.Content(
                    navigator
                ) {
                    Column {
                        // Content
                        Box(modifier = Modifier.weight(1f)) {
                            AppNavigatorTransitionPlatformStyle(navigator)
                            // AppNavigatorFadeAndScaleTransition(navigator)
                            //AppNavigatorFadeTransition(navigator)
                        }
                        // Ads
                        val proState = ProVersionManager.proState.collectAsState()
                        val adUnitId =
                            AdManager.BANNER_DEFAULT // TODO: mit App spezifischer Banner ID ersetzen
                        AdsBanner(
                            adManager = AdsManager.manager,
                            adUnitId = adUnitId,
                            visible = proState.value == ProState.No
                        )
                    }
                }
            }
        }
    }
}
