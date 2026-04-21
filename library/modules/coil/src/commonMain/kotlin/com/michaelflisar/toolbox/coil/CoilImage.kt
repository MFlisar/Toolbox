package com.michaelflisar.toolbox.coil

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.debug
import com.michaelflisar.toolbox.error
import com.michaelflisar.toolbox.info

@Composable
fun CoilImage(
    data: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    showPlaceholderBox: Boolean = true,
) {
    if (data == null) {
        if (showPlaceholderBox) {
            L.debug(ToolboxLogging.Tag.Coil) { "NO media data..." }
            Box(modifier = modifier)
        } else {
            L.info(ToolboxLogging.Tag.Coil) { "NO media data... => showPlaceholderBox = false => no box shown" }
        }
    } else if (data is ImageVector) {
        L.info(ToolboxLogging.Tag.Coil) { "data is ImageVector => showing ImageVector" }
        Image(
            imageVector = data,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier,
        )
    } else {
        L.info(ToolboxLogging.Tag.Coil) { "data is ${data::class.simpleName ?: data::class} => showing AsyncImage" }
        AsyncImage(
            model = data,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier,
            onState = ::onState
        )
    }
}

@Composable
fun CoilMultiImage(
    data: List<Any?>,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    showIndex: MutableIntState? = remember { mutableIntStateOf(0) },
) {
    val idx = showIndex?.intValue ?: 0
    val d = data.getOrNull(idx)

    //LaunchedEffect(idx, showIndex?.intValue, d, data) {
    //    println("idx = $idx | showIndex = ${showIndex?.intValue} | d = $d | data.size = ${data.size} | data = $data")
    //}

    if (showIndex != null) {
        LaunchedEffect(data, d, idx) {
            if (d == null) {
                val nextNotNullIndex = data.drop(idx + 1).indexOfFirst { it != null }
                //println("showIndex => data = $data")
                //println("showIndex => nextNotNullIndex = $nextNotNullIndex | idx = $idx | data.drop(idx + 1) = ${data.drop(idx + 1)}")
                if (nextNotNullIndex != -1)
                    showIndex.intValue = idx + 1 + nextNotNullIndex
            }
        }
    }

    if (d != null) {
        if (d is ImageVector) {
            Image(
                imageVector = d,
                contentDescription = null,
                contentScale = contentScale,
                modifier = modifier,
            )
        } else {
            AsyncImage(
                model = d,
                contentDescription = null,
                contentScale = contentScale,
                modifier = modifier,
                onState = ::onState
            )
        }
    } else {
        LaunchedEffect(idx, showIndex?.intValue, d, data) {
            L.info(ToolboxLogging.Tag.Coil) { "idx = $idx | showIndex = ${showIndex?.intValue} | d = $d | data.size = ${data.size} | data = $data" }
        }
    }
}

private fun onState(state: AsyncImagePainter.State) {
    if (state is AsyncImagePainter.State.Error) {
        L.error(
            ToolboxLogging.Tag.Coil,
            t = state.result.throwable
        ) { "Error loading image | message: ${state.result.throwable.message}" }
    }
}