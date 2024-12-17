package com.michaelflisar.toolbox.windows.ui.pane

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

class DesktopPane internal constructor(
    val label: String,
    val expanded: MutableState<Boolean>,
    val content: @Composable () -> Unit
)

@Composable
fun rememberDesktopPane(
    label: String,
    expanded: Boolean = false,
    content: @Composable () -> Unit
) = DesktopPane(label, remember { mutableStateOf(expanded) }, content)

@Composable
fun DesktopPaneContainer(
    modifier: Modifier = Modifier,
    left: DesktopPane?,
    right: DesktopPane?,
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
                    DesktopPaneSide.Left,
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
                DesktopPaneSide(
                    Modifier.sizeIn(maxWidth = maxWidthRight),
                    DesktopPaneSide.Right,
                    right.label,
                    right.expanded
                ) {
                    right.content()
                }
            }
        }
    }
}