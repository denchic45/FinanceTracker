package com.denchic45.financetracker.ui.icon.appicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.ui.icon.AppIcons

public val AppIcons.`Stack-front`: ImageVector
    get() {
        if (`_stack-front` != null) {
            return `_stack-front`!!
        }
        `_stack-front` = Builder(
            name = "Stack-front", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(12.0f, 4.0f)
                lineToRelative(-8.0f, 4.0f)
                lineToRelative(8.0f, 4.0f)
                lineToRelative(8.0f, -4.0f)
                lineToRelative(-8.0f, -4.0f)
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(8.0f, 14.0f)
                lineToRelative(-4.0f, 2.0f)
                lineToRelative(8.0f, 4.0f)
                lineToRelative(8.0f, -4.0f)
                lineToRelative(-4.0f, -2.0f)
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(8.0f, 10.0f)
                lineToRelative(-4.0f, 2.0f)
                lineToRelative(8.0f, 4.0f)
                lineToRelative(8.0f, -4.0f)
                lineToRelative(-4.0f, -2.0f)
            }
        }
            .build()
        return `_stack-front`!!
    }

private var `_stack-front`: ImageVector? = null
