package com.michaelflisar.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.michaelflisar.toolbox.ads.AdManager
import com.michaelflisar.toolbox.ads.AdsBanner
import com.michaelflisar.toolbox.ads.BANNER_DEFAULT
import com.michaelflisar.toolbox.app.AndroidActivity
import com.michaelflisar.toolbox.app.features.ads.AdsManager
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.features.proversion.ProState

class MainActivity : AndroidActivity() {

    @Composable
    override fun Content() {

        DemoApp(
            banner = {
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
        )
    }
}
