package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.androiddemoapp.DemoActivity
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoAppThemeRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoCollapsibleRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.rememberDemoExpandedRegions
import com.michaelflisar.toolbox.composables.MyColumn
import com.michaelflisar.toolbox.composables.MyExpandableTitle
import com.michaelflisar.toolbox.composables.MyTitle
import com.michaelflisar.toolbox.ui.MyScrollableColumn

class MainActivity : DemoActivity(
    scrollableContent = false
) {
    @Composable
    override fun ColumnScope.Content(
        themeState: ComposeTheme.State
    ) {
        val regionState = rememberDemoExpandedRegions(single = false)
        DemoAppThemeRegion(0, regionState)
        DemoCollapsibleRegion("Region A", 1, regionState) {
            MyColumn {
                Text("Text A1")
                Text("Text A2")
            }

        }
        DemoCollapsibleRegion("Region B", 2, regionState) {
            MyColumn {
                Text("Text B1")
                Text("Text B2")
            }
        }
        MyTitle("Some Content")
        MyScrollableColumn(
            modifier = Modifier.weight(1f)
        ) {
            repeat(100) {
                Text("Item $it")
            }
        }
    }
}