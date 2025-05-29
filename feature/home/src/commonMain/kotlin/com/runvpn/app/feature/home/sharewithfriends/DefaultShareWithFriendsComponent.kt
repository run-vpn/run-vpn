package com.runvpn.app.feature.home.sharewithfriends

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class DefaultShareWithFriendsComponent(
    componentContext: ComponentContext,
    val output: (ShareWithFriendsComponent.Output) -> Unit
) : ShareWithFriendsComponent, ComponentContext by componentContext {

    private val _state = MutableValue(
        ShareWithFriendsComponent.State(
            apkLink = "vpn.run/latest.apk?ref=vasya",
            siteLink = "https://vpn.run/321312",
            tgLink = "https://telegram.org/@runvpn_bot"
        )
    )

    override val state: Value<ShareWithFriendsComponent.State> = _state


    override fun onShowQrCodeClick(link: String) {
        output(ShareWithFriendsComponent.Output.OnShowQrCode(link = link))
    }

    override fun onShareFileClick() {

    }

    override fun onShareLinkClick(link: String) {

    }

    override fun onDismissClicked() {
        output(ShareWithFriendsComponent.Output.OnDismiss)
    }
}
