package com.runvpn.app.android.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.installUpdate
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.ElevatedContainerView
import com.runvpn.app.core.common.UrlConstants
import com.runvpn.app.core.ui.bottomSheetScrimColor
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.greenColor
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.core.ui.textLightGrayColor
import com.runvpn.app.data.device.domain.models.update.UpdateStatus
import com.runvpn.app.feature.settings.update.FakeUpdateComponent
import com.runvpn.app.feature.settings.update.UpdateComponent

@Composable
fun UpdateScreen(component: UpdateComponent, modifier: Modifier = Modifier) {

    val interactionSource = remember { MutableInteractionSource() }

    val state by component.state.subscribeAsState()
    val context = LocalContext.current

    Column(modifier = modifier
        .fillMaxSize()
        .background(bottomSheetScrimColor)
        .clickable(
            interactionSource = interactionSource, indication = null
        ) {}
        .padding(horizontal = 16.dp), verticalArrangement = Arrangement.Center) {
        ElevatedContainerView {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_dialog_update),
                    contentDescription = null,
                    modifier = Modifier.size(140.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.force_update_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = stringResource(R.string.force_update_desc),
                    color = hintTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (state.selfUpdateAllowed) {
                    state.updateStatus.let { updateStatus ->
                        state.updateProgress.let { updateProgress ->


                            Column {
                                when (updateStatus) {
                                    is UpdateStatus.Success -> {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = stringResource(R.string.new_version_downloaded),
                                                fontSize = 14.sp,
                                                color = greenColor,
                                                overflow = TextOverflow.Ellipsis,
                                                maxLines = 1,
                                                modifier = Modifier.weight(1f)
                                            )

                                            Text(
                                                text = "100%", style = TextStyle(
                                                    fontSize = 14.sp,
                                                    lineHeight = 20.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = greenColor,
                                                    textAlign = TextAlign.Right,
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        LinearProgressIndicator(
                                            progress = 100f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = greenColor,
                                            trackColor = lightBlue,
                                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                                        )
                                    }


                                    is UpdateStatus.Error -> {
                                        Text(
                                            text = stringResource(
                                                R.string.update_error_message, updateStatus.message
                                            ),
                                            fontSize = 14.sp,
                                            color = textErrorColor,
                                        )
                                    }

                                    else -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Row {
                                                Text(
                                                    text = stringResource(id = R.string.downloading_in_progress),
                                                    fontSize = 14.sp,
                                                    color = greenColor,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )

                                                Text(
                                                    text = "$updateProgress%",
                                                    style = TextStyle(
                                                        fontSize = 14.sp,
                                                        lineHeight = 20.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = greenColor,
                                                        textAlign = TextAlign.Right,
                                                    )
                                                )
                                                Text(
                                                    text = "/100%", style = TextStyle(
                                                        fontSize = 14.sp,
                                                        lineHeight = 20.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = textLightGrayColor,
                                                        textAlign = TextAlign.Right,
                                                    )
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            LinearProgressIndicator(
                                                progress = (updateProgress ?: 0) / 100f,
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
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                AppButton(
                    onClick = {
                        if (state.updateStatus is UpdateStatus.Error) {
                            component.retryClick((state.updateStatus as UpdateStatus.Error).updateInfo)
                        } else if (state.selfUpdateAllowed) {
                            context.installUpdate((state.updateStatus as UpdateStatus.Success).filePath)
                        } else {
                            component.onUpdateClick()
                        }
                    },
                    enabled = state.updateStatus is UpdateStatus.Success ||
                            !state.selfUpdateAllowed ||
                            state.updateStatus is UpdateStatus.Error,
                    modifier = Modifier.fillMaxWidth()
                ) {


                    Text(
                        text = stringResource(
                            id = if (state.updateStatus is UpdateStatus.Error) {
                                R.string.retry
                            } else {
                                R.string.update
                            }
                        )
                    )

                }

                Spacer(modifier = Modifier.height(40.dp))

                Column(modifier = Modifier.fillMaxWidth()) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.update_issues),
                            fontWeight = FontWeight.Medium,
                            color = textHintColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        HorizontalDivider(modifier = Modifier.weight(1f))

                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.updated_issues_desc),
                        fontSize = 12.sp,
                        color = textHintColor
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = UrlConstants.MAIN_SITE,
                        color = primaryColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { component.onOpenLinkClick(UrlConstants.MAIN_SITE) })

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = UrlConstants.WEBSITE_DL,
                        color = primaryColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { component.onOpenLinkClick(UrlConstants.WEBSITE_DL) })

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = UrlConstants.WEBSITE_APK,
                        color = primaryColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { component.onOpenLinkClick(UrlConstants.WEBSITE_APK) })

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = UrlConstants.TELEGRAM_APK_BOT,
                        color = primaryColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { component.onOpenLinkClick(UrlConstants.TELEGRAM_APK_BOT) })

                }
            }
        }
    }
}


@Preview(backgroundColor = 0xFFE0E0E0, showBackground = true)
@Composable
private fun UpdateScreenPreview() {
    UpdateScreen(component = FakeUpdateComponent(true))
}

