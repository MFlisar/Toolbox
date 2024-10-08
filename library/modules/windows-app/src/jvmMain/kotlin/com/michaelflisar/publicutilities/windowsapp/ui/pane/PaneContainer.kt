package com.michaelflisar.publicutilities.windowsapp.ui.pane

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

class Pane(
    val label: String,
    val exanded: MutableState<Boolean>,
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
    left: Pane?,
    right: Pane?,
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
                DesktopPaneSide(
                    Modifier.sizeIn(maxWidth = maxWidthLeft),
                    PaneSide.Left,
                    left.label,
                    left.exanded
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
                DesktopPaneSide(
                    Modifier.sizeIn(maxWidth = maxWidthRight),
                    PaneSide.Right,
                    right.label,
                    right.exanded
                ) {
                    right.content()
                }
            }
        }
    }
}