package com.runvpn.app.android.screens.profile.subscription.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.ElevatedContainerView
import com.runvpn.app.android.widgets.TextWithGradientAnimation
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.gradientBlue
import com.runvpn.app.core.ui.gradientLightBlue
import com.runvpn.app.core.ui.greenColor
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.textInputBackgroundColor
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.core.ui.ultraLightBlue
import com.runvpn.app.feature.subscription.activate.shareapkdialog.FakeShareApkFileComponent
import com.runvpn.app.feature.subscription.activate.shareapkdialog.ShareApkFileComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareApkFileBottomSheet(component: ShareApkFileComponent) {

    val state by component.state.subscribeAsState()

    AppBottomSheet(
        title = stringResource(R.string.special_file),
        onDismiss = component::onDismissClicked
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.share_file_desc),
                color = hintTextColor,
                fontSize = 14.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            ElevatedContainerView(containerColor = ultraLightBlue) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(textWhite, RoundedCornerShape(8.dp))
                        .border(
                            2.dp, brush = Brush.linearGradient(
                                listOf(
                                    gradientLightBlue, gradientBlue
                                )
                            ), RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_android),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.apk_file),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                TextWithGradientAnimation(
                                    text = stringResource(R.string.recommended), style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Row {
                                Text(
                                    text = stringResource(R.string.file_downloaded),
                                    fontSize = 14.sp,
                                    color = greenColor,
                                    lineHeight = 20.sp,
                                )

                                Text(
                                    text = " · ",
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = hintTextColor
                                )

                                Text(
                                    text = "82 MB",
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = hintTextColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AppButton(
                        onClick = { /*TODO*/ },
                        containerHeight = 34.dp,
                        cornerSize = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_forward),
                            contentDescription = null,
                            tint = textWhite
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(text = stringResource(id = R.string.share_file), fontSize = 12.sp)
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(textWhite, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .background(textInputBackgroundColor, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = state.link,
                            fontSize = 14.sp,
                            fontWeight = FontWeight(400),
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Image(
                            painter = painterResource(id = R.drawable.ic_copy_clipboard),
                            contentDescription = null
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AppButton(
                        onClick = { /*TODO*/ },
                        containerHeight = 34.dp,
                        cornerSize = 4.dp,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = lightBlue,
                        contentColor = colorIconAccent
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_link),
                            contentDescription = null,
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(text = stringResource(R.string.share_file_link), fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ShareApkDialogPreview() {
    ShareApkFileBottomSheet(FakeShareApkFileComponent())
}
