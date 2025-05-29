package com.runvpn.app.android.screens.profile.main

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.BuildConfig
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.getTariffName
import com.runvpn.app.android.ext.toCurrencyFormat
import com.runvpn.app.android.rubikFontFamily
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.ElevatedContainerView
import com.runvpn.app.android.widgets.LoadingShimmerView
import com.runvpn.app.core.ui.appButtonContainerColor
import com.runvpn.app.core.ui.hintTextBackgroundColor
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.profileCardsBackgroundColor
import com.runvpn.app.core.ui.textAccentColor
import com.runvpn.app.core.ui.textPrimaryColor
import com.runvpn.app.core.ui.textSecondaryColor
import com.runvpn.app.data.device.domain.models.user.UserShortData
import com.runvpn.app.data.settings.domain.Tariff
import com.runvpn.app.feature.profile.main.FakeProfileComponent
import com.runvpn.app.feature.profile.main.ProfileComponent

@Composable
fun ProfileMainScreen(component: ProfileComponent, modifier: Modifier = Modifier) {

    val state by component.state.subscribeAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // User info
        if (state.isLoading) {
            LoadingShimmerView(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .height(140.dp)
            )
        } else {
            state.userShortData?.let { user ->
                UserInfoItem(
                    userShortData = user,
                    onAuthClicked = component::onAuthClick,
                    onLogoutClicked = component::onLogoutClicked,
                    onBalanceClicked = component::onBalanceClicked
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (state.devices == null || state.tariff == null) {
            LoadingShimmerView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )
        } else {
            UserSubscriptionItem(
                deviceCount = state.devices?.size ?: 0,
                tariff = state.tariff ?: Tariff.FREE,
                onSubscriptionClicked = component::onSubscriptionClick,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            AppButton(
                onClick = component::onBuySubscriptionClick,
                cornerSize = 8.dp,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Text(
                    text = stringResource(R.string.buy_subscription),
                    maxLines = 1,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            AppButton(
                onClick = component::onActivatePromoClick,
                cornerSize = 8.dp,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                containerColor = Color(0xFFEBF2FF),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Text(
                    text = stringResource(R.string.activate_by_code),
                    color = primaryColor,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (!BuildConfig.FLAVOR.contains("noproxy")) {
            Spacer(modifier = Modifier.height(24.dp))
            state.tariff?.let {
                TrafficModuleItem(
                    onClick = component::onTrafficModuleClick,
                    modifier = Modifier.fillMaxWidth(),
                    it
                )
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        ReferralProgramItem(
            onReferralClicked = component::onReferralClicked,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun TrafficModuleItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tariff: Tariff
) {
    ElevatedContainerView(
        radius = 12.dp,
        containerColor = profileCardsBackgroundColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp)
        ) {
            Row {
                Text(
                    text = stringResource(id = R.string.usage_statistics),
                    fontFamily = rubikFontFamily,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.clickable { onClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)
            ) {
                if (tariff == Tariff.FREE) {
                    VerticalStat(
                        image = painterResource(id = R.drawable.ic_traff_output),
                        title = "331Mb",
                        subtitle = "Вы дали своего трафика",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }

                VerticalStat(
                    image = painterResource(id = R.drawable.ic_traff_input),
                    title = "8.44Gb",
                    subtitle = "Потратили нашего трафика",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                VerticalStat(
                    image = painterResource(id = R.drawable.ic_median_time),
                    title = "331 Mb",
                    subtitle = "Средний онлайн в день",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun VerticalStat(
    image: Painter,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            textAlign = TextAlign.Center,
            fontFamily = rubikFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = subtitle,
            textAlign = TextAlign.Center,
            fontFamily = rubikFontFamily,
            fontSize = 10.sp,
            lineHeight = 10.sp,
            color = hintTextColor
        )
    }
}

@Composable
private fun UserInfoItem(
    userShortData: UserShortData,
    onAuthClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onBalanceClicked: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (userShortData.email == null)
                    stringResource(id = R.string.profile_action_log_in)
                else userShortData.email ?: "",
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(600),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (userShortData.email == null) stringResource(
                    id = R.string.profile_action_log_in_desc
                ) else stringResource(id = R.string.profile_state_logged_in_desc),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = textSecondaryColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                onClick = {
                    if (userShortData.email != null) {
                        onLogoutClicked()
                    } else {
                        onAuthClicked()
                    }
                },
                containerColor = if (userShortData.email == null) primaryColor else lightBlue
            ) {
                Image(
                    painter = if (userShortData.email == null) painterResource(id = R.drawable.ic_person)
                    else painterResource(id = R.drawable.ic_log_out),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(
                        if (userShortData.email == null) Color.White
                        else textAccentColor
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (userShortData.email == null)
                        stringResource(id = R.string.profile_button_log_in) else
                        stringResource(id = R.string.profile_button_log_out),
                    color = if (userShortData.email == null) Color.White else textAccentColor
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onBalanceClicked() }
                .padding(4.dp)) {
            Image(
                painter = painterResource(id = R.drawable.ic_coin),
                contentDescription = "",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = userShortData.balanceInDollar.toCurrencyFormat(),
                color = textPrimaryColor,
                maxLines = 1,
                fontWeight = FontWeight(500),
                fontSize = 20.sp
            )
            Text(
                text = stringResource(id = R.string.balance),
                color = textSecondaryColor
            )
        }
    }
}

@Composable
private fun UserSubscriptionItem(
    deviceCount: Int,
    tariff: Tariff,
    onSubscriptionClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedContainerView(
        radius = 12.dp,
        modifier = modifier,
        containerColor = profileCardsBackgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSubscriptionClicked() }
                .padding(vertical = 16.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_crystal),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = textSecondaryColor)) {
                            append(stringResource(R.string.your_subscription))
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("\n")
                            append(stringResource(getTariffName(tariff)))
                        }
                    },
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Text(
                text = "$deviceCount " + pluralStringResource(
                    id = R.plurals.devices_format,
                    count = deviceCount
                ),
                color = appButtonContainerColor,
                fontSize = 10.sp,
                lineHeight = 16.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(color = hintTextBackgroundColor)
                    .padding(vertical = 2.dp, horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
fun ReferralProgramItem(
    onReferralClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedContainerView(
        containerColor = profileCardsBackgroundColor,
        radius = 12.dp,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onReferralClicked() }
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_referall),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(id = R.string.referral_program),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimaryColor
            )
            Text(
                text = stringResource(id = R.string.referral_program_desc),
                fontSize = 10.sp,
                lineHeight = 12.sp,
                color = textSecondaryColor
            )
        }
    }
}

@Preview(
    device = "id:pixel_2",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    locale = "ru"
)
@Composable
fun ProfileScreenPreviewLoading() {
    ProfileMainScreen(FakeProfileComponent(isLoading = true))
}


@Preview(
    backgroundColor = 0xFFEFF2F5,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    device = "spec:width=720px,height=1250px,dpi=320",
    showBackground = true, locale = "ru-rRU"
)
@Composable
fun ProfileScreenPreview() {
    ProfileMainScreen(FakeProfileComponent())
}

@Preview(
    device = "id:pixel_2",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    locale = "ru"
)
@Composable
fun ProfileScreenPreviewLoggedIn() {
    ProfileMainScreen(FakeProfileComponent(true))
}


