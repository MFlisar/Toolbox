package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
actual fun AdaptiveSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    variant: AdaptiveSwitch.Variant,
) {
    val cs = MaterialTheme.colorScheme
    val isrc = interactionSource ?: remember { MutableInteractionSource() }

    // Cupertino Track Colors (dynamic)
    val trackColor by animateColorAsState(
        if (checked) cs.primary else cs.surfaceVariant,
        label = "switchTrack",
        animationSpec = spring()
    )

    val thumbOffset by animateDpAsState(
        if (checked) 18.dp else 2.dp,
        animationSpec = spring(),
        label = "switchThumbOffset"
    )

    // Optional iOS Press Animation (light scale)
    val pressed by isrc.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.96f else 1f,
        label = "switchScale",
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .size(width = 50.dp, height = 30.dp)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable(
                interactionSource = isrc,
                indication = null, // iOS: kein Ripple
                enabled = enabled
            ) {
                onCheckedChange(!checked)
            }
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(26.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary) // Theme-basiert statt hartes Weiß
        )
    }
}