package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import java.io.File

@Composable
fun MyImage(
    file: File,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val bitmap = remember(file) {
        loadImageBitmap(file.inputStream())
    }
    Image(
        modifier = modifier,
        painter = BitmapPainter(image = bitmap),
        contentDescription = "",
        contentScale = contentScale
    )
}

@Composable
fun MyImage(
    path: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val bitmap = useResource(path) { loadImageBitmap(it) }
    Image(
        modifier = modifier,
        painter = BitmapPainter(image = bitmap),
        contentDescription = "",
        contentScale = contentScale
    )
}