package com.runvpn.app.android.screens.welcome

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.core.ui.colorBackgroundAccentTint
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.core.ui.textLightGrayColor

@Composable
fun UseModeView(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isChecked) colorIconAccent else colorBackgroundAccentTint

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
    ) {

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                contentAlignment = Alignment.CenterStart, modifier = Modifier
                    .paint(
                        painterResource(id = R.drawable.bg_text_title),
                        contentScale = ContentScale.FillBounds
                    )
                    .padding(start = 12.dp, end = 20.dp)
            ) {
                Text(
                    text = title,
                    color = colorIconAccent,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            RadioButton(
                selected = isChecked, onClick = { onClick() },
                colors = RadioButtonColors(
                    unselectedColor = textHintColor,
                    selectedColor = colorIconAccent,
                    disabledSelectedColor = textLightGrayColor,
                    disabledUnselectedColor = textLightGrayColor
                ),
                modifier = Modifier.scale(1.3f)
            )

        }

        Text(
            text = subtitle,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = hintTextColor
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun UseModeCheckedPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        UseModeView(
            title = "UseMode Title",
            subtitle = "UseMode description. Long text",
            isChecked = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun UseModePreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        UseModeView(
            title = "UseMode Title",
            subtitle = "UseMode description. Long text",
            isChecked = false,
            onClick = {}
        )
    }
}
