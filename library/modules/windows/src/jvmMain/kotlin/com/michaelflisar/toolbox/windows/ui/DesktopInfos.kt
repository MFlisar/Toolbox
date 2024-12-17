package com.michaelflisar.toolbox.windows.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.toolbox.classes.LocalStyle
import com.michaelflisar.toolbox.components.MyColumn
import com.michaelflisar.toolbox.ui.MyScrollableLazyColumn

private val INSET_STEP = 4.dp

internal sealed class Info {

    interface InfoText {
        val text: String
        val subText: String
        val color: Color
    }

    class Title(
        override val text: String,
        override val subText: String = "",
        override val color: Color = Color.Unspecified
    ) : Info(), InfoText

    class Text(
        override val text: String,
        override val subText: String = "",
        override val color: Color = Color.Unspecified
    ) : Info(), InfoText

    data object InsetPlus : Info()
    data object InsetMinus : Info()
}

class InfoState internal constructor(
    internal val infos: SnapshotStateList<Info>
) {
    fun size() = infos.size

    fun addTitle(
        text: String,
        subText: String = "",
        color: Color = Color.Unspecified
    ) {
        infos.add(Info.Title(text, subText, color))
    }

    fun addText(
        text: String,
        subText: String = "",
        color: Color = Color.Unspecified
    ) {
        infos.add(Info.Text(text, subText, color))
    }

    fun insetIncrease() {
        infos.add(Info.InsetPlus)
    }

    fun insetDecrease() {
        infos.add(Info.InsetMinus)
    }

    internal fun list() = infos.toList()
}

@Composable
fun rememberInfos() = InfoState(
    remember { mutableStateListOf() }
)

@Composable
fun DesktopInfos(
    modifier: Modifier = Modifier,
    autoScroll: StorageSetting<Boolean>,
    infos: InfoState
) {
    val listState = rememberLazyListState()
    val autoScroll = autoScroll.collectAsStateNotNull()
    LaunchedEffect(infos.size(), autoScroll.value) {
        if (infos.size() > 0 && autoScroll.value)
            listState.scrollToItem(infos.size() - 1)
    }
    val items = remember(infos.size()) { infos.list() }
    Column(modifier = modifier) {
        MyScrollableLazyColumn(
            Modifier.fillMaxWidth().weight(1f),
            LocalStyle.current.spacingMini,
            state = listState
        ) {
            if (items.isEmpty()) {
                item(-1) {
                    Text(text = "Empty!", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items.forEachIndexed { index, info ->
                    when (info) {
                        Info.InsetMinus,
                        Info.InsetPlus -> {
                        }

                        is Info.Text -> item(index) { InfoText(info, items.take(index)) }
                        is Info.Title -> item(index) { TitleLine(info, items.take(index)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoText(
    info: Info.Text,
    infosAbove: List<Info>
) {
    val style = MaterialTheme.typography.bodyMedium
    Line(info.text, info.subText, style, info.color, infosAbove)
}

@Composable
private fun TitleLine(
    info: Info.Title,
    infosAbove: List<Info>
) {
    val style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
    Line(info.text, info.subText, style, info.color, infosAbove)
}

@Composable
private fun Line(
    text: String,
    subText: String,
    style: TextStyle,
    color: Color,
    infosAbove: List<Info>
) {
    val countPlus = infosAbove.count { it is Info.InsetPlus }
    val countMinus = infosAbove.count { it is Info.InsetMinus }
    val inset = countPlus - countMinus
    MyColumn(
        modifier = Modifier.padding(start = INSET_STEP * inset)
    ) {
        Text(
            text = text,
            style = style,
            color = color
        )
        if (subText.isNotEmpty()) {
            Text(
                text = subText,
                style = style,
                color = color
            )
        }
    }
}