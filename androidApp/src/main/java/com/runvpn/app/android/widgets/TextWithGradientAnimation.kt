package com.runvpn.app.android.widgets

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.core.ui.gradientBlue
import com.runvpn.app.core.ui.gradientLightBlue

@Composable
fun TextWithGradientAnimation(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    colors: List<Color> = listOf(
        gradientLightBlue, gradientBlue
    )
) {
    val currentFontSizePx = with(LocalDensity.current) { style.fontSize.toPx() }
    val currentFontSizeDoublePx = currentFontSizePx * 2

    val infiniteTransition = rememberInfiniteTransition(
        label = "gradientAnimationTransition"
    )

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = currentFontSizeDoublePx,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
        label = "gradientAnimationOffset"
    )

    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset(offset, offset),
        end = Offset(offset + currentFontSizePx, offset + currentFontSizePx),
        tileMode = TileMode.Mirror
    )

    Text(
        text,
        style = style.merge(
            TextStyle(brush = brush)
        ),
        modifier = modifier
    )

}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TextWithGradientAnimationPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        TextWithGradientAnimation(
            text = "Gradient Animated Text", style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ), colors = listOf(
                gradientLightBlue, gradientBlue
            )
        )
    }
}
