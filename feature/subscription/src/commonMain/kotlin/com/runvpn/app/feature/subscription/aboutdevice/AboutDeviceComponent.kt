package com.runvpn.app.feature.subscription.aboutdevice

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.subscription.domain.entities.Subscription

interface AboutDeviceComponent {

    data class State(
        val isLoading: Boolean,
        val deviceName: String?,
        val isDeviceNameValid: Boolean,
        val deviceDto: Device,
        val selectedSubscription: Subscription?,
        val subscriptions: List<Subscription>,
        val isSubscriptionActive: Boolean,
        val isSubscriptionSelectedError: Boolean,
        val isSubscriptionActivationLoading: Boolean = false,
        val isNameChangeLoading: Boolean = false,
        val isNameChanged: Boolean = false,
        val isNameError: Boolean = false,
        val isRemoveDeviceAvailable: Boolean = false
    )

    val state: Value<State>

    fun onDeviceNameChange(name: String)

    fun onSubscriptionChoose(subscription: Subscription)
    fun onSubscriptionMoveClick()
    fun onSubscriptionActivateClick()
    fun onPromoClick()
    fun onDeviceRemoveClick()

    fun onSaveDeviceNameClick()
    fun onCancelDeviceNameEditingClick()

    sealed interface Output {
        data object OnActivatePromoRequested : Output
        data object OnBack : Output
    }
}
