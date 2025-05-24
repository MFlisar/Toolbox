package com.michaelflisar.toolbox.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.classes.LocalStyle

object MyDropdownButton {

    sealed class Type {

        internal abstract fun onClick()

        class Dropdown internal constructor(
            val expanded: MutableState<Boolean>,
            val items: List<Entry>
        ) : Type() {
            override fun onClick() {
                expanded.value = !expanded.value
            }

            @Composable
            internal fun Popup() {
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = {
                        expanded.value = false
                    }
                ) {
                    items.forEach { item ->
                        when (item) {
                            is Entry.Button -> {
                                DropdownMenuItem(
                                    onClick = {
                                        item.onClick()
                                        if (item.dismissOnClick) {
                                            expanded.value = false
                                        }
                                    },
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingSmall)
                                        ) {
                                            item.icon?.let {
                                                Icon(
                                                    it,
                                                    null,
                                                    tint = item.foregroundColor
                                                        ?: LocalContentColor.current//.copy(alpha = LocalContentAlpha.current)
                                                )
                                            }
                                            Text(item.text, color = item.foregroundColor ?: Color.Unspecified)
                                        }
                                    }
                                )
                            }

                            Entry.Divider -> {
                                VerticalDivider()
                            }
                        }
                    }
                }
            }
        }

        class Click internal constructor(
            val onClick: () -> Unit
        ) : Type() {
            override fun onClick() {
                this.onClick.invoke()
            }
        }
    }

    sealed class Entry {

        class Button(
            val text: String,
            val icon: ImageVector? = null,
            val foregroundColor: Color? = null,
            val dismissOnClick: Boolean = true,
            val onClick: () -> Unit
        ) : Entry()

        data object Divider : Entry()

    }

}

object MyDropdownButtonDefaults {

    @Composable
    fun typeDropdown(
        items: List<MyDropdownButton.Entry>,
        expanded: MutableState<Boolean> = remember { mutableStateOf(false) }
    ): MyDropdownButton.Type {
        return MyDropdownButton.Type.Dropdown(expanded, items)
    }

    @Composable
    fun typeClick(
        onClick: () -> Unit
    ): MyDropdownButton.Type {
        return MyDropdownButton.Type.Click(onClick)
    }
}

@Composable
fun MyDropdownButton(
    type: MyDropdownButton.Type,
    modifier: Modifier = Modifier,
    // button properties
    style: MyButton.Style = MyButtonDefaults.styleDefault(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // content
    content: @Composable RowScope.() -> Unit,
) {
    val rotation: Float = when (type) {
        is MyDropdownButton.Type.Click -> 0f
        is MyDropdownButton.Type.Dropdown -> animateFloatAsState(if (type.expanded.value) 180f else 0f).value
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row {
            style.Button(
                enabled = enabled,
                onClick = type::onClick,
                interactionSource = interactionSource
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    content()
                    Icon(
                        modifier = Modifier.rotate(rotation),
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
        }
        when (type) {
            is MyDropdownButton.Type.Click -> {
                // -
            }
            is MyDropdownButton.Type.Dropdown -> type.Popup()
        }
    }
}




















/*


@Composable
private fun MyDropdownButtonImpl(
    modifier: Modifier = Modifier,
    text: String,
    expanded: MutableState<Boolean> = remember { mutableStateOf(false) },
    icon: ImageVector? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    items: List<MyDropdownButtonEntry>
) {
    val rotation: Float by animateFloatAsState(if (expanded.value) 180f else 0f)
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row {
            Button(
                colors = colors,
                enabled = enabled,
                onClick = { expanded.value = !expanded.value },
                shape = shape,
                elevation = elevation,
                border = border,
                contentPadding = contentPadding,
                interactionSource = interactionSource
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    icon?.let {
                        Icon(it, null)
                    }
                    Text(text)
                    Icon(
                        modifier = Modifier.rotate(rotation),
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            items.forEach { item ->
                when (item) {
                    is MyDropdownButtonEntry.Button -> {
                        DropdownMenuItem(
                            onClick = {
                                item.onClick()
                                if (item.dismissOnClick) {
                                    expanded.value = false
                                }
                            },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingSmall)
                                ) {
                                    item.icon?.let {
                                        Icon(
                                            it,
                                            null,
                                            tint = item.foregroundColor
                                                ?: LocalContentColor.current//.copy(alpha = LocalContentAlpha.current)
                                        )
                                    }
                                    Text(item.text, color = item.foregroundColor ?: Color.Unspecified)
                                }
                            }
                        )
                    }

                    MyDropdownButtonEntry.Divider -> {
                        VerticalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun MyDropdownOutlinedButtonImpl(
    modifier: Modifier = Modifier,
    text: String,
    expanded: MutableState<Boolean> = remember { mutableStateOf(false) },
    icon: ImageVector? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    items: List<MyDropdownButtonEntry>
) {
    val rotation: Float by animateFloatAsState(if (expanded.value) 180f else 0f)
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row {
            OutlinedButton(
                colors = colors,
                enabled = enabled,
                onClick = { expanded.value = !expanded.value },
                shape = shape,
                elevation = elevation,
                border = border,
                contentPadding = contentPadding,
                interactionSource = interactionSource
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingSmall)
                ) {
                    icon?.let {
                        Icon(it, null)
                    }
                    Text(text)
                    Icon(
                        modifier = Modifier.rotate(rotation),
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            items.forEach { item ->
                when (item) {
                    is MyDropdownButtonEntry.Button -> {
                        DropdownMenuItem(
                            onClick = {
                                item.onClick()
                                if (item.dismissOnClick) {
                                    expanded.value = false
                                }
                            },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingSmall)
                                ) {
                                    item.icon?.let {
                                        Icon(
                                            it,
                                            null,
                                            tint = item.foregroundColor
                                                ?: LocalContentColor.current//.copy(alpha = LocalContentAlpha.current)
                                        )
                                    }
                                    Text(item.text, color = item.foregroundColor ?: Color.Unspecified)
                                }
                            }
                        )
                    }

                    MyDropdownButtonEntry.Divider -> {
                        VerticalDivider()
                    }
                }
            }
        }
    }
}
*/



