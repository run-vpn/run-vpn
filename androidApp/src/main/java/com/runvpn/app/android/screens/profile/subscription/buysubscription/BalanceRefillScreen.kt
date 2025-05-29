package com.runvpn.app.android.screens.profile.subscription.buysubscription

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.rememberQrBitmapPainter
import com.runvpn.app.android.ext.toBlockchainIconResource
import com.runvpn.app.android.utils.dispatchOnBackPressed
import com.runvpn.app.android.widgets.AppTextField
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.android.widgets.LoadingShimmerView
import com.runvpn.app.android.widgets.TextElementWithCopy
import com.runvpn.app.core.ui.blueHint
import com.runvpn.app.core.ui.dividerColor
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.textBlack
import com.runvpn.app.core.ui.textInputBackgroundColor
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost
import com.runvpn.app.feature.subscription.balancerefill.BalanceRefillComponent
import com.runvpn.app.feature.subscription.balancerefill.FakeBalanceRefillComponent
import java.text.NumberFormat

@Composable
fun BalanceRefillScreen(component: BalanceRefillComponent, modifier: Modifier = Modifier) {

    val state by component.state.subscribeAsState()

    val context = LocalContext.current
    var showChoosePaymentCurrencyBottomSheet by remember { mutableStateOf(false) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AppToolBar(
            onBackClick = { dispatchOnBackPressed(context) },
            title = stringResource(R.string.refill_balance),
            subtitle = stringResource(R.string.refill_balance_desc),
            iconBack = painterResource(id = R.drawable.ic_arrow_back)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppTextField(
                    value = state.costInString,
                    placeholder = "",
                    onValueChanged = component::onCostChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    leadingIcon = {
                        Text(
                            text = "$",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            color = textBlack,
                            modifier = Modifier.width(40.dp)
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 24.sp, fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = stringResource(R.string.refill_amount), color = blueHint)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.your_balance_in_usd_, state.balance),
                    fontSize = 18.sp
                )
            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), color = dividerColor
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(id = R.string.payment_system),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (state.isLoading || state.currentWallet == null) {
                    LoadingShimmerView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LoadingShimmerView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 16.dp)
                    )
                } else {
                    WalletsItem(
                        cost = state.cost,
                        currentWallet = state.currentWallet!!,
                        onShowChoosePaymentClick = { showChoosePaymentCurrencyBottomSheet = true },
                        onCopyAddressClick = component::onCopyAddressClicked,
                        onCopySumClick = component::onCopySumClicked
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

    }

    if (showChoosePaymentCurrencyBottomSheet) {
        ChooseCryptoCurrencyBottomSheet(
            cryptoWallets = state.wallets,
            onWalletSelected = component::onCurrentWalletChanged,
            onDismiss = { showChoosePaymentCurrencyBottomSheet = false }
        )
    }
}

@Composable
private fun WalletsItem(
    cost: Double,
    currentWallet: WalletWithRateCost,
    onShowChoosePaymentClick: () -> Unit,
    onCopyAddressClick: (String) -> Unit,
    onCopySumClick: (String) -> Unit
) {

    val format = NumberFormat.getInstance()
    format.maximumFractionDigits = 8
    val formattedCost = format.format((cost / currentWallet.rate.toDouble()))


    AppTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onShowChoosePaymentClick() }
            .focusable(false),
        value = currentWallet.currency,
        onValueChanged = {},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        placeholder = stringResource(R.string.currency),
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_bottom), null
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = currentWallet.blockchain.toBlockchainIconResource()),
                contentDescription = null,
                tint = Color.Unspecified
            )
        },
        disabledPlaceholderColor = textBlack,
        disabledTextColor = textBlack,
        enabled = false
    )

    Spacer(modifier = Modifier.height(20.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                2.dp,
                textInputBackgroundColor,
                RoundedCornerShape(16.dp)
            )
            .padding(20.dp),
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(id = currentWallet.blockchain.toBlockchainIconResource()),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = currentWallet.currency,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextElementWithCopy(
            value = currentWallet.address,
            onClick = onCopyAddressClick
        )

        Spacer(modifier = Modifier.height(12.dp))


        TextElementWithCopy(
            value = formattedCost,
            suffix = " ${currentWallet.ticket}",
            onClick = onCopySumClick
        )

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberQrBitmapPainter(
                    content = currentWallet.address,
                    size = 150.dp,
                    padding = 1.dp
                ),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(
                    R.string.qr_code_to_refill_,
                    formattedCost,
                    currentWallet.currency
                ),
                textAlign = TextAlign.Center,
                color = hintTextColor,
                fontSize = 14.sp
            )
        }
    }
}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun BalanceRefillScreenPreview() {
    BalanceRefillScreen(component = FakeBalanceRefillComponent())
}


@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun LoadingBalanceRefillScreenPreview() {
    BalanceRefillScreen(component = FakeBalanceRefillComponent(loadingMode = true))
}
