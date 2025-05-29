package com.runvpn.app.android.screens.home.sharedialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.rememberQrBitmapPainter
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.feature.common.dialogs.shareqrcode.FakeShareQrCodeComponent
import com.runvpn.app.feature.common.dialogs.shareqrcode.ShareQrCodeComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareQrCodeBottomSheet(component: ShareQrCodeComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()

    AppBottomSheet(
        title = stringResource(R.string.share_qr_code),
        onDismiss = component::onDismissClicked
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
            Text(
                text = stringResource(R.string.share_qr_code_to_friends_desc),
                fontSize = 14.sp,
                color = hintTextColor,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = rememberQrBitmapPainter(
                        content = state.link,
                        size = 230.dp,
                        padding = 1.dp
                    ),
                    contentDescription = null
                )
                Text(
                    text = stringResource(id = R.string.vpn_run),
                    textAlign = TextAlign.Center,
                    color = colorIconAccent,
                    fontSize = 10.sp,
                    lineHeight = 45.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .size(50.dp)
                        .background(textWhite)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_link),
                    contentDescription = null,
                    tint = textWhite
                )
                Text(text = stringResource(R.string.share_qr_code), color = textWhite)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Preview
@Composable
private fun ShareQrCodeBottomSheetPreview() {
    ShareQrCodeBottomSheet(component = FakeShareQrCodeComponent())
}
