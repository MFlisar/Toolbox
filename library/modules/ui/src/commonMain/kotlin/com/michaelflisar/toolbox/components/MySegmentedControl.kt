package com.michaelflisar.toolbox.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.disabled

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> MySegmentedControl(
    modifier: Modifier = Modifier,
    items: List<T & Any>,
    selected: MutableState<T>,
    forceSelection: Boolean = true,
    mapper: (item: T & Any) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    onSelectionChanged: ((T) -> Unit)? = null
) {
    val texts = items.map { mapper(it) }
    val selectedIndex = items.indexOf(selected.value)
    MySegmentedControlImpl(modifier, texts, selectedIndex, forceSelection, minSegmentWidth, color) { index, item ->
        val s = (if (index >= 0) items[index] else null) as T
        selected.value = s
        onSelectionChanged?.invoke(s)
    }
}

@Composable
fun <T> MySegmentedControl(
    modifier: Modifier = Modifier,
    items: List<T & Any>,
    selected: T,
    forceSelection: Boolean = true,
    mapper: (item: T & Any) -> String = { it.toString() },
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    onSelectionChanged: ((T) -> Unit)? = null
) {
    val texts = items.map { mapper(it) }
    val selectedIndex = items.indexOf(selected)
    MySegmentedControlImpl(modifier, texts, selectedIndex, forceSelection, minSegmentWidth, color) { index, item ->
        onSelectionChanged?.invoke(items[index])
    }
}

// Sonderfall: Int Daten (index) + String Werte
@Composable
fun MySegmentedControl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: MutableState<Int>,
    forceSelection: Boolean = true,
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    onSelectionChanged: ((Int) -> Unit)? = null
) {
    val selectedIndex = selected.value
    MySegmentedControlImpl(modifier, items, selectedIndex, forceSelection, minSegmentWidth, color) { index, item ->
        selected.value = index
        onSelectionChanged?.invoke(index)
    }
}

@Composable
fun MySegmentedControl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: Int,
    forceSelection: Boolean = true,
    minSegmentWidth: Dp = 40.dp,
    color: Color = Color.Unspecified,
    onSelectionChanged: ((Int) -> Unit)? = null
) {
    MySegmentedControlImpl(modifier, items, selected, forceSelection, minSegmentWidth, color) { index, item ->
        onSelectionChanged?.invoke(index)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MySegmentedControlImpl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selected: Int,
    forceSelection: Boolean,
    minSegmentWidth: Dp,
    color: Color,
    onSelectionChange: (index: Int, item: String?) -> Unit
) {
    val borderColor = color.takeIf { it != Color.Unspecified }?.disabled()
        ?: MaterialTheme.colorScheme.onSurface.disabled()
    val colorSelected =
        color.takeIf { it != Color.Unspecified } ?: MaterialTheme.colorScheme.primary
    val colorSelectedText = MaterialTheme.colorScheme.contentColorFor(colorSelected)

    val colorNotSelected = Color.Unspecified
    val colorNotSelectedText = Color.Unspecified

    Column(
        modifier = modifier
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingSmall),
            verticalArrangement = Arrangement.spacedBy(LocalStyle.current.spacingSmall)
        ) {
            items.forEachIndexed { index, item ->
                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .border(1.dp, borderColor, MaterialTheme.shapes.small)
                        .background(if (selected == index) colorSelected else colorNotSelected)
                        .clickable {
                            if (selected == index) {
                                if (!forceSelection) {
                                    onSelectionChange(-1, null)
                                }
                            } else
                                onSelectionChange(index, item)
                        }
                        .widthIn(min = minSegmentWidth)
                        .padding(LocalStyle.current.paddingSmall),
                    text = item,
                    maxLines = 1,
                    color = if (selected == index) colorSelectedText else colorNotSelectedText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}