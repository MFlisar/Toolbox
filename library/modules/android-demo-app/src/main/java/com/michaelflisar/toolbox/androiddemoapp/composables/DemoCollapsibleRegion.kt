package com.michaelflisar.toolbox.androiddemoapp.composables

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

object DemoCollapsibleRegion {
    class State(
        private val expandedIds: SnapshotStateList<Int>
    ) {
        fun isExpanded(id: Int) = expandedIds.contains(id)
        fun toggle(id: Int) = if (isExpanded(id)) expandedIds.remove(id) else expandedIds.add(id)
    }
}

@Composable
fun rememberDemoExpandedRegions(ids: List<Int> = emptyList()) = DemoCollapsibleRegion.State(
    expandedIds = remember { ids.toMutableStateList() }
)

@Composable
fun DemoCollapsibleRegion(
    title: String,
    regionId: Int,
    state: DemoCollapsibleRegion.State,
    modifier: Modifier = Modifier,
    info: String = "",
    content: @Composable ColumnScope.() -> Unit
) {
    val isExpanded = state.isExpanded(regionId)
    Column(
        modifier
    ) {
        val transitionState = remember { MutableTransitionState(isExpanded) }
        transitionState.targetState = isExpanded
        val transition = rememberTransition(transitionState, label = "transition")

        val arrowRotationDegree by transition.animateFloat(
            transitionSpec = { tween() },
            label = "arrow",
            targetValueByState = {
                if (it) -180f else 0f
            }
        )

        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    state.toggle(regionId)
                }
                .padding(vertical = 8.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Icon(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(24.dp)
                    .rotate(arrowRotationDegree),
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        DemoAnimatedVisibilityExpand(visible = isExpanded) {
            Column(
                modifier = Modifier.padding(all = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (info.isNotEmpty()) {
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Info, null)
                            Text(
                                text = info,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                content()
            }
        }
    }
}