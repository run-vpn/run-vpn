package com.runvpn.app.android.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.core.ui.sliderActiveColor
import com.runvpn.app.core.ui.sliderInactiveColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSlider(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    enabled: Boolean = true
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        enabled = enabled,
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = sliderActiveColor,
            inactiveTrackColor = sliderInactiveColor
        ),
        thumb = { SliderThumb() },
        modifier = modifier
    )
}

@Composable
private fun SliderThumb(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .size(32.dp)
            .border(2.dp, Color(0xFFEBF2FF), CircleShape)
            .shadow(
                elevation = 24.dp,
                spotColor = Color(0x1F000000),
                ambientColor = Color(0x1F000000),
                shape = CircleShape
            )
            .background(Color.White)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AppSliderPreview() {
    RunVpnTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AppSlider(
                value = 1.0f,
                valueRange = 1.0f..100.0f,
                onValueChange = {},
            )
        }
    }
}
