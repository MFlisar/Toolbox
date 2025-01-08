package com.michaelflisar.toolbox.table

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyTextButton
import com.michaelflisar.toolbox.components.MyTitle
import com.michaelflisar.toolbox.table.definitions.TableDefinition

@Composable
fun TableTitle(
    definition: TableDefinition<*>,
    items: List<*>,
    modifier: Modifier = Modifier,
    filtered: MutableState<Int>,
    infoFormatter: (filtered: Int, total: Int) -> String = { filtered: Int, total: Int ->
        if (filtered == -1) {
            ""
        } else if (filtered == total) {
            "$total"
        } else {
            "${filtered}/$total"
        }
    },

    ) {
    val filterIsActive by remember(definition.columns) {
        derivedStateOf {
            definition.columns.map { it.filter?.isActive() ?: false }.contains(true)
        }
    }
    val titleInfo by remember(filtered.value) {
        derivedStateOf {
            infoFormatter(filtered.value, items.size)
        }
    }

    Row(
        modifier = modifier
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        MyTitle(
            modifier = Modifier.padding(vertical = LocalStyle.current.paddingDefault),
            text = titleInfo,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        AnimatedVisibility(
            visible = filterIsActive,
            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.CenterHorizontally),
            exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally),
        ) {
            MyTextButton(
                text = "Filter zurücksetzen",
                icon = Icons.Default.FilterAltOff
            ) {
                definition.clearFilter()
            }
        }
        AnimatedVisibility(visible = definition.sorts.size > 0) {
            MyTextButton(
                text = "Sortierungen zurücksetzen",
                icon = Icons.Default.Clear
            ) {
                definition.sorts.clear()
            }
        }
    }
}