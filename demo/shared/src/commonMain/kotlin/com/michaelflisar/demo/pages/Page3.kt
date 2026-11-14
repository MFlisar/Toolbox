package com.michaelflisar.demo.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveInfo
import com.michaelflisar.composestyled.ui.components.StyledButton
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.demo.pages.tests.PageTestForm
import com.michaelflisar.demo.pages.tests.PageTestTaskViewer
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.manager.LocalMainBackStackManager
import kotlinx.serialization.Serializable

@Serializable
data object Demo3 : NavScreen {

    @Composable
    override fun Content(
        paddingValues: PaddingValues,
        adaptiveInfo: State<StyledAdaptiveInfo>,
    ) = Page3Content(paddingValues)

    @Composable
    override fun Toolbar() = DemoToolbarConfig("Demos", "Test pages")
}

@Composable
private fun Page3Content(paddingValues: PaddingValues) {
    val backStackManager = LocalMainBackStackManager.current
    val testPages = listOf(
        "Task Viewer" to PageTestTaskViewer,
        "Form" to PageTestForm,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(StyledTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.small),
    ) {
        StyledText("Test demos")
        testPages.forEach { (title, page) ->
            StyledButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { backStackManager.push(page) },
            ) {
                StyledText(title)
            }
        }
    }
}