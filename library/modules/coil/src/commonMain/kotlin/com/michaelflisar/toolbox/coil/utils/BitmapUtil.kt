package com.michaelflisar.toolbox.coil.utils

import coil3.Bitmap

expect fun BitmapUtil.resizeFill(source: Bitmap, width: Int, height: Int): Bitmap
expect fun BitmapUtil.resizeFit(source: Bitmap, newHeight: Int, newWidth: Int): Bitmap
expect fun BitmapUtil.tint(source: Bitmap, color: Int): Bitmap
expect fun BitmapUtil.overlay(
    source: Bitmap,
    overlay: Bitmap,
    horizontalAlignment: BitmapUtil.HorizontalAlignment = BitmapUtil.HorizontalAlignment.Center,
    verticalAlignment: BitmapUtil.VerticalAlignment = BitmapUtil.VerticalAlignment.Center,
): Bitmap

expect fun BitmapUtil.createTextBitmap(
    width: Int,
    height: Int,
    text: String,
    textSizeFactor: Float,
    textColor: Int,
    bold: Boolean,
): Bitmap

object BitmapUtil {
    enum class HorizontalAlignment {
        Left,
        Right,
        Center
    }

    enum class VerticalAlignment {
        Top,
        Bottom,
        Center
    }
}