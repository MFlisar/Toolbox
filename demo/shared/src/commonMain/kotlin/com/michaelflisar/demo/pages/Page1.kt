package com.michaelflisar.demo.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveInfo
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composestyled.ui.components.StyledButton
import com.michaelflisar.composestyled.ui.components.StyledIcon
import com.michaelflisar.composestyled.ui.components.StyledIconButton
import com.michaelflisar.composestyled.ui.layout.StyledColumn
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.manager.LocalMainBackStackManager
import kotlinx.serialization.Serializable

@Serializable
data object Demo1 : NavScreen {

    @Composable
    override fun Content(
        paddingValues: PaddingValues,
        adaptiveInfo: State<StyledAdaptiveInfo>,
    ) = Demo1Content(paddingValues)

    @Composable
    override fun Toolbar() = DemoToolbarConfig(
        title = "Page 1",
        actions = {
            DemoSettingsToolbarAction()
            StyledIconButton(onClick = { /**/ }) {
                StyledIcon(
                    icon = Icons.Default.MoreVert.toIconComposable(),
                    contentDescription = "More",
                )
            }
        }
    )
}

@Composable
private fun Demo1Content(paddingValues: PaddingValues) {
    val backStackManager = LocalMainBackStackManager.current

    StyledColumn(
        modifier = Modifier.fillMaxSize()
            .padding(paddingValues)
            .background(StyledTheme.colorScheme.surface)
            .padding(StyledTheme.padding.content),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StyledText("Page 1 sub pages")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..3).forEach { level ->
                StyledButton(onClick = { backStackManager.push(SubPage(level)) }) {
                    StyledText("Subpage $level")
                }
            }
        }
    }
}
