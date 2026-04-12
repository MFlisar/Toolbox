package com.michaelflisar.toolbox.coil.utils

import android.graphics.BitmapFactory
import androidx.core.graphics.drawable.toBitmap
import coil3.Bitmap
import coil3.asDrawable
import coil3.fetch.FetchResult
import coil3.fetch.ImageFetchResult
import coil3.fetch.SourceFetchResult
import com.michaelflisar.kmp.platformcontext.PlatformContextProvider

actual fun FetcherUtil.getBitmap(result: FetchResult): Bitmap {
    return when (result) {
        is ImageFetchResult -> result.image.asDrawable(PlatformContextProvider.get().resources)
            .toBitmap()

        is SourceFetchResult -> BitmapFactory.decodeStream(result.source.source().inputStream())
    }
}