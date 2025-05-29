package com.runvpn.app.android.screens.settings.support

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.RoundedImage
import com.runvpn.app.core.common.UrlConstants
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.lightGray
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.feature.settings.support.dialog.FakeSupportDialogComponent
import com.runvpn.app.feature.settings.support.dialog.SupportDialogComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportBs(component: SupportDialogComponent, modifier: Modifier = Modifier) {

    AppBottomSheet(
        title = stringResource(R.string.bs_support_title),
        onDismiss = component::onDismissClicked,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.support_chat_desc),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = hintTextColor, modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ContactItem(
                icon = painterResource(id = R.drawable.ic_telegram),
                contact = UrlConstants.TELEGRAM_APK_BOT,
                onClick = component::onCopyContactClick
            )

            ContactItem(
                icon = painterResource(id = R.drawable.ic_mail),
                contact = UrlConstants.SUPPORT_EMAIL_ADDRESS,
                onClick = component::onCopyContactClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = { component.onOpenTelegramClick(UrlConstants.TELEGRAM_APK_BOT) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_telegram),
                    contentDescription = null,
                    tint = textWhite
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = stringResource(R.string.open_telegram), color = textWhite)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ContactItem(
    icon: Painter,
    contact: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 14.dp)
    ) {
        RoundedImage(
            painter = icon,
            contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .background(lightBlue, RoundedCornerShape(100f))
                .padding(10.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            modifier = Modifier
                .weight(1f)
                .background(lightGray, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick(contact) }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = contact,
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_copy_clipboard),
                contentDescription = ""
            )
        }

    }
}

@Preview
@Composable
private fun SupportBsPreview() {
    SupportBs(component = FakeSupportDialogComponent())
}
