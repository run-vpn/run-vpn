package com.runvpn.app.android.screens.profile.subscription.buysubscription

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.toBlockchainIconResource
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.core.ui.dividerColor
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.data.subscription.domain.entities.WalletBlockchain
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseCryptoCurrencyBottomSheet(
    cryptoWallets: List<WalletWithRateCost>,
    onWalletSelected: (WalletWithRateCost) -> Unit,
    onDismiss: () -> Unit
) {

    AppBottomSheet(title = stringResource(R.string.replenishment_method), onDismiss = onDismiss) {
        LazyColumn {
            items(cryptoWallets) {

                CryptoCurrencyItem(it, onWalletSelected, onDismiss)

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = dividerColor
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }

}

@Composable
fun CryptoCurrencyItem(
    walletWithRateCost: WalletWithRateCost,
    onWalletSelected: (WalletWithRateCost) -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onWalletSelected(walletWithRateCost)
                onDismiss()
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = walletWithRateCost.blockchain.toBlockchainIconResource()),
            contentDescription = null,
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = walletWithRateCost.name,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = stringResource(R.string.crypto_method_desc),
                color = hintTextColor,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = null
        )

    }

}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ChooseCryptoCurrencyBottomSheetPreview() {
    ChooseCryptoCurrencyBottomSheet(
        listOf(
            WalletWithRateCost(WalletBlockchain.BITCOIN, "BTC.Bitcoin", "", "71.2210212"),
            WalletWithRateCost(WalletBlockchain.BITCOIN, "BTC.Bitcoin", "", "71.2210212"),
            WalletWithRateCost(WalletBlockchain.BITCOIN, "BTC.Bitcoin", "", "71.2210212"),
            WalletWithRateCost(WalletBlockchain.BITCOIN, "BTC.Bitcoin", "", "71.2210212"),
        ),
        {},
        {}
    )
}
