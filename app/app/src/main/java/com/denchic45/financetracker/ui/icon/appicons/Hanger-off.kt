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

public val AppIcons.`Hanger-off`: ImageVector
    get() {
        if (`_hanger-off` != null) {
            return `_hanger-off`!!
        }
        `_hanger-off` = Builder(
            name = "Hanger-off", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(14.0f, 6.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, true, false, -4.0f, 0.0f)
                moveToRelative(6.506f, 6.506f)
                lineToRelative(3.461f, 1.922f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, true, 1.029f, 1.749f)
                verticalLineToRelative(0.823f)
                moveToRelative(-2.0f, 2.0f)
                horizontalLineToRelative(-14.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, true, -2.0f, -2.0f)
                verticalLineToRelative(-0.823f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, true, 1.029f, -1.749f)
                lineToRelative(6.673f, -3.707f)
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(3.0f, 3.0f)
                lineToRelative(18.0f, 18.0f)
            }
        }
            .build()
        return `_hanger-off`!!
    }

private var `_hanger-off`: ImageVector? = null
