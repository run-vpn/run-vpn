package com.runvpn.app.feature.home

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.feature.common.dialogs.DialogComponent

interface HomeComponent {

    val reviewDialog: Value<ChildSlot<*, DialogComponent>>

    val state: Value<HomeFeature.State>

    fun onServerClicked(server: Server)
    fun onCurrentServerClick()
    fun onVpnButtonClick()
    fun onShopClick()
    fun onSubscribeInfoClick()

    fun onConnectClick()
    fun onDisconnectClick()

    fun onPermissionsGranted()
    fun offlineElementClicked()

    sealed interface Output {
        data object OnCurrentServerClicked : Output
        data object OnPermissionGranted : Output
    }
}
