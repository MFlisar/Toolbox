package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ui.adaptive.utils.pressAnimation

@Composable
actual fun AdaptiveBottomBar(
    items: List<AdaptiveBottomBarItem>,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)               // ✅ Dynamischer Hintergrund
    ) {
        // Optional: iOS‑typische dünne Line oben
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)     // ✅ Dynamische Divider-Farbe
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->

                val interaction = remember { MutableInteractionSource() }

                val colors = if (item.selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .pressAnimation(interaction)      // ✅ Wiederverwendbare Press‑Animation
                        .clickable(
                            interactionSource = interaction,
                            indication = null,            // ✅ Kein Ripple
                            onClick = item.onClick
                        )
                        .padding(vertical = 4.dp)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides colors
                    ) {
                        item.icon()
                        item.label?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.labelSmall,
                                color = colors
                            )
                        }
                    }
                }
            }
        }
    }
}