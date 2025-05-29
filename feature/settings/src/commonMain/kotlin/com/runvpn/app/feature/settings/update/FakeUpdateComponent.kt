package com.runvpn.app.feature.settings.update

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.models.update.UpdateStatus

class FakeUpdateComponent(private val selfUpdateAllowed: Boolean = false) : UpdateComponent {
    override val state: Value<UpdateComponent.State>
        get() = MutableValue(
            UpdateComponent.State(
                "FakeUpdateMessage",
                updateStatus = UpdateStatus.Downloading,
                updateProgress = 50,
                selfUpdateAllowed = selfUpdateAllowed,
            )
        )

    override fun retryClick(updateInfo: UpdateInfo) {
        TODO("Not yet implemented")
    }

    override fun onOpenLinkClick(link: String) {
        TODO("Not yet implemented")
    }

    override fun onUpdateClick() {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }


}

