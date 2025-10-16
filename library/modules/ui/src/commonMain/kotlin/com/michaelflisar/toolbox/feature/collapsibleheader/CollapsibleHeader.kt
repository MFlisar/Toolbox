package com.michaelflisar.toolbox.feature.collapsibleheader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.parcelize.Parcelable
import com.michaelflisar.parcelize.Parcelize
import kotlin.math.roundToInt

object CollapsibleHeader {

    @Parcelize
    class Height(
        val pxInt: Int = 0,
        //val dp: Dp = 0.dp
    ) : Parcelable {
        @IgnoredOnParcel
        val pxFloat = pxInt.toFloat()

        fun dp(density: Density) = with(density) { pxFloat.toDp() }
    }

    @Parcelize
    class Offset(
        val pxFloat: Float = 0f,
        //val dp: Dp = 0.dp
    ) : Parcelable {
        @IgnoredOnParcel
        val pxInt = pxFloat.roundToInt()

        fun dp(density: Density) = with(density) { pxFloat.toDp() }
    }

}


class CollapsibleHeaderState(
    val headerHeight: MutableState<CollapsibleHeader.Height>,
    val stickyHeaderHeight: MutableState<CollapsibleHeader.Height>,
    val headerOffset: MutableState<CollapsibleHeader.Offset>,
)

@Composable
fun rememberCollapsibleHeaderState() = CollapsibleHeaderState(
    rememberSaveable { mutableStateOf(CollapsibleHeader.Height()) },
    rememberSaveable { mutableStateOf(CollapsibleHeader.Height()) },
    rememberSaveable { mutableStateOf(CollapsibleHeader.Offset()) }
)

@Composable
fun CollapsibleHeader(
    modifier: Modifier,
    state: CollapsibleHeaderState = rememberCollapsibleHeaderState(),
    header: @Composable () -> Unit,
    stickyHeader: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {

    val density = LocalDensity.current

    val nestedScrollConnection = remember(state.headerHeight.value) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = state.headerOffset.value.pxFloat + delta
                val newOffsetFloatValue = newOffset.coerceIn(-state.headerHeight.value.pxFloat, 0f)
                return if (state.headerOffset.value.pxFloat != newOffsetFloatValue) {
                    val oldOffset = state.headerOffset.value.pxFloat
                    val offset = newOffset.coerceIn(-state.headerHeight.value.pxFloat, 0f)
                    state.headerOffset.value = CollapsibleHeader.Offset(offset)
                    Offset(0f, newOffsetFloatValue - oldOffset)
                } else Offset.Zero
            }
        }
    }
    Box(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
            .clipToBounds()
    ) {
        Column(
            modifier = Modifier
                .zIndex(1f)
                .offset { IntOffset(x = 0, y = state.headerOffset.value.pxInt) }
        ) {
            Box(
                Modifier.onSizeChanged {
                    state.headerHeight.value = CollapsibleHeader.Height(it.height)
                }
            ) {
                header()
            }
            if (stickyHeader != null) {
                Box(
                    Modifier
                        .onSizeChanged {
                            state.stickyHeaderHeight.value = CollapsibleHeader.Height(it.height)
                        }
                ) {
                    stickyHeader()
                }
            }
        }

        //L.v { "headerHeight = ${state.headerHeight.value.dp(density)} | stickyHeaderHeight = ${state.stickyHeaderHeight.value.dp(density)} | headerOffset = ${state.headerOffset.value.dp(density)}" }

        if (state.headerHeight.value.pxInt > 0 && (stickyHeader == null || state.stickyHeaderHeight.value.pxInt > 0)) {
            Box(
                Modifier
                    .zIndex(0f)
                    .padding(
                        top = state.headerHeight.value.dp(density) +
                                state.stickyHeaderHeight.value.dp(density) +
                                state.headerOffset.value.dp(density)
                    )
            ) {
                content()
            }
        }
    }
}