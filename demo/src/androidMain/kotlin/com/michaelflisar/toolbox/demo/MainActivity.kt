package com.michaelflisar.toolbox.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.michaelflisar.toolbox.app.AndroidApp
import com.michaelflisar.toolbox.app.features.navigation.AppNavigator
import com.michaelflisar.toolbox.app.features.navigation.NavigationUtil

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppNavigator(
                screen = SharedDefinitions.defaultPage
            ) { navigator ->
                AndroidApp(
                    navigator = navigator,
                    navigationItems = { NavigationUtil.getMobileNavigationItems(SharedDefinitions) },
                    menuItems = { NavigationUtil.getMobileMenuItems(SharedDefinitions, true) }
                )
            }
        }
    }
}