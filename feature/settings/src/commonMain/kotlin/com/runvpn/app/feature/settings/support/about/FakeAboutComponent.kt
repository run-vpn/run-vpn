package com.runvpn.app.feature.settings.support.about

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.models.AppVersion

class FakeAboutComponent: AboutComponent {
    override val state: Value<AboutComponent.State>
        get() = MutableValue(
            AboutComponent.State(
                AppVersion(versionCode = 1, versionName = "1.0.7"),
                siteUrl = "https://vpn.run", ourCompany = "Our Company",
                deviceUuid = "some device uuid"
            )
        )

    override fun onDeviceUuidClick(uuid: String) {
        TODO("Not yet implemented")
    }

    override fun onBack() {
        TODO("Not yet implemented")
    }
}
