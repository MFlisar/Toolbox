package com.michaelflisar.toolbox.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ToolboxDefaults

@Composable
fun MyExpandableTitle(
    title: String,
    expanded: MutableState<Boolean> = remember { mutableStateOf(true) },
    info: String = "",
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    showIcon: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    MyExpandableTitle(
        title,
        expanded.value,
        info,
        { expanded.value = !expanded.value },
        modifier,
        color,
        showIcon,
        content
    )
}

@Composable
fun MyExpandableTitle(
    text: String,
    expanded: Boolean,
    info: String = "",
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    showIcon: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val rotation by animateFloatAsState(if (expanded) -180f else 0f)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ToolboxDefaults.ITEM_SPACING)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .clickable {
                    onToggle()
                }
                .padding(
                    start = if (showIcon) 0.dp else 8.dp,
                    bottom = 8.dp,
                    top = 8.dp,
                    end = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIcon) {
                Icon(
                    modifier = Modifier.rotate(rotation),
                    imageVector = Icons.Default.ArrowDropDown,
                    tint = color.takeIf { it != Color.Unspecified }
                        ?: LocalContentColor.current,//.copy(alpha = LocalContentAlpha.current),
                    contentDescription = null
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            if (info.isNotEmpty()) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    text = info,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = color
                )
            }
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                content()
            }
        }
    }
}