package com.runvpn.app.feature.subscription.cryptowallets

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.ClipboardManager
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost

internal class DefaultBuyWithCryptoComponent(
    componentContext: ComponentContext,
    private val walletsWithRateCost: List<WalletWithRateCost>,
    private val clipboardManager: ClipboardManager,
    private val onDismissed: () -> Unit
) : BuyWithCryptoComponent, ComponentContext by componentContext {

    private val _state: MutableValue<BuyWithCryptoComponent.State> = MutableValue(
        BuyWithCryptoComponent.State(
            wallets = listOf(),
            cost = 0.0,
            isLoading = true
        )
    )
    override val state: Value<BuyWithCryptoComponent.State> = _state

    init {
        _state.value = _state.value.copy(wallets = walletsWithRateCost, isLoading = false)
    }

    override fun onShowQrCodeClicked() {
        TODO("Not yet implemented")
    }

    override fun onCopyAddressClicked(wallet: WalletWithRateCost) {
        clipboardManager.copy(wallet.address)
    }

    override fun onCopySumClicked(wallet: WalletWithRateCost) {
        clipboardManager.copy(state.value.cost.toString())
    }

    override fun onDismissClicked() {
        onDismissed()
    }
}
