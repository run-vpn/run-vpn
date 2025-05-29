package com.runvpn.app.feature.home.sharewithfriends

import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.common.dialogs.DialogComponent

interface ShareWithFriendsComponent : DialogComponent {

    data class State(
        val apkLink: String,
        val siteLink: String,
        val tgLink: String
    )

    val state: Value<State>

    fun onShowQrCodeClick(link: String)

    fun onShareFileClick()

    fun onShareLinkClick(link: String)

    sealed interface Output {
        data class OnShowQrCode(val link: String) : Output
        data object OnDismiss : Output
    }

}
