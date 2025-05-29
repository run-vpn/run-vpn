package com.runvpn.app.android.screens.profile.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.android.widgets.RoundedImage
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.sliderInactiveColor
import com.runvpn.app.core.ui.textBlack
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.feature.subscription.info.FakeSubscriptionInfoComponent
import com.runvpn.app.feature.subscription.info.SubscriptionInfoComponent


@Composable
fun SubscriptionScreen(component: SubscriptionInfoComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        AppToolBar(
            onBackClick = component::onBackClick,
            iconBack = painterResource(id = R.drawable.ic_arrow_back),
            title = stringResource(id = R.string.your_subscription_title),
            subtitle = stringResource(id = R.string.your_subscription_subtitle)
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            divider {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, lightBlue, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RoundedImage(
                            painter = painterResource(id = R.drawable.ic_round_star),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .background(sliderInactiveColor, RoundedCornerShape(100f))
                                .padding(8.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(verticalArrangement = Arrangement.Center) {
                            val activeSubscriptionText = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = textHintColor
                                    )
                                ) {
                                    append(stringResource(R.string.active_subscriptions))
                                }
                                append(": ")
                                withStyle(
                                    style = SpanStyle(
                                        color = textBlack
                                    )
                                ) {
                                    append(
                                        if (state.subscriptionsCount.activated > 0) {
                                            "${state.subscriptionsCount.activated}"
                                        } else {
                                            stringResource(id = R.string.none)
                                        }
                                    )
                                }
                            }

                            Text(
                                text = activeSubscriptionText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                            )

                            val devicesInAccountText = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = textHintColor
                                    )
                                ) {
                                    append(stringResource(R.string.devices_in_account))
                                }
                                append(": ")
                                withStyle(
                                    style = SpanStyle(
                                        color = textBlack
                                    )
                                ) {
                                    append(
                                        if (state.devicesInfo.isNotEmpty()) {
                                            "${state.devicesInfo.size}"
                                        } else {
                                            stringResource(id = R.string.none)
                                        }
                                    )
                                }
                            }

                            Text(
                                text = devicesInAccountText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = stringResource(R.string.temp_buy_subs_hint),
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                lineHeight = 14.sp,
                                color = colorIconAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppButton(
                            onClick = { component.onBuySubscriptionClick() },
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
                            onClick = { component.onSwitchTariffClick() },
                            cornerSize = 8.dp,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            containerColor = Color(0xFFEBF2FF),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.change_tariff),
                                color = primaryColor,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = stringResource(R.string.my_devices),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (state.subscriptions.isNotEmpty()) {
                divider {
                    SubscriptionSlotElement(
                        subscriptionsCount = state.subscriptions.size,
                        onActivateClick = component::onActivateSubscriptionClicked,
                        onGiveClick = component::onGiveSubscriptionClicked
                    )
                }
            }

            items(state.devicesInfo) { device ->
                DeviceElement(
                    device = device.device,
                    tariff = device.tariff,
                    isCurrentDevice = device.device.uuid == state.currentDeviceUuid,
                    onClick = component::onDeviceClick,
                    modifier = Modifier
                )
            }

            divider {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

private fun LazyGridScope.divider(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SubscriptionScreenPreview() {
    SubscriptionScreen(FakeSubscriptionInfoComponent())
}
