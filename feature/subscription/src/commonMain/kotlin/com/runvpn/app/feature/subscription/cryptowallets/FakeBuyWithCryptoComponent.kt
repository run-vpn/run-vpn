package com.runvpn.app.feature.subscription.cryptowallets

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.subscription.domain.entities.WalletBlockchain
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost

class FakeBuyWithCryptoComponent : BuyWithCryptoComponent {
    override val state: Value<BuyWithCryptoComponent.State> = MutableValue(
        BuyWithCryptoComponent.State(
            wallets = listOf(
                WalletWithRateCost(
                    WalletBlockchain.BITCOIN,
                    "BITCOIN",
                    "0xkajsdfjasdhfaskjdf",
                    rate = "71458.19010000"
                )
            ),
            cost = 0.0,
            isLoading = false
        )
    )

    override fun onShowQrCodeClicked() {
        TODO("Not yet implemented")
    }

    override fun onCopyAddressClicked(wallet: WalletWithRateCost) {
        TODO("Not yet implemented")
    }

    override fun onCopySumClicked(wallet: WalletWithRateCost) {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }
}
