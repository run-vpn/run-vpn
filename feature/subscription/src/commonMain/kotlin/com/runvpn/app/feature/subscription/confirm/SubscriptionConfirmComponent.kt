package com.runvpn.app.feature.subscription.confirm

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.device.Device

interface SubscriptionConfirmComponent {

    data class State(
        val subscriptionId: String,
        val device: Device,
        val isLoading: Boolean
    )

    val state: Value<State>


    fun onConfirmClicked()


    sealed interface Output {

        data object OnActivationSuccess : Output
    }

}
