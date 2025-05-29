package com.runvpn.app.android.screens.profile.subscription

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.textBlack

@Composable
fun DeviceInfoItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    descriptionColor: Color = textBlack
) {
    Row(modifier = modifier.padding(vertical = 16.dp)) {
        Text(
            text = title,
            color = hintTextColor,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = description,
            fontWeight = FontWeight.Medium,
            color = descriptionColor
        )
    }
}
