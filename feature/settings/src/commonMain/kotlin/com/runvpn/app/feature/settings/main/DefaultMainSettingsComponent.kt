package com.runvpn.app.feature.settings.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.UpdateRepository
import com.runvpn.app.data.settings.domain.Language
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.feature.settings.createChooseLanguageComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

internal class DefaultMainSettingsComponent(
    componentContext: ComponentContext,
    exceptionHandler: CoroutineExceptionHandler,
    private val componentFactory: DecomposeComponentFactory,
    private val appSettingsRepository: AppSettingsRepository,
    private val downloadFileUseCase: UpdateRepository,
    private val onOutput: (MainSettingsComponent.Output) -> Unit,
) : MainSettingsComponent, ComponentContext by componentContext {

    private val coroutineScope = createCoroutineScope(exceptionHandler)

    private val _state =
        MutableValue(
            MainSettingsComponent.State(
                language = appSettingsRepository.userSelectedLocale,
                updateStatus = null,
                updateProgress = null
            )
        )
    override val state: Value<MainSettingsComponent.State> = _state

    private val dialogNavigation: SlotNavigation<SlotConfig> = SlotNavigation()
    override val childSlot: Value<ChildSlot<*, SimpleDialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = SlotConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createDialogChild
    )

    override fun onChooseLanguageClick() {
        dialogNavigation.activate(SlotConfig.ChooseLanguage)
    }

    init {
        coroutineScope.launch {
            downloadFileUseCase.status.collectLatest {
                _state.value = state.value.copy(updateStatus = it)
            }
        }

        coroutineScope.launch {
            downloadFileUseCase.progress.collectLatest {
                _state.value = state.value.copy(updateProgress = it)
            }
        }
    }

    private fun createDialogChild(
        childConfig: SlotConfig,
        childComponentContext: ComponentContext
    ): SimpleDialogComponent {
        return when (childConfig) {
            SlotConfig.ChooseLanguage -> componentFactory.createChooseLanguageComponent(
                childComponentContext,
                onLanguageSelected = {
                    onLanguageChanged(it)
                },
                onDismissed = { dialogNavigation.dismiss() }
            )
        }
    }

    override fun onCommonSettingsClick() {
        onOutput(MainSettingsComponent.Output.OnCommonSettingsRequested)
    }

    override fun onConnectionSettingsClick() {
        onOutput(MainSettingsComponent.Output.OnConnectionSettingsRequested)
    }

    override fun onSupportClick() {
        onOutput(MainSettingsComponent.Output.OnSupportScreenRequested)
    }

    override fun onAboutClick() {
        onOutput(MainSettingsComponent.Output.OnAboutScreenRequested)
    }

    override fun onLanguageChanged(language: Language) {
        _state.value = state.value.copy(language = language)
        appSettingsRepository.userSelectedLocale = language
    }

    override fun onRetryDownloadClick(updateInfo: UpdateInfo) {
        coroutineScope.launch {
            downloadFileUseCase.downloadUpdate(updateInfo)
        }
    }

    @Serializable
    sealed interface SlotConfig {

        @Serializable
        data object ChooseLanguage : SlotConfig
    }

}
