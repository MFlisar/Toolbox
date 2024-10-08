package com.michaelflisar.publicutilities.demo.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.publicutilities.androidapp.DemoActivity
import com.michaelflisar.publicutilities.composables.DemoCollapsibleRegion
import com.michaelflisar.publicutilities.composables.ExpandedRegionState

class MainActivity : DemoActivity(
    scrollableContent = false
) {
    @Composable
    override fun ColumnScope.Content(
        regionsState: ExpandedRegionState,
        themeState: ComposeTheme.State
    ) {
        Text("Demo")
        DemoCollapsibleRegion("Region1", 1, regionsState) {
            Text("Text 1")
            Text("Text 2")
        }
    }
}