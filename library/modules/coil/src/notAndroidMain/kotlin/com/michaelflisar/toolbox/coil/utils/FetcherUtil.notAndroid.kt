package com.michaelflisar.toolbox.coil.utils

import coil3.Bitmap
import coil3.fetch.FetchResult
import coil3.fetch.ImageFetchResult
import coil3.fetch.SourceFetchResult
import coil3.toBitmap
import org.jetbrains.skia.Image

actual fun FetcherUtil.getBitmap(result: FetchResult): Bitmap {
    return when (result) {
        is ImageFetchResult -> result.image.toBitmap()
        is SourceFetchResult -> {
            val bytes = result.source.source().readByteString().toByteArray()
            decodeBitmap(bytes)
        }
    }
}

private fun decodeBitmap(encodedBytes: ByteArray): Bitmap {
    val image = Image.makeFromEncoded(encodedBytes)
    return Bitmap.makeFromImage(image)
}