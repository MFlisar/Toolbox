package com.michaelflisar.toolbox.app.features.toolbar.parts

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreenData

@Composable
fun ToolbarTitle(
    toolbarData: State<NavScreenData>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(toolbarData.value.title)
        toolbarData.value.subTitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}