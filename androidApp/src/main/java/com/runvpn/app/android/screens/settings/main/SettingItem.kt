package com.runvpn.app.android.screens.settings.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.R
import com.runvpn.app.core.ui.textPrimaryColor
import com.runvpn.app.core.ui.textSecondaryColor

@Composable
fun SettingItem(
    iconPainter: Painter,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    descriptionMaxLines: Int = 5,
    onClick: () -> Unit = {},
    rightView: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .heightIn(min = 48.dp)
            .padding(16.dp)
    ) {
        Image(
            painter = iconPainter,
            contentDescription = "",
            modifier = Modifier
                .size(24.dp)
                .align(if (description != null) Alignment.Top else Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = textPrimaryColor
            )
            if (description != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = textSecondaryColor,
                    lineHeight = 16.sp,
                    maxLines = descriptionMaxLines,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (rightView != null) {
            rightView()
        } else {
            Image(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun SettingItemPreview() {
    RunVpnTheme {
        SettingItem(
            iconPainter = painterResource(id = R.drawable.ic_settings_language),
            title = stringResource(id = R.string.setting_hide_disconnect_notification),
            description = "Choose your language for the application",
            onClick = {}
        )
    }
}
