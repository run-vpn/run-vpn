package com.runvpn.app.android.screens.profile.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.getTariffNameForDevice
import com.runvpn.app.android.ext.toDateLocalized
import com.runvpn.app.android.widgets.BorderedContainerView
import com.runvpn.app.android.widgets.RoundedImage
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.greenColor
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.profileCardsBackgroundColor
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.data.device.TestDataDevices.testDevicesList
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.settings.domain.Tariff

private const val WINDOWS_OS = "windows"
private const val ANDROID_OS = "android"

@Composable
fun DeviceElement(
    modifier: Modifier = Modifier,
    device: Device,
    tariff: Tariff,
    isCurrentDevice: Boolean = true,
    /** Временное решение (текущее устройство всегда будет отображаться как онлайн),
     * пока не будет ТЗ и реализован весь флоу*/
    isOnline: Boolean = isCurrentDevice,
    onClick: (Device) -> Unit
) {
    BorderedContainerView(
        modifier = modifier,
        containerColor = profileCardsBackgroundColor,
        strokeColor = if (isCurrentDevice) colorIconAccent else Color.Transparent,
        strokeWidth = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick(device) }
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            Row {
                Box(modifier = Modifier) {
                    RoundedImage(
                        painter = painterResource(id = getPlatformIcon(device.software.platformName)),
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .background(lightBlue, RoundedCornerShape(100f))
                            .padding(6.dp)
                    )

                    if (isOnline) {
                        Box(
                            modifier = Modifier
                                .offset(x = 2.dp, y = 2.dp)
                                .size(10.dp)
                                .clip(RoundedCornerShape(50))
                                .border(1.5.dp, profileCardsBackgroundColor, RoundedCornerShape(50))
                                .padding(1.7.dp)
                                .background(greenColor)
                                .align(Alignment.BottomEnd)
                        )
                    }

                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .weight(1f)
                ) {
                    Text(
                        text = device.fullName ?: stringResource(R.string.dev_unknown),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    Text(
                        text = if (isOnline) {
                            stringResource(R.string.online)
                        } else {
                            stringResource(
                                R.string.last_online_with_time,
                                device.activity?.createdAt?.toDateLocalized()
                                    ?: stringResource(R.string.dev_unknown)
                            )
                        },
                        fontSize = 10.sp,
                        color = if (isOnline) greenColor else hintTextColor,
                        lineHeight = 12.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text =
                stringResource(getTariffNameForDevice(tariff)),
                color = getTariffColor(tariff),
                fontSize = 10.sp,
                lineHeight = 12.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

private fun getPlatformIcon(platformName: String?) = when (platformName?.lowercase()) {
    WINDOWS_OS -> R.drawable.ic_platform_windows
    ANDROID_OS -> R.drawable.ic_platform_android
    else -> R.drawable.ic_platform_android
}


private fun getTariffColor(tariff: Tariff) = when (tariff) {
    Tariff.FREE -> textHintColor
    Tariff.PAID -> greenColor
    Tariff.EXPIRED -> textErrorColor
}

@Preview(locale = "ru")
@Composable
fun DeviceElementPreview() {
    Row(modifier = Modifier.fillMaxWidth()) {
        DeviceElement(
            device = testDevicesList[0],
            onClick = {},
            tariff = Tariff.FREE,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        DeviceElement(
            device = testDevicesList[0],
            onClick = {},
            isCurrentDevice = false,
            tariff = Tariff.FREE,
            modifier = Modifier.weight(1f)
        )
    }

}
