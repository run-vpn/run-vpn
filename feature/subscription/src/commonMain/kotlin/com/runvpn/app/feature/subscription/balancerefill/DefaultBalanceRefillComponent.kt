package com.runvpn.app.feature.subscription.balancerefill

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.ClipboardManager
import com.runvpn.app.data.device.domain.usecases.user.GetUserShortDataUseCase
import com.runvpn.app.data.subscription.domain.GetWalletsUseCase
import com.runvpn.app.data.subscription.domain.entities.WalletWithRateCost
import com.runvpn.app.feature.subscription.toPriceFormat
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class DefaultBalanceRefillComponent(
    componentContext: ComponentContext,
    private val clipboardManager: ClipboardManager,
    private val messageService: MessageService,
    private val getWalletsUseCase: GetWalletsUseCase,
    private val getUserShortDataUseCase: GetUserShortDataUseCase,
    exceptionHandler: CoroutineExceptionHandler,
    cost: Double = 0.0,
) : BalanceRefillComponent, ComponentContext by componentContext {

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    private val _state = MutableValue(
        BalanceRefillComponent.State(
            isLoading = true,
            cost = 0.0,
            costInString = "",
            balance = 0.0,
            wallets = listOf(),
            currentWallet = null
        )
    )

    override val state: Value<BalanceRefillComponent.State> = _state

    init {
        coroutineScope.launch {
            val result = getWalletsUseCase()
            val wallets = result.getOrThrow()

            val userShortData = getUserShortDataUseCase().getOrThrow()
            val balance = userShortData.balanceInDollar

            if (wallets.isNotEmpty()) {
                _state.value = state.value.copy(currentWallet = wallets.first())
            }
            _state.value = state.value.copy(wallets = wallets, balance = balance, isLoading = false)
        }

        if (cost > 0.0) {
            updateCost(cost.toString())
        }
    }

    override fun onCostChanged(cost: String) {
        updateCost(cost)
    }

    override fun onCurrentWalletChanged(walletWithRateCost: WalletWithRateCost) {
        _state.value = state.value.copy(currentWallet = walletWithRateCost)
    }

    override fun onCopyAddressClicked(walletAddress: String) {
        clipboardManager.copy(walletAddress)
        messageService.showMessage(AppMessage.CopyToClipboard())
    }

    override fun onCopySumClicked(costCrypto: String) {
        clipboardManager.copy(costCrypto)
        messageService.showMessage(AppMessage.CopyToClipboard())
    }

    private fun updateCost(cost: String) {
        val amount = cost.toPriceFormat(current = _state.value.costInString) ?: return
        val amountInDouble = amount.toDoubleOrNull() ?: 0.0

        _state.value = state.value.copy(costInString = amount, cost = amountInDouble)
    }
}
