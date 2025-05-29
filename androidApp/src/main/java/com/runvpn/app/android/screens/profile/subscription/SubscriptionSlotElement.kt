package com.runvpn.app.android.screens.profile.subscription

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.core.ui.textWhite

@Composable
fun SubscriptionSlotElement(
    subscriptionsCount: Int,
    modifier: Modifier = Modifier,
    onActivateClick: () -> Unit,
    onGiveClick: () -> Unit,
) {
    Box(modifier = modifier
        .clip(RoundedCornerShape(12.dp))
        .clickable { onActivateClick() }) {
        Column(
            modifier = Modifier
                .border(2.dp, lightBlue, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_stack),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.not_activated_subscription),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Available subscription slots: $subscriptionsCount",
                        fontSize = 12.sp,
                        color = textHintColor,
                        lineHeight = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                AppButton(
                    onClick = { onGiveClick() },
                    cornerSize = 8.dp,
                    containerColor = Color(0xFFEBF2FF),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                ) {
                    Text(
                        text = stringResource(R.string.give),
                        color = primaryColor,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))

                AppButton(
                    onClick = { onActivateClick() },
                    cornerSize = 8.dp,
                    containerColor = Color(0xFFEBF2FF),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                ) {
                    Text(
                        text = stringResource(R.string.activate),
                        color = primaryColor,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

        }

        Icon(
            painter = painterResource(id = R.drawable.ic_add_tint),
            contentDescription = null,
            tint = textHintColor,
            modifier = Modifier
                .background(textWhite)
                .align(Alignment.TopEnd)
        )
    }

}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun SubscriptionSlotElementPreview() {
    SubscriptionSlotElement(
        subscriptionsCount = 5,
        onActivateClick = {},
        onGiveClick = {},
        modifier = Modifier.width(200.dp)
    )
}
