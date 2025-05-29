package com.runvpn.app.feature.serveradd

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.common.models.ConnectionProtocol

class FakeAddServerComponent : AddServerComponent {
    override val configComponent: Value<ChildSlot<*, CustomServerConfigComponent>>
        get() = MutableValue(ChildSlot<Nothing, Nothing>())


    override val state: Value<AddServerComponent.State>
        get() = MutableValue(
            AddServerComponent.State(
                name = "",
                isPublic = false,
                showNameError = false,
                protocol = null,
                connectionStatus = null,
                showTestConnectionResult = false,
                buttonAddEnabled = false,
                isInEditMode = false,
            )
        )

    override fun onProtocolChange(protocol: String) {
        TODO("Not yet implemented")
    }

    override fun onFaqClick(protocol: ConnectionProtocol) {
        TODO("Not yet implemented")
    }


    override fun onNameChange(name: String) {
        TODO("Not yet implemented")
    }


    override fun onServerAddClick() {
        TODO("Not yet implemented")
    }

    override fun onIsPublicChange(isEnable: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onConnectClick() {
        TODO("Not yet implemented")
    }

    override fun onBackClick() {
        TODO("Not yet implemented")
    }

}

