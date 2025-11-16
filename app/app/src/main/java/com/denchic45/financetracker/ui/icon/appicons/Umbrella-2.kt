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

public val AppIcons.`Umbrella-2`: ImageVector
    get() {
        if (`_umbrella-2` != null) {
            return `_umbrella-2`!!
        }
        `_umbrella-2` = Builder(
            name = "Umbrella-2", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(5.343f, 7.343f)
                arcToRelative(8.0f, 8.0f, 0.0f, true, true, 11.314f, 11.314f)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(10.828f, 13.34f)
                lineToRelative(-4.242f, 4.243f)
                arcToRelative(2.0f, 2.0f, 0.0f, true, false, 2.828f, 2.828f)
            }
        }
            .build()
        return `_umbrella-2`!!
    }

private var `_umbrella-2`: ImageVector? = null
