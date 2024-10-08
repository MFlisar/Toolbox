package com.michaelflisar.publicutilities.composables

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

// ButtonDefaults.ContentPadding => 24/8 => we want less here!
private val DefaultButtonPadding = PaddingValues(
    horizontal = 12.dp,
    vertical = 8.dp
)

@Composable
fun <T> DemoSegmentedButtons(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemToText: (T) -> String,
    initialSelectedIndex: Int = 0,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.primary,
    colorOnColor: Color = MaterialTheme.colorScheme.onPrimary,
    buttonPadding: PaddingValues = DefaultButtonPadding,
    onItemSelected: (index: Int, item: T) -> Unit
) {
    DemoSegmentedButtons(
        modifier = modifier,
        items = items.map(itemToText),
        initialSelectedIndex = initialSelectedIndex,
        shape = shape,
        color = color,
        colorOnColor = colorOnColor,
        buttonPadding = buttonPadding,
        onItemSelected = {
            val item = items[it]
            onItemSelected(it, item)
        }
    )
}

@Composable
fun DemoSegmentedButtons(
    modifier: Modifier = Modifier,
    items: List<String>,
    initialSelectedIndex: Int = 0,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.primary,
    colorOnColor: Color = MaterialTheme.colorScheme.onPrimary,
    buttonPadding: PaddingValues = DefaultButtonPadding,
    onItemSelected: (index: Int) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(initialSelectedIndex) }
    val cornerSize0 = CornerSize(0)
    Row(
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->

            val keepCornersStart = index == 0
            val keepCornersEnd = index == items.size - 1
            val shapeOfIndex = if (keepCornersStart && keepCornersEnd) {
                shape
            } else if (keepCornersStart) {
                shape.copy(topEnd = cornerSize0, bottomEnd = cornerSize0)
            } else if (keepCornersEnd) {
                shape.copy(topStart = cornerSize0, bottomStart = cornerSize0)
            } else shape.copy(all = cornerSize0)

            val transitionState = remember(selectedIndex) {
                MutableTransitionState(selectedIndex).apply {
                    targetState = selectedIndex
                }
            }
            val transition = updateTransition(transitionState, label = "transition")
            val colorBackground by transition.animateColor(
                transitionSpec = { tween() },
                label = "background"
            ) {
                if (it == index) color else Color.Transparent
            }
            val colorBorder by transition.animateColor(transitionSpec = { tween() }, "border") {
                if (it == index) color else color.copy(alpha = 0.75f)
            }
            val colorForeground by transition.animateColor(
                transitionSpec = { tween() },
                "background"
            ) {
                if (it == index) colorOnColor else color.copy(alpha = 0.9f)
            }

            OutlinedButton(
                modifier = Modifier
                    .wrapContentWidth()
                    .offset((-1 * index).dp, 0.dp)
                    .zIndex(if (selectedIndex == index) 1f else 0f),
                onClick = {
                    selectedIndex = index
                    onItemSelected(selectedIndex)
                },
                shape = shapeOfIndex,
                border = BorderStroke(1.dp, colorBorder),
                contentPadding = buttonPadding,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = colorBackground
                )
            ) {
                Text(
                    text = item,
                    fontWeight = FontWeight.Normal,
                    color = colorForeground,
                )
            }
        }
    }
}