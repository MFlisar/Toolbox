package com.michaelflisar.toolbox.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

object MySegmentedControl {

    internal val CornerSize0 = CornerSize(0)

    class Style(
        val fixedWidth: Dp?,
        val minWidth: Dp,
        val shape: CornerBasedShape,
        val color: Color,
        val onColor: Color,
        val borderColor: Color,
        val buttonContentPadding: PaddingValues,
    ) {
        private val shapeFirst = shape.copy(topEnd = CornerSize0, bottomEnd = CornerSize0)
        private val shapeLast = shape.copy(topStart = CornerSize0, bottomStart = CornerSize0)
        private val shapeCenter = shape.copy(all = CornerSize0)

        fun getShape(index: Int, count: Int): Shape {
            return if (count == 1)
                shape
            else if (index == 0)
                shapeFirst
            else if (index == count - 1)
                shapeLast
            else shapeCenter
        }
    }
}

@Composable
fun rememberMySegmentedControlStyle(
    fixedWidth: Dp? = null,
    minWidth: Dp = 40.dp,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.primary,
    onColor: Color = MaterialTheme.colorScheme.onPrimary,
    borderColor: Color = color,
    buttonContentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
) = MySegmentedControl.Style(
    fixedWidth,
    minWidth,
    shape,
    color,
    onColor,
    borderColor,
    buttonContentPadding
)

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> MySegmentedControl(
    items: List<T>,
    selected: MutableState<T>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    forceSelection: Boolean = true,
    style: MySegmentedControl.Style = rememberMySegmentedControlStyle(),
    onSelectionChanged: ((T) -> Unit)? = null,
) {
    val texts = items.map { mapper(it) }
    val selectedIndex = items.indexOf(selected.value)
    MySegmentedControlImpl(
        modifier = modifier,
        items = texts,
        selectedIndex = selectedIndex,
        forceSelection = forceSelection,
        style = style
    ) { index ->
        val s = (if (index >= 0) items[index] else null) as T
        selected.value = s
        onSelectionChanged?.invoke(s)
    }
}

@Composable
fun <T> MySegmentedControl(
    items: List<T>,
    selected: T,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    forceSelection: Boolean = true,
    style: MySegmentedControl.Style = rememberMySegmentedControlStyle(),
    onSelectionChanged: (T) -> Unit,
) {
    val texts = items.map { mapper(it) }
    val selectedIndex = items.indexOf(selected)
    MySegmentedControlImpl(
        modifier = modifier,
        items = texts,
        selectedIndex = selectedIndex,
        forceSelection = forceSelection,
        style = style
    ) { index ->
        onSelectionChanged.invoke(items[index])
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> MySegmentedControlIndex(
    items: List<T>,
    selectedIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    forceSelection: Boolean = true,
    style: MySegmentedControl.Style = rememberMySegmentedControlStyle(),
    onSelectionChanged: ((Int) -> Unit)? = null,
) {
    val texts = items.map { mapper(it) }
    MySegmentedControlImpl(
        modifier = modifier,
        items = texts,
        selectedIndex = selectedIndex.value,
        forceSelection = forceSelection,
        style = style
    ) { index ->
        selectedIndex.value = index
        onSelectionChanged?.invoke(index)
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> MySegmentedControlIndex(
    items: List<T>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    mapper: @Composable (item: T) -> String = { it.toString() },
    forceSelection: Boolean = true,
    style: MySegmentedControl.Style = rememberMySegmentedControlStyle(),
    onSelectionChanged: (Int) -> Unit,
) {
    val texts = items.map { mapper(it) }
    MySegmentedControlImpl(
        modifier = modifier,
        items = texts,
        selectedIndex = selectedIndex,
        forceSelection = forceSelection,
        style = style
    ) { index ->
        onSelectionChanged.invoke(index)
    }
}

// --------------------------------
// Implementation
// --------------------------------

@Composable
private fun MySegmentedControlImpl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedIndex: Int,
    forceSelection: Boolean,
    style: MySegmentedControl.Style = rememberMySegmentedControlStyle(),
    onSelectionChange: (index: Int) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            Item(
                selectedIndex,
                item,
                index,
                items.size,
                forceSelection,
                style,
                onSelectionChange
            )
        }
    }
}

@Composable
private fun Item(
    selectedIndex: Int,
    text: String,
    index: Int,
    count: Int,
    forceSelection: Boolean,
    style: MySegmentedControl.Style,
    onSelectionChange: (index: Int) -> Unit,
) {
    val selectedIndex = remember(selectedIndex) { mutableIntStateOf(selectedIndex) }
    val shapeOfIndex = style.getShape(index, count)

    val transitionState = remember {
        MutableTransitionState(selectedIndex)
    }.apply { targetState = selectedIndex }

    val transition = rememberTransition(transitionState, label = "transition")
    val colorBackground by transition.animateColor({ tween() }, "background") {
        if (it.intValue == index) style.color else Color.Transparent
    }
    val colorForeground by transition.animateColor({ tween() }, "background") {
        if (it.intValue == index) style.onColor else style.color
    }

    Text(
        modifier = if (style.fixedWidth != null) {
            Modifier.width(style.fixedWidth)
        } else {
            Modifier.wrapContentWidth()
        }.then(
            if (index != 0) {
                Modifier.offset((-1 * index).dp, 0.dp)
            } else Modifier
        )
            .widthIn(min = style.minWidth)
            .zIndex(if (selectedIndex.intValue == index) 1f else 0f)
            .clip(shapeOfIndex)
            .background(colorBackground)
            .border(1.dp, style.borderColor, shapeOfIndex)
            .clickable {
                if (selectedIndex.intValue == index) {
                    if (!forceSelection) {
                        //selectedIndex = index
                        onSelectionChange(-1)
                    }
                } else {
                    //selectedIndex = index
                    onSelectionChange(index)
                }
            }
            .minimumInteractiveComponentSize()
            .padding(style.buttonContentPadding),
        text = text,
        fontWeight = FontWeight.Normal,
        color = colorForeground,
        maxLines = 1,
        textAlign = TextAlign.Center
    )
}