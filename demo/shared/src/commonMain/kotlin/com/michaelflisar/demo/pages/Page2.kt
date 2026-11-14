package com.michaelflisar.demo.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveInfo
import com.michaelflisar.composestyled.ui.components.StyledButton
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.composestyled.ui.state.rememberStyledExpandableTopBarScrollBehavior
import com.michaelflisar.demo.LocalOpenDemoSheet
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import kotlinx.serialization.Serializable

@Serializable
data object Demo2 : NavScreen {

    @Composable
    override fun Content(
        paddingValues: PaddingValues,
        adaptiveInfo: State<StyledAdaptiveInfo>,
    ) = Content(
        title = "page 2\nwith expandable top app bar",
        color = StyledTheme.colorScheme.surfaceVariant,
        paddingValues = paddingValues,
        scrollable = true
    )

    @Composable
    override fun Toolbar() = DemoToolbarConfig(
        "Page 2",
        "Subtitle von Page 2",
        scrollBehavior = rememberStyledExpandableTopBarScrollBehavior()
    )
}


@Composable
private fun Content(
    title: String,
    color: Color,
    paddingValues: PaddingValues,
    scrollable: Boolean = false,
) {
    val openDemoSheet = LocalOpenDemoSheet.current
    val scrollModifier = if (scrollable) {
        Modifier.verticalScroll(rememberScrollState())
    } else {
        Modifier
    }
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(paddingValues)
            .background(color)
            .then(scrollModifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StyledText(text = title)
        if (openDemoSheet != null) {
            StyledButton(
                onClick = {
                    openDemoSheet()
                }
            ) {
                StyledText("Open app bottom sheet")
            }
        }
    }
}