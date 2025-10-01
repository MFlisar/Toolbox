package com.michaelflisar.helloworld.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresPermission
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import com.michaelflisar.helloworld.SharedDefinitions
import com.michaelflisar.toolbox.ads.AndroidAdManager
import com.michaelflisar.toolbox.ads.FooterAdsBanner
import com.michaelflisar.toolbox.app.AndroidApp
import com.michaelflisar.toolbox.app.AndroidAppContent
import com.michaelflisar.toolbox.app.AndroidToolbar
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.NavigationUtil
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager

class MainActivity : ComponentActivity() {

    @RequiresPermission(Manifest.permission.INTERNET)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO HELLO WORLD
        // Ads initialisieren (OPTIONAL)
        AndroidAdManager.init(this)

        // Content
        setContent {
            AppNavigator(
                screen = SharedDefinitions.defaultPage
            ) { navigator ->
                AndroidApp(
                    navigator = navigator
                ) {
                    // theme + root (drawer state, app state) are available
                    AndroidAppContent(
                        navigationItems = NavigationUtil.getMobileNavigationItems(SharedDefinitions),
                        toolbar = {
                            AndroidToolbar(
                                NavigationUtil.getMobileMenuItems(SharedDefinitions, true)
                            )
                        },
                        // TODO HELLO WORLD: Banner ID definieren oder footer auf null setzen
                        footer = {
                            val proVersionManager = ProVersionManager.setup
                            val proState = proVersionManager.proState.collectAsState()
                            val adUnitId = AndroidAdManager.Ids.BANNER_DEFAULT // TODO: mit App spezifischer Banner ID ersetzen
                            FooterAdsBanner(this, proState, adUnitId)
                        }
                    )
                }
            }
        }
    }
}