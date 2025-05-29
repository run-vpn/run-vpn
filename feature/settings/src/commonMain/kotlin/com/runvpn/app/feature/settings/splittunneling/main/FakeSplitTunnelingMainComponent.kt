package com.runvpn.app.feature.settings.splittunneling.main

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.SplitTunnelingApplication
import com.runvpn.app.data.settings.domain.SplitTunnelingMode

class FakeSplitTunnelingMainComponent : SplitTunnelingMainComponent {

    override val state: Value<SplitTunnelingMainComponent.State> = MutableValue(
        SplitTunnelingMainComponent.State(
            ipSplitMode = SplitTunnelingMode.EXCLUDE,
            appsSplitMode = SplitTunnelingMode.EXCLUDE,
            ips = listOf("192.168.0.0.1", "178.90.35.0.10"),
            apps = listOf(
                SplitTunnelingApplication(
                    "com.android.photo",
                    "Google Photo",
                    false
                ),
                SplitTunnelingApplication(
                    "com.jetbrains.youtrack",
                    "YouTrack",
                    false
                ),
            )
        )
    )

    override fun onSplitAppsClick() {

    }

    override fun onSplitIpsClick() {

    }

    override fun onBackClick() {

    }


}
