package com.michaelflisar.toolbox.coil.utils

import coil3.Bitmap
import coil3.Image
import coil3.ImageLoader
import coil3.asImage
import coil3.fetch.FetchResult
import coil3.request.Options
import coil3.size.Scale
import coil3.size.pxOrElse

expect fun FetcherUtil.getBitmap(result: FetchResult): Bitmap

object FetcherUtil {

    suspend fun fetchUrlCatchErrors(
        imageLoader: ImageLoader,
        options: Options,
        url: String,
    ): FetchResult? {
        return try {
            fetchUrl(imageLoader, options, url)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchUrl(
        imageLoader: ImageLoader,
        options: Options,
        url: String,
    ): FetchResult? {
        try {
            val data = imageLoader.components.map(url, options)
            val output = imageLoader.components.newFetcher(data, options, imageLoader)
            val (fetcher) = checkNotNull(output) { "no supported fetcher" }
            return fetcher.fetch()
        } catch (e: Exception) {
            return null
        }
    }

    fun getImage(
        options: Options,
        result: FetchResult?,
        transform: (Bitmap) -> Bitmap = { it }
    ): Image? {

        val h = options.size.height.pxOrElse { 0 }
        val w = options.size.width.pxOrElse { 0 }

        if (result == null) return null

        val bitmap = getBitmap(result).let {
            when (options.scale) {
                Scale.FILL -> BitmapUtil.resizeFill(it, w, h)
                Scale.FIT -> BitmapUtil.resizeFit(it, w, h)
            }
        }

        return transform(bitmap).asImage()
    }
}