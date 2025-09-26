package com.michaelflisar.toolbox.drawables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Keep: ImageVector
    get() {
        if (_Keep != null) return _Keep!!
        
        _Keep = ImageVector.Builder(
            name = "Keep",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveToRelative(640f, -480f)
                lineToRelative(80f, 80f)
                verticalLineToRelative(80f)
                horizontalLineTo(520f)
                verticalLineToRelative(240f)
                lineToRelative(-40f, 40f)
                lineToRelative(-40f, -40f)
                verticalLineToRelative(-240f)
                horizontalLineTo(240f)
                verticalLineToRelative(-80f)
                lineToRelative(80f, -80f)
                verticalLineToRelative(-280f)
                horizontalLineToRelative(-40f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(400f)
                verticalLineToRelative(80f)
                horizontalLineToRelative(-40f)
                close()
                moveToRelative(-286f, 80f)
                horizontalLineToRelative(252f)
                lineToRelative(-46f, -46f)
                verticalLineToRelative(-314f)
                horizontalLineTo(400f)
                verticalLineToRelative(314f)
                close()
                moveToRelative(126f, 0f)
            }
        }.build()
        
        return _Keep!!
    }

private var _Keep: ImageVector? = null

