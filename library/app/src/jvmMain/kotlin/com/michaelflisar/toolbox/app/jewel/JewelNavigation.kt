package com.michaelflisar.toolbox.app.jewel

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.extensions.Render
import com.michaelflisar.toolbox.app.features.navigation.INavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItem
import com.michaelflisar.toolbox.app.features.navigation.NavItemAction
import com.michaelflisar.toolbox.app.features.navigation.NavItemDivider
import com.michaelflisar.toolbox.app.features.navigation.NavItemPopupMenu
import com.michaelflisar.toolbox.app.features.navigation.NavItemRegion
import com.michaelflisar.toolbox.app.features.navigation.NavItemSpacer
import com.michaelflisar.toolbox.extensions.toIconComposable
import com.michaelflisar.toolbox.feature.menu.MenuState
import com.michaelflisar.toolbox.feature.menu.PopupMenu
import com.michaelflisar.toolbox.feature.menu.PopupMenuScope

internal fun List<INavItem>.toJewelNavigationItems(): List<IJewelNavigationItem> {
    return map {
        when (it) {
            is NavItem -> {
                JewelNavigationItem(
                    title = it.title,
                    icon = it.icon,
                    screen = it.screen
                )
            }

            is NavItemAction -> {
                JewelNavigationAction(
                    title = it.title,
                    icon = it.icon,
                    action = it.action
                )
            }

            is NavItemPopupMenu -> {
                JewelNavigationPopupMenu(
                    title = it.title,
                    icon = it.icon,
                    state = it.state,
                    content = it.content
                )
            }

            is NavItemRegion -> {
                JewelNavigationRegion(
                    title = it.title,
                    icon = it.icon
                )
            }

            is NavItemSpacer -> {
                JewelNavigationItemSpacer(it.weight)
            }

            NavItemDivider -> {
                JewelNavigationItemDivider
            }
        }
    }
}

internal sealed interface IJewelNavigationItem

internal class JewelNavigationItem(
    val title: String,
    val icon: IconComposable?,
    val screen: Screen,
) : IJewelNavigationItem

internal class JewelNavigationAction(
    val title: String,
    val icon: IconComposable?,
    val action: () -> Unit,
) : IJewelNavigationItem

internal class JewelNavigationPopupMenu(
    val title: String,
    val icon: IconComposable? = null,
    val state: MenuState,
    val content: @Composable PopupMenuScope.() -> Unit
) : IJewelNavigationItem

internal class JewelNavigationRegion(
    val title: String,
    val icon: IconComposable? = null,
) : IJewelNavigationItem

internal class JewelNavigationItemSpacer(val weight: Float = 1f) : IJewelNavigationItem

internal data object JewelNavigationItemDivider : IJewelNavigationItem

internal object JewelNavigation {
    class Setup(
        val showExpand: Boolean = true,
        val minWidth: Dp = 0.dp,
    )
}

@Composable
internal fun JewelNavigationContainer(
    modifier: Modifier = Modifier,
    navigation: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
    ) {
        navigation()
        content()
    }
}

@Composable
internal fun JewelNavigation(
    items: List<IJewelNavigationItem>,
    selected: (screen: Screen) -> Boolean,
    expanded: MutableState<Boolean> = remember { mutableStateOf(false) },
    setup: JewelNavigation.Setup = JewelNavigation.Setup(),
) {
    Rail(items, selected, expanded, setup)
}

@Composable
private fun Rail(
    items: List<IJewelNavigationItem>,
    selected: (screen: Screen) -> Boolean,
    expanded: MutableState<Boolean>,
    setup: JewelNavigation.Setup,
) {
    Row {
        Column(
            modifier = Modifier.fillMaxHeight()
                .wrapContentWidth()
                .animateContentSize()
                .width(IntrinsicSize.Min)
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (setup.showExpand) {
                NavExpandableItem {
                    expanded.value = !expanded.value
                }
            }
            items.forEach {
                when (it) {
                    is JewelNavigationItem -> {
                        NavItem(it, selected, expanded, setup)
                    }

                    is JewelNavigationAction -> {
                        NavAction(it, expanded, setup)
                    }

                    is JewelNavigationPopupMenu -> {
                        NavPopupMenu(it, expanded, setup)
                    }

                    JewelNavigationItemDivider -> {
                        HorizontalDivider()
                    }

                    is JewelNavigationItemSpacer -> {
                        Spacer(modifier = Modifier.weight(it.weight))
                    }

                    is JewelNavigationRegion -> {
                        NavRegionItem(it, expanded, setup)
                    }
                }
            }
        }
        VerticalDivider()
    }
}

@Composable
private fun NavExpandableItem(
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = remember { mutableStateOf(false) }
    LaunchedEffect(interactionSource, pressed) {
        interactionSource.interactions.collect {
            pressed.value = it is PressInteraction.Press
        }
    }
    val scale by animateFloatAsState(
        if (pressed.value) .8f else 1f,
        animationSpec = tween(100)
    )

    NavRow(onClick = onClick, interactionSource = interactionSource, fill = false) {
        SelectionIndicator(false)
        val icon = Icons.Default.Menu.toIconComposable()
        NavIcon(
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
            icon = icon
        )
    }
}

@Composable
private fun NavItem(
    item: JewelNavigationItem,
    selected: (screen: Screen) -> Boolean,
    expanded: MutableState<Boolean>,
    setup: JewelNavigation.Setup,
) {
    val isSelected = selected(item.screen)
    val navigator = LocalNavigator.currentOrThrow
    NavRow(
        onClick = {
            navigator.replaceAll(item.screen)
        }
    ) {
        SelectionIndicator(isSelected)
        NavIcon(item.icon)
        NavText(expanded.value, item.title, setup)
    }
}

@Composable
private fun NavAction(
    item: JewelNavigationAction,
    expanded: MutableState<Boolean>,
    setup: JewelNavigation.Setup,
) {
    NavRow(
        onClick = {
            item.action()
        }
    ) {
        SelectionIndicator(false)
        NavIcon(item.icon)
        NavText(expanded.value, item.title, setup)
    }
}

@Composable
private fun NavPopupMenu(
    item: JewelNavigationPopupMenu,
    expanded: MutableState<Boolean>,
    setup: JewelNavigation.Setup,
) {
    NavRow(
        onClick = {
            item.state.show()
        }
    ) {
        SelectionIndicator(false)
        NavIcon(item.icon)
        NavText(expanded.value, item.title, setup)
        PopupMenu(
            state = item.state
        ) {
            item.content(this)
        }
    }
}


@Composable
private fun NavRegionItem(
    item: JewelNavigationRegion,
    expanded: MutableState<Boolean>,
    setup: JewelNavigation.Setup,
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .padding(all = 4.dp)
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectionIndicator(false)
        if (item.icon != null) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.primary
            ) {
                NavIcon(item.icon)
            }
        } else {
            NavIconRegionPlaceholder(expanded.value, setup)
        }
        NavText(
            expanded.value,
            item.title,
            setup,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun NavRow(
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    fill: Boolean = true,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(interactionSource, LocalIndication.current) {
                onClick()
            }
            .padding(all = 4.dp)
            .height(IntrinsicSize.Min)
            .then(if (fill) Modifier.fillMaxWidth() else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun NavIcon(
    icon: IconComposable?,
    modifier: Modifier = Modifier,
) {
    if (icon == null) {
        return
    }
    Box(modifier = modifier.size(28.dp).padding(4.dp)) {
        icon.Render()
    }
}

@Composable
private fun NavIconRegionPlaceholder(
    expanded: Boolean,
    setup: JewelNavigation.Setup,
) {
    val height = animateDpAsState(if (setup.showExpand && !expanded) 9.dp else 28.dp)
    val paddingVertical = animateDpAsState(if (setup.showExpand && !expanded) 0.dp else 4.dp)
    Box(
        modifier = Modifier.width(28.dp).height(height.value)
            .padding(horizontal = 4.dp, vertical = paddingVertical.value),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.height(1.dp).fillMaxWidth()
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}

@Composable
private fun NavText(
    expanded: Boolean,
    title: String,
    setup: JewelNavigation.Setup,
    style: TextStyle = MaterialTheme.typography.titleSmall,
    color: Color = Color.Unspecified,
) {
    if (setup.showExpand) {
        if (expanded) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .widthIn(min = setup.minWidth),
                style = style,
                color = color
            )
        }
    } else {
        Text(
            text = title,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .widthIn(min = setup.minWidth),
            style = style,
            color = color
        )
    }
}

@Composable
private fun SelectionIndicator(isSelected: Boolean) {
    val alpha by animateFloatAsState(if (isSelected) 1f else 0f)
    Spacer(
        modifier = Modifier
            .width(3.dp)
            .fillMaxHeight()
            .padding(vertical = 4.dp)
            .clip(MaterialTheme.shapes.small)
            .alpha(alpha)
            .background(MaterialTheme.colorScheme.primary)
    )
}