package com.michaelflisar.toolbox.ui.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ui.adaptive.utils.pressAnimation

@Composable
actual fun AdaptiveNavigationBar(
    title: String,
    modifier: Modifier,
    variant: AdaptiveNavigationBar.Variant,
    onBack: (() -> Unit)?,
    actions: @Composable (() -> Unit)?
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)    // ✅ dynamisch aus Theme abgeleitet
    ) {
        // Divider oben (iOS typisch sehr dünn)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)    // ✅ Theme Outline
        )

        // Standard Navigation Bar Height: 44dp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BACK BUTTON
            onBack?.let { callback ->
                val interaction = remember { MutableInteractionSource() }
                Text(
                    text = "‹",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .pressAnimation(interaction)          // ✅ Press Effekt
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            onClick = callback
                        )
                        .padding(end = 8.dp)
                )
            }

            // TITLE
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )

            // ACTIONS
            actions?.let {
                val interaction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .pressAnimation(interaction)
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            onClick = {} // Actions must handle click inside content
                        )
                ) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                        it()
                    }
                }
            }
        }

        if (variant == AdaptiveNavigationBar.Variant.LargeTitle) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
            )
        }
    }
}