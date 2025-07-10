package com.michaelflisar.kmpgradletools.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.kmpgradletools.removeFirstSave

// TODO: Marker nutzen, aktuelle Lösung ging mit compiler 1.5.3 / Kotlin 1.9.10 nicht mehr...

/*
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class MenuDsl

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class SubMenuDsl
*/

// ------------
// Items + Menu
// ------------

sealed class DemoPopupMenuItem {

    class Item internal constructor(
        val text: AnnotatedString,
        val textColor: Color?,
        val fontWeight: FontWeight?,
        val icon: Painter?,
        val iconTint: Color?,
        val endIcon: Painter?,
        val endIconTint: Color?,
        val enabled: Boolean,
        val onClick: () -> Unit
    ) : DemoPopupMenuItem()

    class ItemContent internal constructor(
        val content: @Composable () -> Unit,
        val endIcon: Painter?,
        val endIconTint: Color?,
        val enabled: Boolean,
        val onClick: () -> Unit
    ) : DemoPopupMenuItem()

    class SubMenu internal constructor(
        val text: String,
        val textColor: Color?,
        val icon: Painter?,
        val iconTint: Color?,
        val enabled: Boolean,
        val subItems: List<DemoPopupMenuItem>
    ) : DemoPopupMenuItem()

    class Separator internal constructor(
        val text: String = "",
        val textColor: Color?
    ) : DemoPopupMenuItem()
}

// ------------
// Builder
// ------------

abstract class BaseDemoPopupMenuBuilder {

    internal val items = mutableListOf<DemoPopupMenuItem>()

    @Composable
    fun Item(
        text: AnnotatedString,
        textColor: Color? = null,
        fontWeight: FontWeight? = null,
        icon: Painter? = null,
        iconTint: Color? = null,
        endIcon: Painter? = null,
        endIconTint: Color? = null,
        enabled: Boolean = true,
        onClick: () -> Unit = {}
    ) {
        items.add(
            DemoPopupMenuItem.Item(
                text,
                textColor,
                fontWeight,
                icon,
                iconTint,
                endIcon,
                endIconTint,
                enabled,
                onClick
            )
        )
    }

    @Composable
    fun Item(
        text: String,
        textColor: Color? = null,
        fontWeight: FontWeight? = null,
        icon: Painter? = null,
        iconTint: Color? = null,
        endIcon: Painter? = null,
        endIconTint: Color? = null,
        enabled: Boolean = true,
        onClick: () -> Unit = {}
    ) {
        items.add(
            DemoPopupMenuItem.Item(
                buildAnnotatedString { append(text) },
                textColor,
                fontWeight,
                icon,
                iconTint,
                endIcon,
                endIconTint,
                enabled,
                onClick
            )
        )
    }

    @Composable
    fun Item(
        content: @Composable () -> Unit,
        endIcon: Painter? = null,
        endIconTint: Color? = null,
        enabled: Boolean = true,
        onClick: () -> Unit = {}
    ) {
        items.add(
            DemoPopupMenuItem.ItemContent(
                content,
                endIcon,
                endIconTint,
                enabled,
                onClick
            )
        )
    }

    @Composable
    fun ItemIcon(
        text: String,
        textColor: Color? = null,
        fontWeight: FontWeight? = null,
        icon: ImageVector? = null,
        iconTint: Color? = null,
        endIcon: Painter? = null,
        endIconTint: Color? = null,
        enabled: Boolean = true,
        onClick: () -> Unit = {}
    ) {
        Item(
            text,
            textColor,
            fontWeight,
            icon?.let { rememberVectorPainter(image = it) },
            iconTint,
            endIcon,
            endIconTint,
            enabled,
            onClick
        )
    }

    @Composable
    fun Separator(
        text: String = "",
        textColor: Color? = null
    ) {
        items.add(DemoPopupMenuItem.Separator(text, textColor))
    }

    @Composable
    fun SubMenu(
        text: String,
        textColor: Color? = null,
        icon: Painter? = null,
        iconTint: Color? = null,
        enabled: Boolean = true,
        init: @Composable /*@SubMenuDsl*/ PopupSubMenuBuilder.() -> Unit
    ) {
        val subMenu = PopupSubMenuBuilder(text, textColor, icon, iconTint, enabled)
        subMenu.init()
        items.add(subMenu.build())
    }
}

//@MenuDsl
class DemoPopupMenuBuilder : BaseDemoPopupMenuBuilder()

//@SubMenuDsl
class PopupSubMenuBuilder(
    val text: String,
    val textColor: Color?,
    val icon: Painter?,
    val iconTint: Color?,
    val enabled: Boolean,
) : BaseDemoPopupMenuBuilder() {
    fun build() = DemoPopupMenuItem.SubMenu(text, textColor, icon, iconTint, enabled, items)
}

// ------------
// Main Composable
// ------------

@Immutable
data class LargePopupData(
    val countForLazy: Int? = null,
    val parentWidth: Dp? = null,
    val height: Dp = 200.dp
)

@Composable
fun DemoPopupMenu(
    state: MenuState,
    autoDismiss: Boolean = true,
    largePopupData: LargePopupData = LargePopupData(),
    init: @Composable /*@MenuDsl*/ DemoPopupMenuBuilder.() -> Unit
) {
    if (!state.isShowing)
        return

    val popupMenu = DemoPopupMenuBuilder()
    popupMenu.init()
    val items by remember {
        derivedStateOf {
            state.getCurrentItems(popupMenu.items)
        }
    }
    DropdownMenu(
        expanded = state.isShowing,
        onDismissRequest = {
            if (state.openedLevels.value.isNotEmpty()) {
                state.openedLevels.value = state.openedLevels.value.toMutableList().dropLast(1)
            } else {
                state.hide()
            }
        }
    ) {
        if (largePopupData.countForLazy != null && items.size > largePopupData.countForLazy) {
            Box(
                modifier = Modifier
                    .size(width = largePopupData.parentWidth ?: 200.dp, height = largePopupData.height)
            ) {
                LazyColumn {
                    itemsIndexed(items) { index, item->
                        Item(item, index, autoDismiss, state)
                    }
                }
            }
        } else {
            items.forEachIndexed { index, item ->
                Item(item, index, autoDismiss, state)
            }
        }
    }
}

@Composable
private fun Item(item: DemoPopupMenuItem, index: Int, autoDismiss: Boolean, state: MenuState) {
    when (item) {
        is DemoPopupMenuItem.Item -> {
            DropdownMenuItem(
                text = { Text(item.text, color = item.textColor ?: Color.Unspecified, fontWeight = item.fontWeight) },
                enabled = item.enabled,
                leadingIcon = if (item.icon != null) {
                    {
                        Icon(
                            painter = item.icon,
                            tint = item.iconTint ?: LocalContentColor.current,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else null,
                trailingIcon = if (item.endIcon != null) {
                    {
                        Icon(
                            painter = item.endIcon,
                            tint = item.endIconTint ?: LocalContentColor.current,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else null,
                onClick = {
                    item.onClick()
                    if (autoDismiss) {
                        state.hide()
                    }
                }
            )
        }

        is DemoPopupMenuItem.ItemContent -> {
            DropdownMenuItem(
                text = { item.content() },
                enabled = item.enabled,
                leadingIcon = null,
                trailingIcon = if (item.endIcon != null) {
                    {
                        Icon(
                            painter = item.endIcon,
                            tint = item.endIconTint ?: LocalContentColor.current,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else null,
                onClick = {
                    item.onClick()
                    if (autoDismiss) {
                        state.hide()
                    }
                }
            )
        }

        is DemoPopupMenuItem.SubMenu -> {
            DropdownMenuItem(
                text = { Text(item.text, color = item.textColor ?: Color.Unspecified) },
                enabled = item.enabled,
                leadingIcon = if (item.icon != null) {
                    {
                        Icon(
                            painter = item.icon,
                            tint = item.iconTint ?: LocalContentColor.current,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else null,
                onClick = {
                    state.openedLevels.value =
                        state.openedLevels.value.toMutableList().apply {
                            add(index)
                        }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
        }

        is DemoPopupMenuItem.Separator -> {
            if (item.text.isEmpty()) {
                HorizontalDivider()
            } else {
                Text(
                    modifier = Modifier.padding(all = 8.dp),
                    text = item.text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = item.textColor ?: Color.Unspecified
                )
            }
        }
    }
}

// ------------
// Menu State
// ------------

@Composable
fun rememberDemoMenuState(): MenuState {
    return MenuState(
        remember { mutableStateOf(false) },
        remember { mutableStateOf(emptyList()) },
        remember { mutableStateOf(null) }
    )
}

@Stable
data class MenuState(
    private val show: MutableState<Boolean>,
    internal val openedLevels: MutableState<List<Int>>,
    private val data: MutableState<Any?>,
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

    fun getCurrentItems(items: List<DemoPopupMenuItem>): List<DemoPopupMenuItem> {
        if (!show.value)
            return items
        var currentItems = items
        val levels = openedLevels.value.toMutableList()
        while (levels.size > 0) {
            currentItems = (currentItems[levels.removeFirstSave()] as DemoPopupMenuItem.SubMenu).subItems
        }
        return currentItems
    }


}