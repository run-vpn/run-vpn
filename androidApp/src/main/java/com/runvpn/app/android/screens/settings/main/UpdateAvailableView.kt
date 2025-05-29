package com.runvpn.app.android.screens.settings.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.ElevatedClickableContainerView
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.greenColor
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.profileCardsBackgroundColor
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textLightGrayColor
import com.runvpn.app.core.ui.textPrimaryColor
import com.runvpn.app.core.ui.textSecondaryColor
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.data.device.data.models.update.FileInfo
import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.models.update.UpdateStatus


@Composable
fun UpdateAvailableView(
    modifier: Modifier = Modifier,
    updateStatus: UpdateStatus,
    updateProgress: Long,
    onClick: (fileName: String) -> Unit,
    onRetryClick: (updateInfo: UpdateInfo) -> Unit
) {
    ElevatedClickableContainerView(
        onClick = {
            if (updateStatus is UpdateStatus.Success) {
                onClick(updateStatus.filePath)
            }
            if (updateStatus is UpdateStatus.Error) {
                onRetryClick(updateStatus.updateInfo)
            }
        },
        containerColor = profileCardsBackgroundColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(
                    painterResource(id = R.drawable.ic_update_exits),
                    contentDescription = null,
                    tint = Color.Unspecified
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.update_is_available),
                        fontSize = 16.sp,
                        fontWeight = FontWeight(500),
                        color = textPrimaryColor,
                    )
                    Text(
                        text = stringResource(R.string.description_update_available),
                        fontSize = 10.sp,
                        color = textSecondaryColor,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Box(
                    modifier = Modifier
                        .width(86.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(50))
                        .align(Alignment.CenterVertically)
                        .background(
                            if (updateStatus is UpdateStatus.Downloading)
                                textSecondaryColor
                            else
                                colorIconAccent
                        )
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(
                            id =
                            if (updateStatus is UpdateStatus.Error)
                                R.string.retry
                            else
                                R.string.update
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight(500),
                        color = textWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.padding(start = 25.dp)) {

                when (updateStatus) {
                    is UpdateStatus.Success -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.new_version_downloaded),
                                fontSize = 14.sp,
                                color = greenColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "100%",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = greenColor,
                                    textAlign = TextAlign.Right,
                                )
                            )
                        }
                    }

                    is UpdateStatus.Downloading -> {

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(id = R.string.downloading_in_progress),
                                    fontSize = 14.sp,
                                    color = greenColor,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = "${updateProgress}%",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = greenColor,
                                        textAlign = TextAlign.Right,
                                    )
                                )
                                Text(
                                    text = "/100%",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textLightGrayColor,
                                        textAlign = TextAlign.Right,
                                    )
                                )
                            }

                            LinearProgressIndicator(
                                progress = updateProgress / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = colorIconAccent,
                                trackColor = lightBlue,
                                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                            )
                        }
                    }

                    is UpdateStatus.Error -> {
                        Text(
                            text = stringResource(
                                R.string.update_error_message,
                                updateStatus.message
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp,
                            color = textErrorColor,
                        )
                    }
                }
            }
        }
    }
}

@Preview(locale = "ru-rRU", device = "spec:width=640px,height=1280px,dpi=320")
@Composable
private fun UpdateAvailablePreview() {
    UpdateAvailableView(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        updateStatus = UpdateStatus.Error(
//            50
            "",
            UpdateInfo("", "", false, "", "", FileInfo("", "", ""))
        ),
        updateProgress = 50,
        onClick = {},
        onRetryClick = {})
}
