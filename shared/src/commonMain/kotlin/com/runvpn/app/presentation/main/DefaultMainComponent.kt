package com.runvpn.app.presentation.main

import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.runvpn.app.data.device.domain.UpdateRepository
import com.runvpn.app.data.device.domain.models.update.UpdateStatus
import com.runvpn.app.data.device.domain.usecases.update.GetApplicationInfoUseCase
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.feature.home.HomeComponent
import com.runvpn.app.feature.home.createHomeComponent
import com.runvpn.app.feature.profile.createProfileTabComponent
import com.runvpn.app.feature.servertab.ServerTabComponent
import com.runvpn.app.feature.servertab.createServerTabComponent
import com.runvpn.app.feature.settings.createSettingsComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.dialog.DialogManager
import com.runvpn.app.tea.dialog.RootDialogConfig
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.navigation.RootRouter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

class DefaultMainComponent(
    private val componentContext: ComponentContext,
    private val decomposeComponentFactory: DecomposeComponentFactory,
    private val rootRouter: RootRouter,
    private val dialogManager: DialogManager,
    private val messageService: MessageService,
    private val updateRepository: UpdateRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val getApplicationInfoUseCase: GetApplicationInfoUseCase,
    private val mainComponentOutput: (MainComponent.Output) -> Unit,
) : MainComponent, ComponentContext by componentContext {

    companion object {
        private const val MAIN_CHILD_STACK_KEY = "main_child_stack_key"
        private const val CLICKS_COUNT_TO_DOWNLOAD_RC = 5

        private const val RELEASE_CANDIDATE_CODE = "com.runvpn.app.android.ps.apk.rls"
        private const val RELEASE_CANDIDATE_SOURCE = "rc"

        private val logger = Logger.withTag("DefaultMainComponent")
    }

    private val navigation = StackNavigation<ChildConfig>()

    private val initialTabStack: List<ChildConfig> = listOf(
        ChildConfig.Home,
        ChildConfig.Servers,
        ChildConfig.Profile,
        ChildConfig.Settings
    ).reversed()

    override val activeChild: Value<ChildStack<*, MainComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialStack = { initialTabStack },
        key = MAIN_CHILD_STACK_KEY,
        childFactory = ::createRootChild
    )
    private val _state = MutableValue(MainComponent.State(hasUpdate = false))
    override val state: Value<MainComponent.State>
        get() = _state

    private val backCallback = BackCallback() {
        val currentChild = activeChild.value.active.configuration

        if (currentChild !is ChildConfig.Home) {
            navigateAndTurnOffCallback(ChildConfig.Home)
        }
    }

    private val coroutineScope = createCoroutineScope()

    private var downloadingStateJob: Job? = null
    private var settingsClickCounter = 0

    init {
        backHandler.register(backCallback)
        backCallback.isEnabled = false
        coroutineScope.launch {
            updateRepository.status.collect {
                if (it != null) {
                    _state.value = state.value.copy(hasUpdate = true)
                }
            }
        }

        lifecycle.doOnCreate {
            if (!appSettingsRepository.isPrivacyPolicyAccepted) {
                dialogManager.showDialog(RootDialogConfig.ChooseAppUsageModeConfig(isCancellable = false))
            }
        }
    }

    private fun navigateAndTurnOffCallback(child: ChildConfig) {
        navigation.bringToFront(child)
        backCallback.isEnabled = false
    }

    private fun createRootChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): MainComponent.Child = when (config) {
        ChildConfig.Home -> MainComponent.Child.Home(
            decomposeComponentFactory.createHomeComponent(
                componentContext,
                ::onHomeComponentOutput
            )
        )

        ChildConfig.Profile -> MainComponent.Child.Profile(
            decomposeComponentFactory.createProfileTabComponent(
                componentContext
            )
        )

        ChildConfig.Servers -> MainComponent.Child.Servers(
            decomposeComponentFactory.createServerTabComponent(
                componentContext,
                ::handleServerTabComponentOutput
            )
        )

        ChildConfig.Settings -> MainComponent.Child.Settings(
            decomposeComponentFactory.createSettingsComponent(
                componentContext
            )
        )
    }


    private fun handleServerTabComponentOutput(output: ServerTabComponent.Output) {
        when (output) {
            is ServerTabComponent.Output.OnServerSelected -> {
                navigateAndTurnOffCallback(ChildConfig.Home)
            }

            is ServerTabComponent.Output.OnMyServersScreenRequested -> {
                mainComponentOutput.invoke(MainComponent.Output.OnMyServersScreenRequest)
            }

            is ServerTabComponent.Output.OnAddServerScreenRequested -> {
                mainComponentOutput.invoke(MainComponent.Output.OnAddServerScreenRequest)
            }

            ServerTabComponent.Output.OnPermissionGranted -> {
                startSdk()
            }
        }
    }

    private fun onHomeComponentOutput(output: HomeComponent.Output) {
        when (output) {
            is HomeComponent.Output.OnCurrentServerClicked -> navigateToTab(ChildConfig.Servers)
            HomeComponent.Output.OnPermissionGranted -> {
                startSdk()
            }
        }
    }

    private fun startSdk() {
//        appSettingsRepository.deviceUuid?.let {
//            sdkStart(
//                deviceUuid = it,
//                coroutineScope
//            )
//        }
    }

    private fun navigateToTab(tab: ChildConfig) {
        navigation.bringToFront(tab)
        backCallback.isEnabled = tab !is ChildConfig.Home
    }

    override fun onHomeClicked() {
        navigateToTab(ChildConfig.Home)
    }

    override fun onServersClicked() {
        navigateToTab(ChildConfig.Servers)
    }

    override fun onProfileClicked() {
        navigateToTab(ChildConfig.Profile)
    }

    override fun onSettingsClicked() {
        incrementSettingsClicksAndOpenRc()
        navigateToTab(ChildConfig.Settings)
    }

    private var lastSettingsClickTime = Clock.System.now()
    private fun incrementSettingsClicksAndOpenRc() {
        val clickTime = Clock.System.now()
        if (clickTime.toEpochMilliseconds() - lastSettingsClickTime.toEpochMilliseconds() > 300) {
            settingsClickCounter = 2
            lastSettingsClickTime = clickTime
            return
        }
        lastSettingsClickTime = clickTime

        if (settingsClickCounter < CLICKS_COUNT_TO_DOWNLOAD_RC) {
            settingsClickCounter++
        } else {
            messageService.showMessage(
                AppMessage.Persistent(
                    "Скачать версию Release Candidate?",
                    actionTitle = "Скачать",
                    action = {
                        downloadRcUpdate()
                    }
                )
            )
            settingsClickCounter = 0
        }
    }

    private fun downloadRcUpdate() {
        downloadingStateJob = coroutineScope.launch {
            updateRepository.status.collectLatest { status ->
                when (status) {
                    is UpdateStatus.Downloading -> {
                        messageService.showMessage(
                            AppMessage.Common(
                                "Идет скачивание...",
                                action = {
                                    downloadingStateJob?.cancel()
                                    downloadingStateJob = null
                                }
                            )
                        )
                    }

                    is UpdateStatus.Error -> {
                        messageService.showMessage(
                            AppMessage.Common(
                                "Ошибка скачивания",
                            )
                        )
                    }

                    is UpdateStatus.Success -> {
                        messageService.showMessage(
                            AppMessage.Common(
                                "Обновление скачено",
                                actionTitle = "Установить",
                                action = {
                                    navigateToTab(ChildConfig.Settings)
                                }
                            )
                        )
                    }

                    null -> {}
                }
            }
        }

        coroutineScope.launch(Dispatchers.Default) {
            getApplicationInfoUseCase(
                applicationCode = RELEASE_CANDIDATE_CODE,
                source = RELEASE_CANDIDATE_SOURCE
            ).onSuccess {
                updateRepository.downloadUpdate(it)
            }.onFailure {
                messageService.showMessage(
                    AppMessage.Common(
                        "Ошибка скачивания. Повторить?",
                        actionTitle = "Скачать",
                        action = {
                        }
                    )
                )
            }
        }
    }

    @Serializable
    private sealed interface ChildConfig {
        @Serializable
        data object Home : ChildConfig

        @Serializable
        data object Servers : ChildConfig

        @Serializable
        data object Profile : ChildConfig

        @Serializable
        data object Settings : ChildConfig
    }
}

