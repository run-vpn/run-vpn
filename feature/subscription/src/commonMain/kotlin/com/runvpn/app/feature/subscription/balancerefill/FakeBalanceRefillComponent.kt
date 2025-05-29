package com.runvpn.app.feature.subscription.balancerefill

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.subscription.domain.entities.WalletBlockchain
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost

class FakeBalanceRefillComponent(
    loadingMode: Boolean = false
) : BalanceRefillComponent {
    override val state: Value<BalanceRefillComponent.State> = MutableValue(
        BalanceRefillComponent.State(
            cost = 10.0,
            costInString = "10.0",
            balance = 5.0,
            wallets = listOf(),
            isLoading = loadingMode,
            currentWallet = WalletWithRateCost(
                WalletBlockchain.BITCOIN,
                "BTC.Bitcoin",
                "adasda",
                "71513.04600000"
            )
        )
    )

    override fun onCostChanged(cost: String) {
        TODO("Not yet implemented")
    }

    override fun onCurrentWalletChanged(walletWithRateCost: WalletWithRateCost) {
        TODO("Not yet implemented")
    }

    override fun onCopyAddressClicked(walletAddress: String) {
        TODO("Not yet implemented")
    }

    override fun onCopySumClicked(costCrypto: String) {
        TODO("Not yet implemented")
    }
}
