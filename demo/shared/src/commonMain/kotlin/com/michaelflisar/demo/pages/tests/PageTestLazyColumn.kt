package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.app.features.toolbar.PageToolbar
import com.michaelflisar.toolbox.app.features.toolbar.composables.ToolbarMainMenuItems
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.ui.MyScrollableLazyColumn

@Parcelize
object PageTestLazyColumn : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        name = "LazyColumn",
        icon = Icons.Default.List.toIconComposable()
    )

    @Composable
    override fun Screen() {
        Page()
    }

    @Composable
    override fun Toolbar() {
        val data = provideData()
        PageToolbar(
            screen = this,
            title = data.name,
            endContent = { ToolbarMainMenuItems(showInOverflow = true) },
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Page(
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    MyScrollableLazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .fillMaxSize(),
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(100) {
            Text(
                text = "Item ${it + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = if (it % 2 == 0) Color.LightGray else Color.Transparent,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(8.dp)
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}