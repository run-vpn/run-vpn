package com.runvpn.app.feature.profile.main

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.device.domain.models.user.UserShortData
import com.runvpn.app.data.settings.domain.Tariff

interface ProfileComponent {

    data class State(
        val isLoading: Boolean,
        val userShortData: UserShortData?,
        val devices: List<Device>?,
        val tariff: Tariff?
    )

    val state: Value<State>

    fun onAuthClick()
    fun onSubscriptionClick()
    fun onReferralClicked()
    fun onLogoutClicked()
    fun onTrafficModuleClick()
    fun onBuySubscriptionClick()
    fun onActivatePromoClick()

    fun onBalanceClicked()

    sealed interface Output {
        data object OnAuthScreenRequest : Output
        data object OnTrafficModuleScreenRequest : Output
        data object OnBuySubscriptionRequested : Output
        data object OnActivatePromoRequested : Output

    }
}

