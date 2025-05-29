package com.runvpn.app.feature.subscription.balancerefill

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost

interface BalanceRefillComponent {


    data class State(
        val wallets: List<WalletWithRateCost>,
        val currentWallet: WalletWithRateCost?,
        val cost: Double,
        val costInString: String,
        val balance: Double,
        val isLoading: Boolean,
    )

    val state: Value<State>

    fun onCostChanged(cost: String)

    fun onCurrentWalletChanged(walletWithRateCost: WalletWithRateCost)

    fun onCopyAddressClicked(walletAddress: String)
    fun onCopySumClicked(costCrypto: String)

}
