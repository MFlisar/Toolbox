package com.michaelflisar.toolbox.proversion.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun ProVersionHeroGraphic(
    variant: ProVersionHeroVariant = ProVersionHeroVariant.Radiance,
    primary: Color = StyledTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth().height(152.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            when (variant) {
                ProVersionHeroVariant.Radiance -> drawRadiance(center, primary)
            }
            drawHeroSparkles(variant)
        }
        Image(
            imageVector = ProVersionCrown,
            modifier = Modifier.size(width = 100.dp, height = 86.dp),
            contentDescription = null
        )
    }
}

internal enum class ProVersionHeroVariant {
    Radiance
}

private data class HeroSparkle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val color: Color,
)

private fun DrawScope.drawHalo(
    center: Offset,
    primary: Color,
) {
    drawCircle(primary.copy(alpha = 0.10f), radius = 62.dp.toPx(), center = center)
    drawCircle(
        primary.copy(alpha = 0.42f),
        radius = 68.dp.toPx(),
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
    drawCircle(
        Color(0xFFFFC857),
        radius = 74.dp.toPx(),
        center = center.copy(x = center.x - 6.dp.toPx(), y = center.y + 2.dp.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )
}

private fun DrawScope.drawRadiance(
    center: Offset,
    primary: Color,
) {
    for (index in 0 until 16) {
        val angle = (2 * kotlin.math.PI * index / 16).toFloat()
        val innerRadius = if (index % 2 == 0) 58.dp.toPx() else 64.dp.toPx()
        val outerRadius = if (index % 2 == 0) 74.dp.toPx() else 70.dp.toPx()
        drawLine(
            color = if (index % 3 == 0) Color(0xFFFFC857) else primary.copy(alpha = 0.68f),
            start = Offset(
                center.x + cos(angle) * innerRadius,
                center.y + sin(angle) * innerRadius
            ),
            end = Offset(
                center.x + cos(angle) * outerRadius,
                center.y + sin(angle) * outerRadius
            ),
            strokeWidth = if (index % 2 == 0) 2.dp.toPx() else 1.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}


private fun DrawScope.drawHeroSparkles(
    variant: ProVersionHeroVariant,
) {
    val gold = Color(0xFFE0A100)
    val purple = Color(0xFF8E5ABD)
    val teal = Color(0xFF168A86)
    val coral = Color(0xFFD05D7D)
    val sparkles = when (variant) {
        ProVersionHeroVariant.Radiance -> listOf(
            HeroSparkle(0.20f, 0.30f, 8f, gold),
            HeroSparkle(0.80f, 0.30f, 8f, purple),
            HeroSparkle(0.18f, 0.68f, 6f, teal),
            HeroSparkle(0.82f, 0.68f, 6f, coral)
        )
    }
    sparkles.forEach { sparkle ->
        drawFourPointSparkle(
            center = Offset(size.width * sparkle.x, size.height * sparkle.y),
            radius = sparkle.radius.dp.toPx(),
            color = sparkle.color
        )
    }
}

private fun DrawScope.drawFourPointSparkle(
    center: Offset,
    radius: Float,
    color: Color,
) {
    val narrowRadius = radius * 0.24f
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        lineTo(center.x + narrowRadius, center.y - narrowRadius)
        lineTo(center.x + radius, center.y)
        lineTo(center.x + narrowRadius, center.y + narrowRadius)
        lineTo(center.x, center.y + radius)
        lineTo(center.x - narrowRadius, center.y + narrowRadius)
        lineTo(center.x - radius, center.y)
        lineTo(center.x - narrowRadius, center.y - narrowRadius)
        close()
    }
    drawPath(path, color)
}

@Composable
private fun ProVersionHeroPreview(variant: ProVersionHeroVariant) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(Color(0xFFF8F5FC))
            .padding(horizontal = 12.dp)
    ) {
        ProVersionHeroGraphic(
            variant = variant,
            primary = Color(0xFF6550A4)
        )
    }
}

@Preview(name = "Pro Hero 4 - Radiance", showBackground = true)
@Composable
private fun PreviewProVersionHeroRadiance() {
    ProVersionHeroPreview(ProVersionHeroVariant.Radiance)
}

private val ProVersionCrown: ImageVector by lazy {
    ImageVector.Builder(
        name = "ProVersionCrown",
        defaultWidth = 100.dp,
        defaultHeight = 86.dp,
        viewportWidth = 100f,
        viewportHeight = 86f
    ).apply {
        path(
            fill = SolidColor(Color(0xFFFFC857)),
            stroke = SolidColor(Color(0xFF9A5A00)),
            strokeLineWidth = 2.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 24f)
            lineTo(33f, 42f)
            lineTo(47f, 9f)
            lineTo(63f, 42f)
            lineTo(87f, 24f)
            lineTo(78f, 68f)
            lineTo(21f, 68f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFE89C28)),
            stroke = SolidColor(Color(0xFF9A5A00)),
            strokeLineWidth = 2.5f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(20f, 59f)
            lineTo(79f, 59f)
            lineTo(77f, 72f)
            lineTo(22f, 72f)
            close()
        }
    }.build()
}
