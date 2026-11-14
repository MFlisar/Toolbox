package com.michaelflisar.demo.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.adaptive.StyledAdaptive
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveInfo
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveMasterDetailMode
import com.michaelflisar.composestyled.ui.components.StyledButton
import com.michaelflisar.composestyled.ui.components.StyledIcon
import com.michaelflisar.composestyled.ui.icons.StyledIcons
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.toolbox.app.MasterDetailContainer
import com.michaelflisar.toolbox.app.features.navigation.NavScreen
import com.michaelflisar.toolbox.app.features.navigation.manager.rememberMasterDetailState
import kotlinx.serialization.Serializable

@Serializable
data class Demo4Detail(val itemId: Int) : NavKey

@Serializable
data object Demo4 : NavScreen {

    @Composable
    override fun Content(
        paddingValues: PaddingValues,
        adaptiveInfo: State<StyledAdaptiveInfo>,
    ) = Demo4Content(paddingValues, adaptiveInfo)

    @Composable
    override fun Toolbar() = DemoToolbarConfig("Page 4", "Master-detail demo")
}

@Composable
private fun Demo4Content(
    paddingValues: PaddingValues,
    adaptiveInfo: State<StyledAdaptiveInfo>,
) {
    val masterDetailMode = StyledAdaptive.masterDetail(adaptiveInfo)
    val masterDetailState = rememberMasterDetailState(
        configuration = NavigationSavedStateConfiguration,
        rootPage = Demo4,
        detailPageType = Demo4Detail::class,
        masterDetailMode = masterDetailMode,
    )

    val subPages = (1..5)

    MasterDetailContainer(
        manager = masterDetailState,
        masterDetailMode = masterDetailMode.value,
        defaultDetailPage = { Demo4Detail(subPages.first()) },
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        masterContent = {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(StyledTheme.colorScheme.surface)
                    .padding(StyledTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.small),
            ) {
                StyledText("Items")
                subPages.forEach { item ->
                    StyledButton(onClick = {
                        masterDetailState.switchDetailsPage(Demo4Detail(item))
                    }) {
                        if (masterDetailMode.value == StyledAdaptiveMasterDetailMode.SideBySide) {
                            StyledIcon(
                                icon = if (masterDetailState.detail?.itemId == item) {
                                    StyledIcons.Check
                                } else {
                                    StyledIcons.Check
                                },
                                contentDescription = null,
                            )
                        }
                        StyledText("Item $item")
                    }
                }
                StyledText("Layout: ${masterDetailMode.value}")
            }
        },
        emptyDetailContent = {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(StyledTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                StyledText("Select an item to see its details")
                StyledText("Layout: ${masterDetailMode.value}")
            }
        },
        detailContent = { key ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(StyledTheme.colorScheme.primarySurface)
                    .padding(StyledTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.medium),
            ) {
                StyledText("Item ${key.itemId}")
                StyledText("Layout: ${masterDetailMode.value}")
                if (masterDetailMode.value == StyledAdaptiveMasterDetailMode.SideBySide) {
                    StyledText("Back button is disabled in side-by-side mode")
                } else {
                    StyledButton(onClick = { masterDetailState.onBack() }) {
                        StyledText("Back")
                    }
                }
            }
        },
    )
}
