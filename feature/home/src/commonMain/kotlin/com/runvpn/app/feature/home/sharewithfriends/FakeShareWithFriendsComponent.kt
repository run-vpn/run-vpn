package com.runvpn.app.feature.home.sharewithfriends

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class FakeShareWithFriendsComponent : ShareWithFriendsComponent {
    override val state: Value<ShareWithFriendsComponent.State>
        get() = MutableValue(
            ShareWithFriendsComponent.State(
                apkLink = "fakeApkLink",
                siteLink = "fakeSiteLink",
                tgLink = "fakeTgLink"
            )
        )

    override fun onShowQrCodeClick(link: String) {
        TODO("Not yet implemented")
    }

    override fun onShareFileClick() {
        TODO("Not yet implemented")
    }

    override fun onShareLinkClick(link: String) {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }
}
