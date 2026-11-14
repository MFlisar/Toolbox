package com.michaelflisar.demo.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveInfo
import com.michaelflisar.composestyled.ui.components.StyledButton
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.NavScreenDefaults
import com.michaelflisar.toolbox.app.features.navigation.manager.LocalMainBackStackManager
import kotlinx.serialization.Serializable

@Serializable
data class SubPage(val level: Int) : NavScreen {

    init {
        require(level in 1..3) { "Subpage level must be between 1 and 3." }
    }

    override val navigationMetadata: Map<String, Any>
        get() = NavScreenDefaults.slideTransitionMetadata

    @Composable
    override fun Content(
        paddingValues: PaddingValues,
        adaptiveInfo: State<StyledAdaptiveInfo>,
    ) = SubPageContent(level, paddingValues)

    @Composable
    override fun Toolbar() = DemoToolbarConfig("Page 1 / Subpage $level")
}

@Composable
private fun SubPageContent(level: Int, paddingValues: PaddingValues) {
    val backStackManager = LocalMainBackStackManager.current

    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues)
            .background(
                when (level) {
                    1 -> StyledTheme.colorScheme.primarySurface
                    2 -> StyledTheme.colorScheme.secondarySurface
                    3 -> StyledTheme.colorScheme.tertiarySurface
                    else -> StyledTheme.colorScheme.surface
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StyledText("Subpage $level")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..3).filter { it != level }.forEach { nextLevel ->
                StyledButton(onClick = { backStackManager.push(SubPage(nextLevel)) }) {
                    StyledText("Subpage $nextLevel")
                }
            }
        }
    }
}
