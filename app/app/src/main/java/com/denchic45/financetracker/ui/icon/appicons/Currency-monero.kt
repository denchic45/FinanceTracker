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

public val AppIcons.`Currency-monero`: ImageVector
    get() {
        if (`_currency-monero` != null) {
            return `_currency-monero`!!
        }
        `_currency-monero` = Builder(
            name = "Currency-monero", defaultWidth = 24.0.dp, defaultHeight
            = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(3.0f, 18.0f)
                horizontalLineToRelative(3.0f)
                verticalLineToRelative(-11.0f)
                lineToRelative(6.0f, 7.0f)
                lineToRelative(6.0f, -7.0f)
                verticalLineToRelative(11.0f)
                horizontalLineToRelative(3.0f)
            }
        }
            .build()
        return `_currency-monero`!!
    }

private var `_currency-monero`: ImageVector? = null
