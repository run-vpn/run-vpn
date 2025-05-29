package com.runvpn.app.android.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp

@Composable
fun HorizontalStrokeDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color
) = Canvas(modifier = modifier
    .fillMaxWidth()
    .height(thickness)) {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
    drawLine(
        strokeWidth = thickness.toPx(),
        color = color,
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f),
        pathEffect = pathEffect
    )
}
