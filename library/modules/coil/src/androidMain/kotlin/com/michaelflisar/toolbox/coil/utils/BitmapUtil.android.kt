package com.michaelflisar.toolbox.coil.utils

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap
import coil3.Bitmap

actual fun BitmapUtil.resizeFill(
    source: Bitmap,
    width: Int,
    height: Int,
): Bitmap {

    val sourceWidth = source.width
    val sourceHeight = source.height

    val xScale = width.toFloat() / sourceWidth
    val yScale = height.toFloat() / sourceHeight
    val scale = maxOf(xScale, yScale)

    val scaledWidth = (scale * sourceWidth).toInt()
    val scaledHeight = (scale * sourceHeight).toInt()

    val left = (width - scaledWidth) / 2
    val top = (height - scaledHeight) / 2

    val targetRect = RectF(
        left.toFloat(),
        top.toFloat(),
        (left + scaledWidth).toFloat(),
        (top + scaledHeight).toFloat()
    )

    val dest = createBitmap(width, height, source.config!!)
    val canvas = Canvas(dest)
    canvas.drawBitmap(source, null, targetRect, null)

    return dest
}

actual fun BitmapUtil.resizeFit(
    source: Bitmap,
    newHeight: Int,
    newWidth: Int,
): Bitmap {
    val sourceWidth = source.width
    val sourceHeight = source.height

    // Berechnen der Skalierungsfaktoren, um die neue Höhe und Breite zu erreichen
    val xScale = newWidth.toFloat() / sourceWidth
    val yScale = newHeight.toFloat() / sourceHeight
    val scale = maxOf(xScale, yScale)

    // Berechnen der neuen Größe des skalierten Bitmaps
    val scaledWidth = scale * sourceWidth
    val scaledHeight = scale * sourceHeight

    // Berechnen der oberen linken Koordinaten, um das skalierte Bitmap zu zentrieren
    val left = (newWidth - scaledWidth) / 2
    val top = (newHeight - scaledHeight) / 2

    // Zielrechteck für das neue, skalierte Bitmap
    val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

    // Erstellen eines neuen Bitmaps der angegebenen Größe und Zeichnen des skalierten Bitmaps darauf
    val dest = createBitmap(newWidth, newHeight, source.config!!)
    val canvas = Canvas(dest)
    canvas.drawBitmap(source, null, targetRect, null)

    return dest
}

actual fun BitmapUtil.tint(
    source: Bitmap,
    color: Int,
): Bitmap {
    val dest = createBitmap(source.width, source.height, source.config!!)
    val canvas = Canvas(dest)
    canvas.drawBitmap(source, 0f, 0f, null)

    val paint = Paint()
    paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(dest, 0f, 0f, paint)

    return dest
}

actual fun BitmapUtil.overlay(
    source: Bitmap,
    overlay: Bitmap,
    horizontalAlignment: BitmapUtil.HorizontalAlignment,
    verticalAlignment: BitmapUtil.VerticalAlignment
): Bitmap {
    val dest = createBitmap(source.width, source.height, source.config!!)
    val canvas = Canvas(dest)
    canvas.drawBitmap(source, 0f, 0f, null)
    val left = when (horizontalAlignment) {
        BitmapUtil.HorizontalAlignment.Left -> 0f
        BitmapUtil.HorizontalAlignment.Center -> (source.width - overlay.width) / 2f
        BitmapUtil.HorizontalAlignment.Right -> (source.width - overlay.width).toFloat()
    }
    val top = when (verticalAlignment) {
        BitmapUtil.VerticalAlignment.Top -> 0f
        BitmapUtil.VerticalAlignment.Center -> (source.height - overlay.height) / 2f
        BitmapUtil.VerticalAlignment.Bottom -> (source.height - overlay.height).toFloat()
    }
    canvas.drawBitmap(overlay, left, top, null)
    return dest
}

actual fun BitmapUtil.createTextBitmap(
    width: Int,
    height: Int,
    text: String,
    textSizeFactor: Float,
    textColor: Int,
    bold: Boolean,
): Bitmap {
    val textDrawable = TextDrawable(
        text = text,
        textSizeFactor = textSizeFactor,
        textColor = textColor,
        bold = bold
    )

    val dest = createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = Canvas(dest)
    textDrawable.setBounds(0, 0, width, height)
    textDrawable.draw(canvas)

    return dest
}

private class TextDrawable(
    private val text: String,
    private val textSizeFactor: Float,
    private val textColor: Int,
    private val bold: Boolean
) : Drawable() {

    private val paint: Paint = Paint().apply {
        this.color = textColor
        this.textSize = textSize
        isAntiAlias = true
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = if (bold) Typeface.DEFAULT else Typeface.DEFAULT_BOLD
    }

    override fun draw(canvas: Canvas) {
        paint.textSize = bounds.height() * textSizeFactor

        val xPos = (bounds.width() / 2f)
        val yPos = ((bounds.height() / 2f) - ((paint.descent() + paint.ascent()) / 2f))

        canvas.drawText(
            /* text = */ text,
            /* x = */ xPos,
            /* y = */ yPos,
            /* paint = */ paint
        )
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.setColorFilter(cf)
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}