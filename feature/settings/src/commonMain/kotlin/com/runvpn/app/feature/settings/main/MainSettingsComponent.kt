package com.runvpn.app.feature.settings.main

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.models.update.UpdateStatus
import com.runvpn.app.data.settings.domain.Language
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface MainSettingsComponent {

    data class State(
        val language: Language?,
        val updateStatus: UpdateStatus?,
        val updateProgress: Long?
    )

    val state: Value<State>

    val childSlot: Value<ChildSlot<*, SimpleDialogComponent>>

    fun onChooseLanguageClick()
    fun onCommonSettingsClick()
    fun onConnectionSettingsClick()
    fun onSupportClick()
    fun onAboutClick()

    fun onLanguageChanged(language: Language)
    fun onRetryDownloadClick(updateInfo: UpdateInfo)

    sealed interface Output {
        data object OnCommonSettingsRequested : Output
        data object OnConnectionSettingsRequested : Output
        data object OnSupportScreenRequested : Output
        data object OnAboutScreenRequested : Output
    }
}
