package com.michaelflisar.toolbox.app.features.toolbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreenData
import com.michaelflisar.toolbox.components.MyRow

@Composable
internal fun ToolbarTitle(
    toolbarData: State<NavScreenData>,
    modifier: Modifier = Modifier,
    endContent: @Composable RowScope.() -> Unit = {}
) {
    MyRow(
        modifier = modifier,
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        Column(
            modifier = Modifier.Companion.weight(1f),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = toolbarData.value.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            toolbarData.value.subTitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
        endContent()
    }
}