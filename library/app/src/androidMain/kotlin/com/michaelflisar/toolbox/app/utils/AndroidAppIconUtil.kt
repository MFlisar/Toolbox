package com.michaelflisar.toolbox.app.utils

import android.content.res.Resources
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.michaelflisar.lumberjack.core.L

object AndroidAppIconUtil {

    @Composable
    fun adaptiveIconPainterResource(@DrawableRes id: Int): Painter? {
        val context = LocalContext.current
        val res = context.resources
        val theme = context.theme

        val drawable = ResourcesCompat.getDrawable(res, id, theme)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable is AdaptiveIconDrawable) {
            return BitmapPainter(drawable.toBitmap().asImageBitmap())
        }
        return when (drawable) {
            is BitmapDrawable -> BitmapPainter(drawable.bitmap.asImageBitmap())
            else -> fallbackPainterResource(id)
        }
    }

    /**
     * Copied from `androidx.compose.ui.res.PainterResources.android.kt`.
     *
     * Only rasterized image handling is altered, VectorDrawable requests are
     * passed through to the original Compose code. See comments inside for modifications.
     */
    @Composable
    private fun fallbackPainterResource(@DrawableRes id: Int): Painter? {
        val context = LocalContext.current
        val res = resources()
        val value = remember { TypedValue() }
        res.getValue(id, value, true)
        val path = value.string
        return if (path?.endsWith(".xml") == true) {
            painterResource(id = id)
        } else {
            val imageBitmap = remember(path, id, context.theme) {
                loadImageBitmapResource(res, id)
            }
            if (imageBitmap != null) {
                BitmapPainter(imageBitmap)
            } else {
                null
            }
        }
    }

    private fun loadImageBitmapResource(res: Resources, id: Int): ImageBitmap? {
        return try {
            ImageBitmap.Companion.imageResource(res, id)
        } catch (throwable: Throwable) {
            L.e(throwable) { "Failed to load resource $id" }
            null
        }
    }

    @Composable
    @ReadOnlyComposable
    private fun resources(): Resources {
        LocalConfiguration.current
        return LocalContext.current.resources
    }
}
