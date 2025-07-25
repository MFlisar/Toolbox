package com.michaelflisar.toolbox.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import java.io.File

@Composable
fun MyImage(
    file: File,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val bitmap = remember(file) {
        file.inputStream().readAllBytes().decodeToImageBitmap()
    }
    Image(
        modifier = modifier,
        painter = BitmapPainter(image = bitmap),
        contentDescription = "",
        contentScale = contentScale
    )
}

/**
 * use something like "Res.readBytes("files/myDir/someFile.bin")" to read the file
 */
@Composable
fun MyImage(
    bytes: suspend () -> ByteArray,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    var bytes by remember { mutableStateOf(ByteArray(0)) }
    LaunchedEffect(Unit) {
        bytes = bytes()
    }
    if (bytes.isEmpty()) {
        return
    }
    val bitmap = bytes.decodeToImageBitmap()
    Image(
        modifier = modifier,
        painter = BitmapPainter(image = bitmap),
        contentDescription = "",
        contentScale = contentScale
    )
}