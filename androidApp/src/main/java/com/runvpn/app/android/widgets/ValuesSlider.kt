package com.runvpn.app.android.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.ext.round
import com.runvpn.app.android.rubikFontFamily
import com.runvpn.app.android.utils.WidgetPreview

@Composable
fun <T : Any> ValuesSlider(
    values: List<T>,
    selectedValue: T,
    onValueSelected: (T) -> Unit,
    showValueLabels: Boolean = false,
) {
    val rangeMin = 0f
    val rangeMax = 1f

    val stepsCount = if (values.size > 2) {
        values.size - 2
    } else 0

    val valuesMap: MutableMap<Float, T> = remember {
        mutableMapOf()
    }

    var currentValue by remember {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(values, selectedValue) {
        if (values.size > 2) {
            valuesMap[rangeMin] = values.first()
            valuesMap[rangeMax] = values.last()

            for (i in 1..stepsCount) {
                val rangeValue = rangeMax / (values.size - 1) * i
                valuesMap[rangeValue.round(3)] = values[i]
            }

            currentValue = valuesMap.entries.firstOrNull {
                it.value == selectedValue
            }?.key ?: rangeMin
        }
    }

    Column {
        AppSlider(
            value = currentValue,
            valueRange = rangeMin..rangeMax,
            enabled = values.size > 1,
            steps = if (values.size > 2) {
                values.size - 2
            } else 0,
            onValueChange = { newValue ->
                val roundedValue = newValue.round(3)
                valuesMap[roundedValue]?.let {
                    onValueSelected(it)
                }
                currentValue = newValue
            }
        )
        if (showValueLabels) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                values.forEach {
                    Text(
                        text = it.toString(),
                        fontSize = 14.sp,
                        fontFamily = rubikFontFamily,
                        fontWeight = FontWeight(500),
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@WidgetPreview
@Composable
private fun ValuesSliderPreview() {
    RunVpnTheme {
        Box(
            modifier = Modifier
                .padding(32.dp)
                .background(Color.White)
        ) {
            ValuesSlider(
                values = listOf(1, 3, 10),
                selectedValue = 3,
                onValueSelected = {}
            )
        }
    }
}
