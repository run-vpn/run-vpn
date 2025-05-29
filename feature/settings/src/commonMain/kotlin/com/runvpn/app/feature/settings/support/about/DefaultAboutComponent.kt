package com.runvpn.app.feature.settings.support.about

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.ClipboardManager
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.models.AppVersion
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage

class DefaultAboutComponent(
    componentContext: ComponentContext,
    appSettingsRepository: AppSettingsRepository,
    deviceInfo: AppVersion,
    private val clipboardManager: ClipboardManager,
    private val messageService: MessageService,
    private val output: (AboutComponent.Output) -> Unit
) : AboutComponent, ComponentContext by componentContext {

    private val _state =
        MutableValue(
            AboutComponent.State(
                aboutInfo = deviceInfo,
                siteUrl = "https://vpn.run",
                ourCompany = "Our Company",
                deviceUuid = appSettingsRepository.deviceUuid
            )
        )

    override val state: Value<AboutComponent.State>
        get() = _state

    override fun onDeviceUuidClick(uuid: String) {
        clipboardManager.copy(uuid)
        messageService.showMessage(AppMessage.CopyToClipboard())
    }


    override fun onBack() {
        output(AboutComponent.Output.OnBack)
    }

}
