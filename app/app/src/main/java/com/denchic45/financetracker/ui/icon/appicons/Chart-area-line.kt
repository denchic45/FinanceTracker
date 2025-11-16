package com.denchic45.financetracker.ui.icon.appicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.ui.icon.AppIcons

public val AppIcons.`Chart-area-line`: ImageVector
    get() {
        if (`_chart-area-line` != null) {
            return `_chart-area-line`!!
        }
        `_chart-area-line` = Builder(
            name = "Chart-area-line", defaultWidth = 24.0.dp, defaultHeight
            = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(4.0f, 19.0f)
                lineToRelative(4.0f, -6.0f)
                lineToRelative(4.0f, 2.0f)
                lineToRelative(4.0f, -5.0f)
                lineToRelative(4.0f, 4.0f)
                lineToRelative(0.0f, 5.0f)
                lineToRelative(-16.0f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(4.0f, 12.0f)
                lineToRelative(3.0f, -4.0f)
                lineToRelative(4.0f, 2.0f)
                lineToRelative(5.0f, -6.0f)
                lineToRelative(4.0f, 4.0f)
            }
        }
            .build()
        return `_chart-area-line`!!
    }

private var `_chart-area-line`: ImageVector? = null
