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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.components.MyTooltipBox
import com.michaelflisar.toolbox.extensions.Icon

@LayoutScopeMarker
@Immutable
interface MenuScope
internal object MenuScopeInstance : MenuScope

@LayoutScopeMarker
@Immutable
interface MenuIconRowScope {
    val rowScope: RowScope
}

internal class MenuIconRowScopeInstance(override val rowScope: RowScope) :
    MenuIconRowScope

private val LocalMenuSetup =
    staticCompositionLocalOf<MenuSetup> { error("MenuSetup not initialised!") }
private val LocalMenuState =
    staticCompositionLocalOf<MenuState> { error("MenuState not initialised!") }
private val LocalMenuIndex =
    staticCompositionLocalOf<MutableIntState> { error("MenuIndex not initialised!") }
private val LocalMenuLevels =
    staticCompositionLocalOf<List<Int>> { error("MenuLevel not initialised!") }

object Menu {

    val clickShape: Shape
        @Composable get() = MaterialTheme.shapes.small

    val itemPadding = 8.dp

    @Composable
    fun modifier(padding: Boolean = true, clip: Boolean = true) = Modifier
        .then(
            if (padding) {
                Modifier.padding(horizontal = itemPadding)
            } else Modifier
        )
        .then(
            if (clip) Modifier.clip(clickShape) else Modifier
        )
}


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
    internal val offset: MutableState<IntOffset>,
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
internal fun ProvideMenuLocals(
    state: MenuState,
    setup: MenuSetup,
    level: Int,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalMenuState provides state,
        LocalMenuSetup provides setup,
        LocalMenuLevels provides emptyList(),
        LocalMenuIndex provides remember { mutableIntStateOf(level) }
    ) {
        content()
    }
}

@Composable
internal fun ProvideUpdatedMenuLocals(
    levels: List<Int>,
    menuIndex: Int,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalMenuLevels provides levels,
        LocalMenuIndex provides remember { mutableIntStateOf(menuIndex) }
    ) {
        content()
    }
}

// --------------
// Hierarchy Management
// --------------

data class Level(
    val level: Int,
    val index: Int,
)

@Composable
private fun MenuScope.WrappedItem(
    content: @Composable () -> Unit,
    subContent: @Composable (() -> Unit)? = null,
) {
    val state = LocalMenuState.current
    val levels = LocalMenuLevels.current
    val index = LocalMenuIndex.current

    ProvideUpdatedMenuLocals(levels, index.value++) {
        if (state.openedLevels.value == levels) {
            content()
        }
        subContent?.invoke()
    }
}

@Composable
private fun MenuScope.WrappedSubMenu(
    content: @Composable MenuScope.() -> Unit,
) {
    val levels = LocalMenuLevels.current
    val index = LocalMenuIndex.current
    ProvideUpdatedMenuLocals(levels + index.value,0) {
        content()
    }
}

// --------------
// Items
// --------------

@Composable
fun MenuScope.MenuItem(
    text: @Composable () -> Unit,
    icon: IconComposable? = null,
    endIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    WrappedItem(
        content = {
            val setup = LocalMenuSetup.current
            val state = LocalMenuState.current
            DropdownMenuItem(
                modifier = Menu.modifier(),
                text = text,
                enabled = enabled,
                leadingIcon = icon?.let { { Icon(it) } },
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
fun MenuScope.MenuItem(
    content: @Composable () -> Unit,
    endIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    WrappedItem(
        content = {
            val setup = LocalMenuSetup.current
            val state = LocalMenuState.current
            DropdownMenuItem(
                modifier = Menu.modifier(),
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
fun MenuScope.MenuCheckbox(
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
fun MenuScope.MenuCheckbox(
    text: @Composable () -> Unit,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    icon: IconComposable?,
    enabled: Boolean = true,
) {
    WrappedItem(content = {
        DropdownMenuItem(
            modifier = Menu.modifier(),
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
            leadingIcon = icon?.let { { Icon(it) } },
            trailingIcon = null,
            onClick = {
                onCheckChange(!checked)
            }
        )
    }
    )
}

@Composable
fun MenuScope.MenuSeparator(
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
fun MenuScope.MenuIconRow(
    enabled: Boolean = true,
    content: @Composable MenuIconRowScope.() -> Unit,
) {
    WrappedItem(
        content = {
            DropdownMenuItem(
                modifier = Menu.modifier(padding = false),
                text = {
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MenuIconRowScopeInstance(this).content()
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
fun MenuIconRowScope.MenuIcon(
    enabled: Boolean = true,
    tooltip: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    val setup = LocalMenuSetup.current
    val state = LocalMenuState.current

    Box(
        modifier = Modifier
            .then(with(rowScope) { Modifier.weight(1f) })
            .minimumInteractiveComponentSize()
            .fillMaxHeight()
            .clip(Menu.clickShape)
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
fun MenuScope.MenuSubMenu(
    text: String,
    textColor: Color? = null,
    icon: IconComposable? = null,
    enabled: Boolean = true,
    content: @Composable MenuScope.() -> Unit,
) {
    WrappedItem(
        content = {
            val state = LocalMenuState.current
            val index = LocalMenuIndex.current
            DropdownMenuItem(
                modifier = Menu.modifier(),
                text = { Text(text, color = textColor ?: Color.Unspecified) },
                enabled = enabled,
                leadingIcon = icon?.let { { Icon(it) } },
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
private fun MenuScope.MenuBack(
    text: String,
) {
    WrappedItem(
        content = {
            val state = LocalMenuState.current
            Column {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(Menu.modifier())
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