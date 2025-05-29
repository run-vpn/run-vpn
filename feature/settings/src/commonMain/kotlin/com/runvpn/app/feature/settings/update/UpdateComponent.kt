package com.runvpn.app.feature.settings.update

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.models.update.UpdateStatus
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface UpdateComponent : SimpleDialogComponent {
    data class State(
        val updateMessage: String,
        val updateStatus: UpdateStatus?,
        val updateProgress: Long?,
        val selfUpdateAllowed: Boolean
    )

    val state: Value<State>


    fun retryClick(updateInfo: UpdateInfo)
    fun onUpdateClick()

    fun onOpenLinkClick(link: String)

}
