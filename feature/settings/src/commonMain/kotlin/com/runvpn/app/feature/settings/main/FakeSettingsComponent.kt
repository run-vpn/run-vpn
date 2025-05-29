package com.runvpn.app.feature.settings.main


import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.models.update.UpdateStatus
import com.runvpn.app.data.settings.domain.Language
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

class FakeSettingsComponent(
    isUpdateAvailable: Boolean = true
) : MainSettingsComponent {

    override val state: Value<MainSettingsComponent.State> =
        MutableValue(
            MainSettingsComponent.State(
                language = Language(
                    isoCode = "ru",
                    flagIsoCode = "ru",
                    language = "Русский",
                    languageInEnglish = "Russian"
                ),
                updateStatus = if (isUpdateAvailable) {
                    UpdateStatus.Downloading
                } else null,
                updateProgress = if (isUpdateAvailable) 50 else 0

            )
        )
    override val childSlot: Value<ChildSlot<*, SimpleDialogComponent>>
        get() = MutableValue(ChildSlot<Nothing, Nothing>(null))

    override fun onChooseLanguageClick() {
        TODO("Not yet implemented")
    }

    override fun onCommonSettingsClick() {
        TODO("Not yet implemented")
    }

    override fun onConnectionSettingsClick() {
        TODO("Not yet implemented")
    }

    override fun onSupportClick() {
        TODO("Not yet implemented")
    }

    override fun onAboutClick() {
        TODO("Not yet implemented")
    }

    override fun onLanguageChanged(language: Language) {
        TODO("Not yet implemented")
    }

    override fun onRetryDownloadClick(updateInfo: UpdateInfo) {
        TODO("Not yet implemented")
    }

}

