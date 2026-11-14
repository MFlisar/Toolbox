package com.michaelflisar.toolbox.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.extensions.alphaDisabled
import com.michaelflisar.toolbox.theme.LocalComponentOptions
import com.michaelflisar.toolbox.theme.LocalComponentStyles

@Composable
fun MyLabeledBox(
    modifier: Modifier,
    modifierInner: Modifier,
    label: String = "",
    enabled: Boolean = true,
    error: String? = null,
    focused: Boolean = false,
    onTopPaddingAvailable: ((padding: Dp) -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outline
    val errorColor = MaterialTheme.colorScheme.error
    val border by remember(focused, error, enabled) {
        mutableStateOf(
            BorderStroke(
                if (focused) 2.dp else 1.dp,
                if (error != null) errorColor else (if (focused) primary else outline)
            )
        )
    }
    val fill by remember(focused) {
        mutableStateOf(
            if (focused) primary.copy(alpha = .1f) else Color.Transparent
        )
    }

    val density = LocalDensity.current


    Box(
        modifier = modifier.then(if (!enabled) Modifier.alphaDisabled() else Modifier)
    ) {

        var titleSize by remember { mutableStateOf(IntSize(0, 0)) }
        var topPadding by remember { mutableStateOf(0.dp) }

        val labelHorizontalPadding = 16.dp
        val extraCutoutPadding = with(density) { 4.dp.toPx() }

        val paddingErrorText = PaddingValues(all = 4.dp)

        val borderCutout = if (label.isNotEmpty()) {
            // we calculate an EXACT border cut out... because it will disable all drawing inside this cutout (e.g. ripple of buttons, ...)
            val left =
                with(density) { labelHorizontalPadding.toPx() } - extraCutoutPadding
            val right =
                with(density) { labelHorizontalPadding.toPx() } + titleSize.width.toFloat() + extraCutoutPadding
            val top = 0f
            val bottom =
                with(density) { border.width.toPx() } //titleSize.height.toFloat()
            Rect(left, top, right, bottom)
        } else null

        Column {
            OutlinedCard(
                modifier = Modifier
                    .padding(top = topPadding)
                    .drawWithoutRect(borderCutout)
                    .clip(LocalComponentOptions.current.textField.outlinedShape)
                    .height(IntrinsicSize.Min)
                    .then(modifierInner),
                shape = LocalComponentOptions.current.textField.outlinedShape,
                border = border,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = fill
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    content()
                }
            }
            AnimatedVisibility(visible = error != null && enabled) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingErrorText),
                    text = error ?: "",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        if (label.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.TopStart)
                    .padding(horizontal = labelHorizontalPadding)
                    .onGloballyPositioned {
                        titleSize = it.size
                        topPadding = with(density) { (titleSize.height / 2).toDp() }
                        onTopPaddingAvailable?.invoke(topPadding)
                    },
                text = label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

private fun Modifier.drawWithoutRect(rect: Rect?) = drawWithContent {
    if (rect != null) {
        clipRect(
            left = rect.left,
            top = rect.top,
            right = rect.right,
            bottom = rect.bottom,
            clipOp = ClipOp.Difference,
        ) {
            this@drawWithContent.drawContent()
        }
    } else {
        drawContent()
    }
}