package com.michaelflisar.toolbox.demo.pages.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.menu.MenuItem
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.collapsibleheader.CollapsibleHeader

@Parcelize
object PageTestExpandableHeader : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        title = "Expandable Header",
        subTitle = null,
        icon = Icons.Default.Expand.toIconComposable()
    )

    @Composable
    override fun provideMenu(): List<MenuItem> = emptyList()

    @Composable
    override fun Screen() {
        Page()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Page(
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    CollapsibleHeader(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .background(Color.Blue)
            .padding(all = 16.dp)
            .fillMaxSize(),
        header = { modifier ->
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.Red)
            ) {
                Text("Header 1")
                Text("Header 2")
                Text("Header 3")
            }
        },
        stickyHeader = { modifier ->
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
            ) {
                Text("Sticky Header 1")
            }
        }
    ) { modifier ->
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .background(Color.Green)
        ) {
            for (i in 0 until 500) {
                Text("Item $i")
            }
        }
    }
}