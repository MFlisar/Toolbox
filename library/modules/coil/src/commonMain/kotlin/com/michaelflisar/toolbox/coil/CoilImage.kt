package com.michaelflisar.toolbox.coil

import androidx.compose.foundation.Image
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

@Composable
fun CoilImage(
    data: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    showIndex: MutableIntState = remember { mutableIntStateOf(0) },
) {
    CoilImage(listOf(data), modifier, contentScale, showIndex)
}

@Composable
fun CoilImage(
    data: List<Any?>,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    showIndex: MutableIntState = remember { mutableIntStateOf(0) },
) {
    val idx = showIndex.intValue
    val d = data.getOrNull(idx)

    LaunchedEffect(showIndex.intValue) {
        println("showIndex = ${showIndex.intValue} | d = $d")
    }

    LaunchedEffect(data, d, idx) {
        if (d == null) {
            val nextNotNullIndex = data.drop(idx + 1).indexOfFirst { it != null }
            //println("showIndex => data = $data")
            //println("showIndex => nextNotNullIndex = $nextNotNullIndex | idx = $idx | data.drop(idx + 1) = ${data.drop(idx + 1)}")
            if (nextNotNullIndex != -1)
                showIndex.intValue = idx + 1 + nextNotNullIndex
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
                onState = {
                    if (it is AsyncImagePainter.State.Error && idx < data.size - 1) {
                        showIndex.intValue = idx + 1
                    }
                }
            )
        }
    }
}