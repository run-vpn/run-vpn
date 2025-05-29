package com.runvpn.app.android.screens.profile.subscription.buysubscription

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.toCurrencyFormat
import com.runvpn.app.android.utils.dispatchOnBackPressed
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.android.widgets.AppCheckBox
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.android.widgets.BorderedContainerView
import com.runvpn.app.core.ui.lightBlue
import com.runvpn.app.core.ui.textBlack
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.core.ui.textSecondaryColor
import com.runvpn.app.data.subscription.data.network.Rate
import com.runvpn.app.feature.subscription.buy.BuySubscriptionComponent
import com.runvpn.app.feature.subscription.buy.FakeBuySubscriptionComponent
import com.runvpn.app.feature.subscription.tariff.ChooseRateComponent


@Composable
fun BuySubscriptionScreen(component: BuySubscriptionComponent) {
    val state by component.state.subscribeAsState()
    val dialogChild by component.dialogSlot.subscribeAsState()

    val context = LocalContext.current

    var showSelectDeviceBottomSheet by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppToolBar(
            title = stringResource(id = R.string.buy_subscription_title),
            subtitle = stringResource(id = R.string.buy_subscription_desc),
            onBackClick = { dispatchOnBackPressed(context) },
            iconBack = painterResource(id = R.drawable.ic_arrow_back)
        )

        Spacer(modifier = Modifier.height(24.dp))

        (state as? BuySubscriptionComponent.State.Loading)?.let {
            Spacer(modifier = Modifier.weight(1f))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.weight(1f))
        }

        (state as? BuySubscriptionComponent.State.Loaded)?.let {
            BalanceView(balance = it.balance)
            Spacer(modifier = Modifier.height(16.dp))
            SelectedTariffView(
                tariff = it.chosenTariff,
                deviceCount = it.deviceCount,
                periodInDays = it.periodInDays,
                cost = it.calculatedCost,
                onEditClick = component::onEditTariffClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            if (!it.isEnoughFunds) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(
                        id = R.string.missing_funds,
                        (it.calculatedCost - it.balance).toCurrencyFormat()
                    ),
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = textErrorColor,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(2.dp, lightBlue, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.choose_device_desc),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = textHintColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                AppTextField(modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showSelectDeviceBottomSheet = true }
                    .focusable(false),
                    value = if (it.selectedDeviceUuids.isNotEmpty()) stringResource(
                        R.string.selected_devices, it.selectedDeviceUuids.size
                    )
                    else "",
                    onValueChanged = {},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    placeholder = stringResource(R.string.choose_device),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_bottom), null
                        )
                    },
                    disabledPlaceholderColor = textBlack,
                    disabledTextColor = textBlack,
                    enabled = false
                )

            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AppButton(
            onClick = component::onBuyClicked,
            enabled = !((state as? BuySubscriptionComponent.State.Loaded)?.isBuyingLoading
                ?: false),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            if ((state as? BuySubscriptionComponent.State.Loaded)?.isBuyingLoading == true) {
                CircularProgressIndicator(modifier = Modifier.size(30.dp))
            } else if ((state as? BuySubscriptionComponent.State.Loaded)?.isEnoughFunds == true) {
                Text(text = stringResource(id = R.string.buy))
            } else {
                Text(text = stringResource(id = R.string.refill_balance))
            }
        }
    }

    if (showSelectDeviceBottomSheet) {
        (state as? BuySubscriptionComponent.State.Loaded)?.let { loadedState ->
            SelectDeviceBottomSheet(title = stringResource(id = R.string.your_devices),
                items = loadedState.devices,
                subtitle = stringResource(id = R.string.selected) +
                        " ${loadedState.selectedDeviceUuids.size} " +
                        pluralStringResource(
                            id = R.plurals.devices_format,
                            count = loadedState.selectedDeviceUuids.size
                        ) + " " +
                        stringResource(id = R.string.from) +
                        " ${loadedState.devices.size} " +
                        stringResource(id = R.string.available),
                onItemClick = {
                    component.onActivateDevicesSelected(it.uuid)
                },
                onDismiss = { showSelectDeviceBottomSheet = false }) { device ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppCheckBox(checked = loadedState.selectedDeviceUuids.contains(device.uuid),
                        onCheckedChange = { component.onActivateDevicesSelected(device.uuid) })

                    Column {
                        Text(
                            text = device.fullName ?: stringResource(id = R.string.dev_unknown),
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(
                                id = if (device.latestSubscriptionUuid == null) R.string.no_subscription
                                else R.string.subscription_status_active
                            ), fontSize = 12.sp, color = textHintColor
                        )
                    }
                }
            }
        }
    }

    when (val dialog = dialogChild.child?.instance) {
        is ChooseRateComponent -> ChooseRateBsDialog(component = dialog)
    }

}

@Composable
private fun BalanceView(
    balance: Double, modifier: Modifier = Modifier
) {
    BorderedContainerView(
        strokeWidth = 2.dp, modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_coin),
                contentDescription = "",
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column {
                Text(
                    text = balance.toCurrencyFormat(),
                    fontWeight = FontWeight.Medium,
                    fontSize = 24.sp,
                    lineHeight = 24.sp
                )

                Text(
                    text = stringResource(id = R.string.balance),
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    color = textSecondaryColor
                )
            }
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun SelectedTariffView(
    tariff: Rate,
    deviceCount: Int,
    periodInDays: Int,
    cost: Double,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BorderedContainerView(
        strokeWidth = 2.dp, modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TariffDescPart(
                title = stringResource(id = R.string.tariff_buy_cost),
                value = cost.toCurrencyFormat(),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            TariffDescPart(
                title = stringResource(id = R.string.tariff_device_max),
                value = deviceCount.toString(),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            TariffDescPart(
                title = stringResource(id = R.string.tariff_expire_in),
                value = periodInDays.toString(),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onEditClick, modifier = Modifier.size(36.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pen),
                    contentDescription = "",
                )
            }
        }
    }
}

@Composable
private fun TariffDescPart(
    title: String, value: String, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textSecondaryColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value, fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium
        )
    }
}

@Preview(
    showSystemUi = true, device = "id:pixel_7", showBackground = true, backgroundColor = 0xFFFFFFFF,
    locale = "ru-rRU"
)
@Composable
private fun BuySubscriptionScreenDefaultPreview() {
    BuySubscriptionScreen(component = FakeBuySubscriptionComponent(isLoading = false))
}
