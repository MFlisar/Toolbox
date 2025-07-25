package com.michaelflisar.toolbox.feature.pane

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier


class Pane internal constructor(
    val label: String,
    val expanded: MutableState<Boolean>,
    val content: @Composable () -> Unit
)

@Composable
fun rememberPane(
    label: String,
    expanded: Boolean = false,
    content: @Composable () -> Unit
) = Pane(label, remember { mutableStateOf(expanded) }, content)

@Composable
fun PaneContainer(
    modifier: Modifier = Modifier,
    left: Pane? = null,
    right: Pane? = null,
    maxWidthLeftInPercentages: Float = 1f / 5f,
    maxWidthRightInPercentages: Float = 1f / 5f,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val maxWidthLeft = maxWidth * maxWidthLeftInPercentages
        val maxWidthRight = maxWidth * maxWidthRightInPercentages
        Row(modifier = Modifier.fillMaxSize()) {
            if (left != null) {
                PaneSide(
                    Modifier.sizeIn(maxWidth = maxWidthLeft),
                    PaneSide.Left,
                    left.label,
                    left.expanded
                ) {
                    left.content()
                }
            }
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                content()
            }

            if (right != null) {
                PaneSide(
                    Modifier.sizeIn(maxWidth = maxWidthRight),
                    PaneSide.Right,
                    right.label,
                    right.expanded
                ) {
                    right.content()
                }
            }
        }
    }
}