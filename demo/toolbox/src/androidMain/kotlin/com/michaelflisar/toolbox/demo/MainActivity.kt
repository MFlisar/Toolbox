package com.michaelflisar.toolbox.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.michaelflisar.toolbox.ads.AdManagerImpl
import com.michaelflisar.toolbox.ads.FooterAdsBanner
import com.michaelflisar.toolbox.app.AndroidApp
import com.michaelflisar.toolbox.app.AndroidAppDefaults
import com.michaelflisar.toolbox.app.AndroidApplication
import com.michaelflisar.toolbox.app.AndroidNavigation
import com.michaelflisar.toolbox.app.AndroidScaffold
import com.michaelflisar.toolbox.app.AndroidToolbar
import com.michaelflisar.toolbox.app.features.navigation.AppNavigatorFadeTransition
import com.michaelflisar.toolbox.app.features.proversion.ProVersionManager
import com.michaelflisar.toolbox.app.features.scaffold.rememberNavigationStyleAuto

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init function
        AndroidApp.init(this)
        AdManagerImpl.init(this)

        setContent {

            AndroidApplication(
                screen = Shared.page1
            ) { navigator ->
                // theme + root (drawer state, app state) are available now

                // Scaffold
                val navigationStyle = rememberNavigationStyleAuto()
                AndroidScaffold(
                    toolbar = {
                        AndroidToolbar(
                            AndroidAppDefaults.getMobileMenuItems(Shared.pageSettings)
                        )
                    },
                    navigationStyle = navigationStyle,
                    navigation = {
                        AndroidNavigation(
                            navigationStyle = navigationStyle,
                            items = Shared.pages.map { it.toNavItem() },
                            alwaysShowLabel = false
                        )
                    },
                    footer = {
                        val proVersionManager = ProVersionManager.setup
                        val proState = proVersionManager.proState.collectAsState()
                        val adUnitId =
                            AdManagerImpl.Ids.BANNER_DEFAULT // TODO: mit App spezifischer Banner ID ersetzen
                        FooterAdsBanner(this, proState, adUnitId)
                    }
                ) {
                    // Content - TODO
                    /*AndroidPage(
                        toolbar = {

                        }
                    ) {

                    }*/

                    AppNavigatorFadeTransition(navigator)
                }
            }
        }
    }
}