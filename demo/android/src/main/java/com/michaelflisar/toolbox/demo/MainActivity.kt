package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.androiddemoapp.DemoActivity
import com.michaelflisar.toolbox.androiddemoapp.LocalDemo
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoAppThemeRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoCollapsibleRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.rememberDemoExpandedRegions
import com.michaelflisar.toolbox.components.MyButton
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.components.MyTitle
import kotlinx.coroutines.launch

class MainActivity : DemoActivity(
    scrollableContent = true
) {

    @Composable
    override fun ColumnScope.Content(
        themeState: ComposeTheme.State
    ) {
        val scope = rememberCoroutineScope()
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
        val demo = LocalDemo.current
        MyColumn {
            MyButton("Snackbar") {
                scope.launch {
                    demo.showSnackbar("Button")
                }
            }
            repeat(100) {
                Text("Item $it")
            }
        }
    }
}