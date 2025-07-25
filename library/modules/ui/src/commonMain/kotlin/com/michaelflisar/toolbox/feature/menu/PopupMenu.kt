package com.michaelflisar.toolbox.feature.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.components.MyTooltipBox
import com.michaelflisar.toolbox.extensions.Render

@LayoutScopeMarker
@Immutable
interface PopupMenuScope
internal object PopupMenuScopeInstance : PopupMenuScope

@LayoutScopeMarker
@Immutable
interface PopupMenuIconRowScope {
    val rowScope: RowScope
}

internal class PopupMenuIconRowScopeInstance(override val rowScope: RowScope) :
    PopupMenuIconRowScope

private val LocalPopupMenuSetup =
    staticCompositionLocalOf<MenuSetup> { error("MenuSetup not initialised!") }
private val LocalPopupMenuState =
    staticCompositionLocalOf<MenuState> { error("MenuState not initialised!") }
private val LocalPopupMenuIndex =
    staticCompositionLocalOf<MutableIntState> { error("MenuIndex not initialised!") }
private val LocalPopupMenuLevels =
    staticCompositionLocalOf<List<Int>> { error("MenuLevel not initialised!") }


@Stable
class MenuSetup internal constructor(
    val autoDismiss: Boolean,
    val offset: DpOffset,
)

@Composable
fun rememberMenuSetup(
    autoDismiss: Boolean = true,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
): MenuSetup {
    return remember { MenuSetup(autoDismiss, offset) }
}

@Stable
data class MenuState(
    private val show: MutableState<Boolean>,
    internal val openedLevels: MutableState<List<Int>>,
    private val data: MutableState<Any?>,
    internal val offset: MutableState<IntOffset>
) {
    internal val isShowing: Boolean
        get() = show.value

    fun show() {
        show.value = true
    }

    fun show(data: Any) {
        this.data.value = data
        show()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> requireData() = data.value as T

    fun hide() {
        show.value = false
        openedLevels.value = emptyList()
        this.data.value = null
    }

    fun goUp(): Boolean {
        return if (openedLevels.value.isNotEmpty()) {
            openedLevels.value = openedLevels.value.toMutableList().dropLast(1)
            true
        } else false
    }

    fun open(index: Int) {
        openedLevels.value = openedLevels.value.toMutableList().apply {
            add(index)
        }
    }
}

@Composable
fun rememberMenuState(
    show: Boolean = false,
    openedLevels: List<Int> = emptyList(),
    data: Any? = null,
): MenuState {
    return MenuState(
        remember { mutableStateOf(show) },
        remember { mutableStateOf(openedLevels) },
        remember { mutableStateOf(data) },
        remember { mutableStateOf(IntOffset.Zero) }
    )
}

@Composable
fun PopupMenu(
    state: MenuState,
    modifier: Modifier = Modifier,
    setup: MenuSetup = rememberMenuSetup(),
    content: @Composable PopupMenuScope.() -> Unit,
) {
    if (!state.isShowing)
        return

    CompositionLocalProvider(
        LocalPopupMenuState provides state,
        LocalPopupMenuSetup provides setup,
        LocalPopupMenuLevels provides emptyList(),
        LocalPopupMenuIndex provides remember { mutableIntStateOf(-1) } // we still outside of the menu
    ) {
        DropdownMenu(
            modifier = modifier
                .onGloballyPositioned {
                    state.offset.value = it.positionOnScreen().round()
                }
            // springt damit falls sich die Position des Popups durch die sich ändernde Größe verändert!
            //.animateContentSize()
            ,
            expanded = true,
            onDismissRequest = { state.hide() },
            offset = setup.offset,
            shape = MaterialTheme.shapes.small
        ) {
            CompositionLocalProvider(
                LocalPopupMenuLevels provides emptyList(),
                LocalPopupMenuIndex provides remember { mutableIntStateOf(0) }
            ) {
                with(PopupMenuScopeInstance) {
                    content()
                }
            }
        }
    }
}

// --------------
// Hierarchy Management
// --------------

data class Level(
    val level: Int,
    val index: Int
)

@Composable
private fun PopupMenuScope.WrappedItem(
    content: @Composable () -> Unit,
    subContent: @Composable (() -> Unit)? = null,
) {
    val state = LocalPopupMenuState.current
    val levels = LocalPopupMenuLevels.current
    val index = LocalPopupMenuIndex.current

    val itemIndex = remember {
        mutableIntStateOf(index.value++)
    }

    CompositionLocalProvider(
        LocalPopupMenuLevels provides levels,
        LocalPopupMenuIndex provides itemIndex
    ) {
        if (state.openedLevels.value == levels) {
            content()
        }
        subContent?.invoke()
    }
}

@Composable
private fun PopupMenuScope.WrappedSubMenu(
    content: @Composable PopupMenuScope.() -> Unit,
) {
    val levels = LocalPopupMenuLevels.current
    val index = LocalPopupMenuIndex.current
    CompositionLocalProvider(
        LocalPopupMenuLevels provides levels + index.value,
        LocalPopupMenuIndex provides remember { mutableIntStateOf(0) }
    ) {
        content()
    }
}

// --------------
// Items
// --------------

@Composable
fun PopupMenuScope.MenuItem(
    text: @Composable () -> Unit,
    icon: IconComposable? = null,
    endIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    WrappedItem(
        content = {
            val setup = LocalPopupMenuSetup.current
            val state = LocalPopupMenuState.current
            DropdownMenuItem(
                modifier = modifier(),
                text = text,
                enabled = enabled,
                leadingIcon = icon?.let { { it.Render() } },
                trailingIcon = endIcon,
                onClick = {
                    onClick()
                    if (setup.autoDismiss) {
                        state.hide()
                    }
                }
            )
        }
    )
}

@Composable
fun PopupMenuScope.MenuItem(
    content: @Composable () -> Unit,
    endIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    WrappedItem(
        content = {
            val setup = LocalPopupMenuSetup.current
            val state = LocalPopupMenuState.current
            DropdownMenuItem(
                modifier = modifier(),
                text = content,
                enabled = enabled,
                leadingIcon = null,
                trailingIcon = endIcon,
                onClick = {
                    onClick()
                    if (setup.autoDismiss) {
                        state.hide()
                    }
                }
            )
        }
    )
}

@Composable
fun PopupMenuScope.MenuCheckbox(
    text: @Composable () -> Unit,
    checked: MutableState<Boolean>,
    icon: IconComposable? = null,
    enabled: Boolean = true,
) {
    MenuCheckbox(
        text = text,
        checked = checked.value,
        onCheckChange = { checked.value = it },
        icon = icon,
        enabled = enabled
    )
}

@Composable
fun PopupMenuScope.MenuCheckbox(
    text: @Composable () -> Unit,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    icon: IconComposable?,
    enabled: Boolean = true,
) {
    WrappedItem(content = {
        DropdownMenuItem(
            modifier = modifier(),
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        text()
                    }
                    Icon(
                        imageVector = if (checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = null
                    )
                }
            },
            enabled = enabled,
            leadingIcon = icon?.let { { it.Render() } },
            trailingIcon = null,
            onClick = {
                onCheckChange(!checked)
            }
        )
    }
    )
}

@Composable
fun PopupMenuScope.MenuSeparator(
    text: String = "",
    textColor: Color? = null,
) {
    WrappedItem(
        content = {
            if (text.isEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            } else {
                Text(
                    modifier = Modifier.padding(all = 8.dp),
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor ?: LocalContentColor.current.copy(alpha = .5f)
                )
            }
        }
    )
}

@Composable
fun PopupMenuScope.MenuIconRow(
    enabled: Boolean = true,
    content: @Composable PopupMenuIconRowScope.() -> Unit,
) {
    WrappedItem(
        content = {
            DropdownMenuItem(
                modifier = modifier(padding = false),
                text = {
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PopupMenuIconRowScopeInstance(this).content()
                    }
                },
                enabled = enabled,
                leadingIcon = null,
                trailingIcon = null,
                onClick = {}
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupMenuIconRowScope.MenuIcon(
    enabled: Boolean = true,
    tooltip: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    val setup = LocalPopupMenuSetup.current
    val state = LocalPopupMenuState.current

    Box(
        modifier = Modifier
            .then(with(rowScope) { Modifier.weight(1f) })
            .minimumInteractiveComponentSize()
            .fillMaxHeight()
            .clip(clickShape())
            .clickable(
                onClick = {
                    onClick()
                    if (setup.autoDismiss) {
                        state.hide()
                    }
                },
                enabled = enabled,
                role = Role.Button,
            ),
        contentAlignment = Alignment.Center
    ) {
        MyTooltipBox(
            tooltip = tooltip,
            offset = state.offset.value
        ) {
            icon()
        }
    }
}

@Composable
fun PopupMenuScope.MenuSubMenu(
    text: String,
    textColor: Color? = null,
    icon: IconComposable? = null,
    enabled: Boolean = true,
    content: @Composable PopupMenuScope.() -> Unit,
) {
    WrappedItem(
        content = {
            val state = LocalPopupMenuState.current
            val index = LocalPopupMenuIndex.current
            DropdownMenuItem(
                modifier = modifier(),
                text = { Text(text, color = textColor ?: Color.Unspecified) },
                enabled = enabled,
                leadingIcon = icon?.let { { it.Render() } },
                onClick = {
                    state.open(index.value)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
        },
        subContent = {
            WrappedSubMenu {
                MenuBack(text)
                content()
            }
        }
    )
}

@Composable
private fun PopupMenuScope.MenuBack(
    text: String,
) {
    WrappedItem(
        content = {
            val state = LocalPopupMenuState.current
            Column {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(modifier())
                            .clickable {
                                state.goUp()
                            }
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val color = LocalContentColor.current.copy(alpha = .7f)
                        //val fontSize = MaterialTheme.typography.titleSmall.fontSize * .8f
                        val style = MaterialTheme.typography.titleSmall//.copy(fontSize = fontSize)
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(horizontal = 4.dp),
                            tint = color
                        )
                        Text(
                            modifier = Modifier,
                            text = text,
                            style = style,
                            color = color,
                            //fontStyle = FontStyle.Italic
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    )
}

// ----------------
// Hilfsfunktionen
// ----------------

@Composable
private fun clickShape() = MaterialTheme.shapes.small

@Composable
private fun modifier(padding: Boolean = true) = Modifier
    .then(
        if (padding) {
            Modifier.padding(horizontal = 8.dp)
        } else Modifier
    )
    .clip(clickShape())