package com.runvpn.app.feature.settings.splittunneling.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnResume
import com.runvpn.app.data.settings.domain.SplitTunnelingMode
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage

internal class DefaultSplitTunnelingMainComponent(
    componentContext: ComponentContext,
    private val appSettingsRepository: AppSettingsRepository,
    private val messageService: MessageService,
    private val output: (SplitTunnelingMainComponent.Output) -> Unit
) : SplitTunnelingMainComponent, ComponentContext by componentContext {

    private val _state: MutableValue<SplitTunnelingMainComponent.State> = MutableValue(
        SplitTunnelingMainComponent.State(
            ipSplitMode = SplitTunnelingMode.EXCLUDE,
            appsSplitMode = SplitTunnelingMode.EXCLUDE,
            ips = listOf(),
            apps = listOf()
        )
    )
    override val state: Value<SplitTunnelingMainComponent.State> = _state

    private val coroutineScope = createCoroutineScope()

    init {
        lifecycle.doOnResume {
            _state.value = _state.value.copy(
                apps = appSettingsRepository.excludedPackageIds
            )
        }
    }

    override fun onSplitAppsClick() {
        output(SplitTunnelingMainComponent.Output.OnAppsScreenRequested)
    }

    override fun onSplitIpsClick() {
        messageService.showMessage(AppMessage.NotImplemented())
//        output(SplitTunnelingMainComponent.Output.OnIpsScreenRequest)
    }

    override fun onBackClick() {
        output(SplitTunnelingMainComponent.Output.OnBack)
    }
}
