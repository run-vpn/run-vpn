package com.runvpn.app.android.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.android.widgets.RoundedImage
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.sliderInactiveColor
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.core.ui.ultraLightBlue
import com.runvpn.app.feature.common.dialogs.connectionerror.ConnectionErrorComponent
import com.runvpn.app.feature.common.dialogs.connectionerror.FakeConnectionErrorComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionErrorBottomSheet(modifier: Modifier = Modifier, component: ConnectionErrorComponent) {

    AppBottomSheet(
        title = stringResource(R.string.backend_connection_error),
        onDismiss = component::onDismissClicked,
        modifier = modifier
    ) {

        Text(
            text = stringResource(R.string.backend_connection_error_desc),
            fontSize = 14.sp,
            lineHeight = 18.sp,
            color = textErrorColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.you_can_manually_update_apk_version),
            fontSize = 14.sp,
            color = hintTextColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ultraLightBlue)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LinkItem(
                title = stringResource(R.string.in_out_website),
                subtitle = stringResource(R.string.direct_link),
                icon = painterResource(id = R.drawable.ic_settings_language),
                onClick = component::onDownloadApkClick
            )

            LinkItem(
                title = stringResource(id = R.string.telegram_bot),
                subtitle = stringResource(id = R.string.recommended),
                icon = painterResource(id = R.drawable.ic_telegram),
                onClick = component::onTelegramBotClick
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun LinkItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: Painter,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(textWhite)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedImage(
            painter = icon,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .background(sliderInactiveColor, RoundedCornerShape(100f))
                .padding(10.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 14.sp, color = hintTextColor)
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
}


@Preview
@Composable
private fun ConnectionErrorBsPreview() {
    ConnectionErrorBottomSheet(component = FakeConnectionErrorComponent())
}
