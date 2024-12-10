package com.michaelflisar.toolbox.demo

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.androiddemoapp.DemoActivity
import com.michaelflisar.toolbox.androiddemoapp.LocalDemo
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoAppThemeRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoCollapsibleRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.rememberDemoExpandedRegions
import com.michaelflisar.toolbox.composables.MyButton
import com.michaelflisar.toolbox.composables.MyColumn
import com.michaelflisar.toolbox.composables.MyExpandableTitle
import com.michaelflisar.toolbox.composables.MyTitle
import com.michaelflisar.toolbox.ui.MyScrollableColumn
import kotlinx.coroutines.launch

class MainActivity : DemoActivity(
    scrollableContent = true
) {
    /*
    @Composable
    override fun createBottomBar(): @Composable() (() -> Unit)? {
        return {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(Icons.Filled.Check, contentDescription = "Localized description")
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Mic,
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = "Localized description",
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /* do something */ },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.Add, "Localized description")
                    }
                }
            )
        }
    }
*/

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
        MyColumn(
            //modifier = Modifier.weight(1f)
        ) {
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