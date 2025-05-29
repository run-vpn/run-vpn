package com.runvpn.app.feature.subscription.cryptowallets

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface BuyWithCryptoComponent : SimpleDialogComponent {

    data class State(
        val wallets: List<WalletWithRateCost>,
        val cost: Double,
        val isLoading: Boolean,
    )

    val state: Value<State>

    fun onShowQrCodeClicked()

    fun onCopyAddressClicked(wallet: WalletWithRateCost)
    fun onCopySumClicked(wallet: WalletWithRateCost)
}
