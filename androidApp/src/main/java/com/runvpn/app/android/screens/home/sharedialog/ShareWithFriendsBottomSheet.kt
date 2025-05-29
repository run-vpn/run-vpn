package com.runvpn.app.android.screens.home.sharedialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.conditional
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.android.widgets.ElevatedContainerView
import com.runvpn.app.android.widgets.TextWithGradientAnimation
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.dividerColor
import com.runvpn.app.core.ui.gradientBlue
import com.runvpn.app.core.ui.gradientLightBlue
import com.runvpn.app.core.ui.greenColor
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.lightField
import com.runvpn.app.core.ui.textBlack
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.core.ui.textInputBackgroundColor
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.feature.home.sharewithfriends.FakeShareWithFriendsComponent
import com.runvpn.app.feature.home.sharewithfriends.ShareWithFriendsComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareWithFriendsBottomSheet(
    component: ShareWithFriendsComponent,
    modifier: Modifier = Modifier
) {

    val state by component.state.subscribeAsState()

    AppBottomSheet(
        title = stringResource(R.string.title_share_with_friends),
        containerColor = lightField,
        onDismiss = { component.onDismissClicked() }) {

        val expandedItem = remember {
            mutableStateOf(ExpandableElement.SHARE_FILE)
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            InfoItem()

            Spacer(modifier = Modifier.height(8.dp))

            ExpandableContainerElement(
                link = state.apkLink,
                description = stringResource(R.string.share_file_desc),
                isExpanded = expandedItem.value == ExpandableElement.SHARE_FILE,
                onRootClick = {
                    if (expandedItem.value == ExpandableElement.SHARE_FILE) {
                        expandedItem.value = ExpandableElement.NONE
                    } else {
                        expandedItem.value = ExpandableElement.SHARE_FILE
                    }
                },
                shareActionText = stringResource(R.string.share_file),
                onQrCodeClick = component::onShowQrCodeClick
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                                text = stringResource(R.string.recommended),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row {
                            Text(
                                text = stringResource(R.string.file_downloaded), fontSize = 14.sp,
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
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExpandableContainerElement(
                link = state.siteLink,
                description = stringResource(id = R.string.share_file_desc),
                isExpanded = expandedItem.value == ExpandableElement.SHARE_SITE,
                onRootClick = {
                    if (expandedItem.value == ExpandableElement.SHARE_SITE) {
                        expandedItem.value = ExpandableElement.NONE
                    } else {
                        expandedItem.value = ExpandableElement.SHARE_SITE
                    }
                },
                shareActionText = stringResource(R.string.share_link),
                onQrCodeClick = component::onShowQrCodeClick
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings_language),
                        contentDescription = null,
                        tint = colorIconAccent,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(50))
                            .background(lightBlue)
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string._site, "VPN.RUN"),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Spacer(modifier = Modifier.height(2.dp))


                        Text(
                            text = stringResource(R.string.ref_link_to_site),
                            fontSize = 14.sp,
                            color = hintTextColor,
                            lineHeight = 20.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExpandableContainerElement(
                link = state.tgLink,
                description = stringResource(id = R.string.share_file_desc),
                isExpanded = expandedItem.value == ExpandableElement.SHARE_TG,
                onRootClick = {
                    if (expandedItem.value == ExpandableElement.SHARE_TG) {
                        expandedItem.value = ExpandableElement.NONE
                    } else {
                        expandedItem.value = ExpandableElement.SHARE_TG
                    }
                },
                shareActionText = stringResource(R.string.share_file),
                onQrCodeClick = component::onShowQrCodeClick
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_telegram),
                        contentDescription = null,
                        tint = colorIconAccent,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(50))
                            .background(lightBlue)
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.telegram_bot),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = stringResource(R.string.telegram_bot_sends_file),
                            fontSize = 14.sp,
                            color = hintTextColor,
                            lineHeight = 20.sp,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InfoItem() {
    ElevatedContainerView {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_gift),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.what_your_earn_from_friends),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = dividerColor)

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "1",
                    color = colorIconAccent,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color = lightBlue)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.what_you_earn_from_friends_desc),
                        color = textBlack,
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(R.string.profitable_terms),
                        fontSize = 12.sp, color = greenColor,
                        lineHeight = 16.sp
                    )
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "2",
                    color = colorIconAccent,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color = lightBlue)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.what_your_earn_from_friends_play_market),
                        color = textHintColor,
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { }
                        .padding(8.dp))
                {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_stats),
                        contentDescription = null,
                        tint = colorIconAccent
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = stringResource(R.string.statistics),
                        color = colorIconAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { }
                        .padding(8.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_transfer_money),
                        contentDescription = null,
                        tint = colorIconAccent
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = stringResource(R.string.withdraw_money),
                        color = colorIconAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                }
            }
        }
    }
}

@Composable
private fun ExpandableContainerElement(
    link: String,
    description: String,
    isExpanded: Boolean = true,
    onRootClick: () -> Unit,
    onQrCodeClick: (String) -> Unit,
    shareActionText: String,
    titleContext: @Composable () -> Unit
) {

    ElevatedContainerView(
        radius = 16.dp,
        modifier = Modifier
            .conditional(isExpanded) {
                border(
                    2.dp, brush = Brush.linearGradient(
                        listOf(
                            gradientLightBlue,
                            gradientBlue
                        )
                    ),
                    RoundedCornerShape(16.dp)
                )
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRootClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            titleContext()

            Spacer(modifier = Modifier.weight(1f))


            Icon(
                painter = painterResource(id = R.drawable.ic_server_group_toggle),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .rotate(if (isExpanded) 0f else 180f),
                tint = Color.Unspecified

            )

        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                HorizontalDivider(color = dividerColor)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = description,
                    color = hintTextColor,
                    fontSize = 14.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(textInputBackgroundColor, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = link,
                        fontSize = 14.sp,
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

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(lightBlue)
                            .clickable { onQrCodeClick(link) }
                            .padding(horizontal = 16.dp, vertical = 8.dp))
                    {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = null,
                            tint = colorIconAccent
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = stringResource(id = R.string.qr_code),
                            color = colorIconAccent,
                            fontSize = 14.sp,
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorIconAccent)
                            .clickable { }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center)
                    {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_link),
                            contentDescription = null,
                            tint = textWhite
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = shareActionText,
                            color = textWhite,
                            fontSize = 14.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private enum class ExpandableElement {
    NONE,
    SHARE_FILE,
    SHARE_SITE,
    SHARE_TG
}


@Preview(showBackground = true, backgroundColor = 0xFFC6C6C6)
@Composable
private fun ShareWithFriendsViewPreview() {
    ShareWithFriendsBottomSheet(FakeShareWithFriendsComponent())
}
