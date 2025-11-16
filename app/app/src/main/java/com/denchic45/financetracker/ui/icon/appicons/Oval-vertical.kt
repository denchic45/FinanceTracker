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

public val AppIcons.`Oval-vertical`: ImageVector
    get() {
        if (`_oval-vertical` != null) {
            return `_oval-vertical`!!
        }
        `_oval-vertical` = Builder(
            name = "Oval-vertical", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(3.0f, 12.0f)
                curveToRelative(0.0f, -3.314f, 4.03f, -6.0f, 9.0f, -6.0f)
                reflectiveCurveToRelative(9.0f, 2.686f, 9.0f, 6.0f)
                reflectiveCurveToRelative(-4.03f, 6.0f, -9.0f, 6.0f)
                reflectiveCurveToRelative(-9.0f, -2.686f, -9.0f, -6.0f)
                close()
            }
        }
            .build()
        return `_oval-vertical`!!
    }

private var `_oval-vertical`: ImageVector? = null
