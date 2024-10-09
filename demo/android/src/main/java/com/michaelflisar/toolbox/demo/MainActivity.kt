package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.androiddemoapp.DemoActivity
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoCollapsibleRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.rememberDemoExpandedRegions

class MainActivity : DemoActivity(
    scrollableContent = false
) {
    @Composable
    override fun ColumnScope.Content(
        themeState: ComposeTheme.State
    ) {
        Text("Demo")
        val regionState = rememberDemoExpandedRegions()
        DemoCollapsibleRegion("Region1", 1, regionState) {
            Text("Text 1")
            Text("Text 2")
        }
    }
}