package com.michaelflisar.toolbox.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MyDropdownButton(
    modifier: Modifier = Modifier,
    text: String,
    expanded: MutableState<Boolean> = remember { mutableStateOf(false) },
    icon: ImageVector? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    enabled: Boolean = true,
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
                onClick = {
                    expanded.value = !expanded.value
                }
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
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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
fun MyDropdownOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    expanded: MutableState<Boolean> = remember { mutableStateOf(false) },
    icon: ImageVector? = null,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    enabled: Boolean = true,
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
                onClick = {
                    expanded.value = !expanded.value
                }
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
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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

sealed class MyDropdownButtonEntry {

    class Button(
        val text: String,
        val icon: ImageVector? = null,
        val foregroundColor: Color? = null,
        val dismissOnClick: Boolean = true,
        val onClick: () -> Unit
    ) : MyDropdownButtonEntry()

    data object Divider : MyDropdownButtonEntry()

}


