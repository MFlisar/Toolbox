package com.michaelflisar.demo.pages.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.michaelflisar.parcelize.Parcelize
import com.michaelflisar.toolbox.app.features.appstate.AppState
import com.michaelflisar.toolbox.app.features.appstate.LocalAppState
import com.michaelflisar.toolbox.app.features.navigation.screen.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.screen.rememberNavScreenData
import com.michaelflisar.toolbox.components.MyFlowRow
import com.michaelflisar.toolbox.components.MySegmentedButtonRow
import com.michaelflisar.toolbox.components.MySegmentedButtonRowIndex
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.ui.MyScrollableColumn
import com.michaelflisar.toolbox.ui.adaptive.StyledTheme
import com.michaelflisar.toolbox.ui.adaptive.components.StyledButton
import com.michaelflisar.toolbox.ui.adaptive.implementations.common.StyledColumn
import com.michaelflisar.toolbox.ui.adaptive.implementations.common.StyledExpandableTitle
import com.michaelflisar.toolbox.ui.adaptive.implementations.common.StyledRow
import com.michaelflisar.toolbox.ui.adaptive.implementations.common.StyledText
import com.michaelflisar.toolbox.ui.adaptive.implementations.common.rememberStyledExpandableTitleStyle
import com.michaelflisar.toolbox.ui.adaptive.implementations.cupertino.rememberCupertinoTheme
import com.michaelflisar.toolbox.ui.adaptive.implementations.fluent2.rememberFluent2Theme
import com.michaelflisar.toolbox.ui.adaptive.implementations.material3.rememberMaterial3Theme
import com.michaelflisar.toolbox.ui.adaptive.isDark
import com.michaelflisar.toolbox.ui.adaptive.platform.Platform
import com.michaelflisar.toolbox.ui.adaptive.tokens.darkStyledColors
import com.michaelflisar.toolbox.ui.adaptive.tokens.lightStyledColors

@Parcelize
object PageTestUIAdaptive : NavScreen() {

    @Composable
    override fun provideData() = rememberNavScreenData(
        name = "UI (Adaptive)",
        icon = Icons.Default.Visibility.toIconComposable()
    )

    @Composable
    override fun Screen() {
        Page()
    }

}

@Composable
private fun Page() {
    val appState = LocalAppState.current

    val platforms = Platform.ALL
    val themes = listOf(
        "Light", "Dark", "System"
    )
    val selectedPlatform = remember { mutableStateOf(platforms.first()) }
    val selectedThemeIndex = remember { mutableStateOf(0) }

    StyledTheme(
        platform = selectedPlatform.value,
        theme = when (selectedPlatform.value) {
            Platform.Android -> rememberMaterial3Theme()
            is Platform.Desktop -> rememberFluent2Theme()
            Platform.IOS -> rememberCupertinoTheme()
            Platform.Web -> rememberFluent2Theme()
        },
        colors = when (selectedThemeIndex.value) {
            0 -> lightStyledColors()
            1 -> darkStyledColors()
            2 -> if (isSystemInDarkTheme()) darkStyledColors() else lightStyledColors()
            else -> throw RuntimeException()
        }
    ) {
        StyledColumn(
            modifier = Modifier
                .background(StyledTheme.colors.background)
                .padding(StyledTheme.padding.content)
        ) {
            StyledRow(
                modifier = Modifier.fillMaxWidth()
                    // MySegmentedButtonRow unterstützt nicht vollständige customisation...
                    .background(MaterialTheme.colorScheme.background)
            ) {
                MySegmentedButtonRow(
                    modifier = Modifier.weight(1f),
                    items = platforms,
                    selected = selectedPlatform,
                    color = StyledTheme.colors.primary,
                    onColor = StyledTheme.colors.onPrimary
                )
                MySegmentedButtonRowIndex(
                    modifier = Modifier.weight(1f),
                    items = themes,
                    selectedIndex = selectedThemeIndex,
                    color = StyledTheme.colors.primary,
                    onColor = StyledTheme.colors.onPrimary
                )
            }

            StyledText(
                text = "Aktuelles Theme: ${StyledTheme.themeName}",
                style = StyledTheme.typography.titleSmall
            )

            MyScrollableColumn(
                modifier = Modifier.weight(1f)
            ) {
                TestButtons(appState, "Buttons")
            }
        }
    }
}

@Composable
private fun TestButtons(
    appState: AppState,
    title: String,
) {
    RegionContainer(title) {

        SubTitle("Default Buttons")
        MyFlowRow {
            StyledButton.Variant.entries.forEach {
                StyledButton(
                    variant = it,
                    onClick = { appState.showToast("${it.name} Button clicked") }
                ) {
                    StyledText("${it.name} Button")
                }
            }
        }

        SubTitle("Disabled Buttons")
        MyFlowRow {
            StyledButton.Variant.entries.forEach {
                StyledButton(
                    variant = it,
                    onClick = {},
                    enabled = false
                ) {
                    StyledText("${it.name} Button")
                }
            }
        }

        SubTitle("Buttons with Customization")
        MyFlowRow {
            val colors = listOf(
                Color.Red,
                Color.Green,
                Color.Magenta,
                Color.Blue,
                Color.Cyan
            )
            StyledButton.Variant.entries.forEachIndexed { index, variant ->
                val color = colors[index % colors.size]
                val onColor = if (color.isDark) Color.White else Color.Black
                val customization = StyledButton.customize(
                    tint = color,
                    onTint = onColor,
                    border = color
                )
                StyledButton(
                    variant = variant,
                    onClick = { appState.showToast("${variant.name} Button clicked") },
                    customization = customization
                ) {
                    StyledText("${variant.name} Button")
                }
            }
        }
    }
}

@Composable
private fun SubTitle(
    title: String,
) {
    StyledText(
        text = title,
        fontWeight = FontWeight.Bold,
        style = StyledTheme.typography.titleSmall
    )
}

@Composable
private fun RegionContainer(
    title: String,
    content: @Composable () -> Unit,
) {
    StyledExpandableTitle(
        title = { StyledText(title) },
        modifier = Modifier.fillMaxWidth(),
        style = rememberStyledExpandableTitleStyle(
            containerColor = StyledTheme.colors.primary,
            contentColor = StyledTheme.colors.onPrimary,
            borderColor = StyledTheme.colors.outline
        )
    ) {
        StyledColumn(
            modifier = Modifier.fillMaxWidth().padding(StyledTheme.padding.cardContent)
        ) {
            content()
        }
    }
}