package com.runvpn.app.presentation.root

import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.backStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.runvpn.app.SharedSDK
import com.runvpn.app.core.exceptions.NeedUpdateException
import com.runvpn.app.core.exceptions.UnauthorizedException
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.device.domain.UpdateRepository
import com.runvpn.app.data.device.domain.usecases.device.CheckDeviceRegisterUseCase
import com.runvpn.app.data.device.domain.usecases.device.RegisterDeviceUseCase
import com.runvpn.app.data.device.domain.usecases.update.CheckAppUpdateAvailableUseCase
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.settings.domain.AppTheme
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import com.runvpn.app.data.settings.models.AppVersion
import com.runvpn.app.domain.ClearAppCacheUseCase
import com.runvpn.app.domain.InitApplicationDataUseCase
import com.runvpn.app.domain.LogOutUserUseCase
import com.runvpn.app.domain.SwitchTokensUseCase
import com.runvpn.app.feature.authorization.AuthorizationComponent
import com.runvpn.app.feature.authorization.createAuthorizationComponent
import com.runvpn.app.feature.home.createTrafficOverComponent
import com.runvpn.app.feature.home.createWelcomeComponent
import com.runvpn.app.feature.home.welcome.WelcomeComponent
import com.runvpn.app.feature.myservers.MyServersComponent
import com.runvpn.app.feature.myservers.createMyServersComponent
import com.runvpn.app.feature.serveradd.AddServerComponent
import com.runvpn.app.feature.serveradd.createServerAddComponent
import com.runvpn.app.feature.settings.Constants.SELF_UPDATE_ALLOWED_SOURCE
import com.runvpn.app.feature.settings.createAboutComponent
import com.runvpn.app.feature.settings.createChooseDnsServerComponent
import com.runvpn.app.feature.settings.createSplitTunnelingComponent
import com.runvpn.app.feature.settings.createSupportComponent
import com.runvpn.app.feature.settings.support.about.AboutComponent
import com.runvpn.app.feature.settings.support.createFaqComponent
import com.runvpn.app.feature.settings.support.createFeedbackComponent
import com.runvpn.app.feature.settings.support.createReportProblemComponent
import com.runvpn.app.feature.settings.support.createSupportComponent
import com.runvpn.app.feature.settings.support.createUpdateComponent
import com.runvpn.app.feature.subscription.buy.BuySubscriptionComponent
import com.runvpn.app.feature.subscription.createBalanceRefillComponent
import com.runvpn.app.feature.subscription.createBuySubscriptionComponent
import com.runvpn.app.feature.subscription.createSubscriptionRootComponent
import com.runvpn.app.feature.trafficmodule.TrafficModuleComponent
import com.runvpn.app.feature.trafficmodule.createTrafficModuleComponent
import com.runvpn.app.presentation.main.MainComponent
import com.runvpn.app.presentation.main.createMainComponent
import com.runvpn.app.tea.LogoutHandler
import com.runvpn.app.tea.SyncDataRequest
import com.runvpn.app.tea.createCommonDialog
import com.runvpn.app.tea.decompose.BaseComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent
import com.runvpn.app.tea.dialog.DialogManager
import com.runvpn.app.tea.dialog.RootDialogConfig
import com.runvpn.app.tea.message.createMessageComponent
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.presentation.MessageComponent
import com.runvpn.app.tea.navigation.RootChildConfig
import com.runvpn.app.tea.navigation.RootRouter
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class DefaultRootComponent(
    componentContext: ComponentContext,
    fileDir: String,
    deviceInfo: DeviceInfo,
    settingsRepository: UserSettingsRepository,
    appSettingsRepository: AppSettingsRepository,
    deviceRepository: DeviceRepository,
    vpnConnectionManager: VpnConnectionManager,
    serversRepository: ServersRepository,
    httpClient: HttpClient,
    appVersion: AppVersion,
    updateRepository: UpdateRepository,
    checkAppUpdateAvailableUseCase: CheckAppUpdateAvailableUseCase,
    checkDeviceRegisterUseCase: CheckDeviceRegisterUseCase,
    registerDeviceUseCase: RegisterDeviceUseCase,
    initApplicationDataUseCase: InitApplicationDataUseCase,
    clearAppCacheUseCase: ClearAppCacheUseCase,
    logOutUserUseCase: LogOutUserUseCase,
    switchTokensUseCase: SwitchTokensUseCase,
    messageService: MessageService,
    private val componentFactory: DecomposeComponentFactory,
) : BaseComponent<RootFeature.State, RootFeature.Message, RootFeature.Dependencies>(
    initialState = RootFeature.Logic.initialUpdate,
    restore = RootFeature.Logic::restore,
    update = RootFeature.Logic::update,
    dependencies = RootFeature.Dependencies(
        deviceInfo = deviceInfo,
        deviceRepository = deviceRepository,
        appSettingsRepository = appSettingsRepository,
        httpClient = httpClient,
        fileDir = fileDir,
        appVersion = appVersion,
        updateRepository = updateRepository,
        checkAppUpdateAvailableUseCase = checkAppUpdateAvailableUseCase,
        vpnConnectionManager = vpnConnectionManager,
        registerDeviceUseCase = registerDeviceUseCase,
        checkDeviceRegisterUseCase = checkDeviceRegisterUseCase,
        initApplicationDataUseCase = initApplicationDataUseCase,
        serversRepository = serversRepository,
        logOutUserUseCase = logOutUserUseCase,
        messageService = messageService
    ),
    enableLogs = true,
), RootComponent, ComponentContext by componentContext {

    companion object {
        private const val ROOT_CHILD_STACK_KEY = "root_child_stack_key"
    }

    private val rootNavigation = StackNavigation<RootChildConfig>()

    private val dialogNavigation = SlotNavigation<RootDialogConfig>()

    override val dialog: Value<ChildSlot<*, SimpleDialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = RootDialogConfig.serializer(),
        // persistent = false, // Disable navigation state saving, if needed
        handleBackButton = false, // Close the dialog on back button press
        childFactory = ::createDialogChild
    )

    private val _appTheme = MutableValue(AppTheme.SYSTEM)
    override val appTheme: Value<AppTheme> = _appTheme

    private val _oneTimeEvent: Channel<RootComponent.OneTimeEvent> = Channel()
    override val oneTimeEvent: Flow<RootComponent.OneTimeEvent> = _oneTimeEvent.receiveAsFlow()

    override val messageComponent: MessageComponent = componentFactory.createMessageComponent(
        childContext("messageComponent")
    )

    override var coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e("CoroutineExceptionHandler", throwable)

        when (throwable) {
//            is NeedUpdateException -> {
//                Logger.i("Throwable!" + throwable.message)
//                CoroutineScope(Dispatchers.Main).launch {
//                    dialogNavigation.activate(RootDialogConfig.ForceUpdateConfig)
//                }
//                if (deviceInfo.source == SELF_UPDATE_ALLOWED_SOURCE) {
//                    dispatch(RootFeature.Message.UpdateRequested(deviceInfo.application.code))
//                }
//            }
//
//            is UnauthorizedException -> {
//                val appToken = switchTokensUseCase()
//                if (appToken.isNullOrEmpty()) {
//                    dispatch(RootFeature.Message.Init)
//                } else {
//                    dispatch(RootFeature.Message.SyncServers)
//                }
//            }

            else -> {
//                appSettingsRepository.setIsDomainReachable(false)
//                dispatch(RootFeature.Message.ShowMessage(throwable.message ?: "Some Error Occured"))
            }
        }
    }

    private val logoutHandler = object : LogoutHandler {
        override suspend fun logout() {
            clearAppCacheUseCase()
            logOutUserUseCase()
            syncDataRequest.syncServers()
        }
    }

    private val syncDataRequest = object : SyncDataRequest {

        override fun syncServers() {
            dispatch(RootFeature.Message.SyncServers)
        }
    }

    private val rootRouter: RootRouter = object : RootRouter {
        override fun open(config: RootChildConfig) {
            rootNavigation.push(config)
        }

        override fun replace(config: RootChildConfig) {
            rootNavigation.replaceCurrent(config)
        }

        override fun pop() {
            rootNavigation.pop()
        }
    }

    private val dialogManager: DialogManager = object : DialogManager {
        override fun showDialog(dialogConfig: RootDialogConfig) {
            dialogNavigation.activate(dialogConfig)
        }

        override fun dismiss() {
            dialogNavigation.dismiss()
        }
    }

    init {
        SharedSDK.koinApplication.koin.apply {
            declare<LogoutHandler>(logoutHandler)
            declare<SyncDataRequest>(syncDataRequest)
            declare(coroutineExceptionHandler)
            declare(rootRouter)
            declare(dialogManager)
        }
    }

    override val childStack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = rootNavigation,
        serializer = RootChildConfig.serializer(),
        initialConfiguration = RootChildConfig.Main,
        key = ROOT_CHILD_STACK_KEY,
        childFactory = ::createRootChild,
        handleBackButton = true
    )

    init {
        _appTheme.value = settingsRepository.theme
        settingsRepository.setAppThemeListener {
            _appTheme.value = it
        }
        dispatch(RootFeature.Message.Init)

        lifecycle.doOnCreate {
            if (deviceInfo.source == SELF_UPDATE_ALLOWED_SOURCE) {
                dispatch(RootFeature.Message.UpdateRequested(deviceInfo.application.code))
            }
        }
    }

    private fun createRootChild(
        config: RootChildConfig, componentContext: ComponentContext
    ): RootComponent.Child = when (config) {

        RootChildConfig.Main -> RootComponent.Child.Main(
            componentFactory.createMainComponent(componentContext, ::handleMainOutput)
        )

        RootChildConfig.About -> RootComponent.Child.About(
            componentFactory.createAboutComponent(componentContext, ::handleAboutOutput)
        )

        RootChildConfig.Authorization -> RootComponent.Child.Authorization(
            componentFactory.createAuthorizationComponent(
                componentContext, ::onAuthComponentOutput
            )
        )

        RootChildConfig.SubscriptionRoot -> RootComponent.Child.SubscriptionRoot(
            componentFactory.createSubscriptionRootComponent(componentContext)
        )

        RootChildConfig.BuySubscriptionConfig -> RootComponent.Child.BuySubscription(
            componentFactory.createBuySubscriptionComponent(
                componentContext,
                ::handleBuySubscriptionOutput
            )
        )

        is RootChildConfig.AddServer -> RootComponent.Child.ServerAdd(
            componentFactory.createServerAddComponent(
                componentContext,
                serverToEdit = config.serverToEdit,
                ::handleServerAddOutput
            )
        )

        RootChildConfig.MyServers -> RootComponent.Child.MyServers(
            componentFactory.createMyServersComponent(
                componentContext, ::handleMyServersOutput
            )
        )

        RootChildConfig.SplitTunneling -> RootComponent.Child.SplitTunneling(
            componentFactory.createSplitTunnelingComponent(componentContext)
        )

        RootChildConfig.SupportMain -> RootComponent.Child.SupportMain(
            componentFactory.createSupportComponent(componentContext)
        )

        is RootChildConfig.Faq -> RootComponent.Child.Faq(
            componentFactory.createFaqComponent(componentContext)
        )

        is RootChildConfig.ReportProblem -> RootComponent.Child.ReportProblem(
            componentFactory.createReportProblemComponent(componentContext)
        )

        is RootChildConfig.Feedback -> RootComponent.Child.FeedBack(
            componentFactory.createFeedbackComponent(componentContext)
        )

        is RootChildConfig.PrivacyPolicy -> RootComponent.Child.PrivacyPolicy(
            componentFactory.createWelcomeComponent(
                componentContext,
                config.hasBackStack,
                ::handlePrivacyPolicyOutput
            )
        )

        RootChildConfig.ChooseDnsServer -> RootComponent.Child.ChooseDnsServer(
            componentFactory.createChooseDnsServerComponent(componentContext)
        )

        RootChildConfig.TrafficModule -> RootComponent.Child.TrafficModule(
            componentFactory.createTrafficModuleComponent(
                componentContext, ::onTrafficModuleOutput
            )
        )

        RootChildConfig.TrafficModuleLogs -> RootComponent.Child.TrafficModuleLogs(
            componentFactory.createTrafficModuleComponent(
                componentContext, ::onTrafficModuleOutput
            )
        )

        is RootChildConfig.BalanceRefillConfig -> RootComponent.Child.BalanceRefill(
            componentFactory.createBalanceRefillComponent(
                componentContext,
                config.cost
            )
        )
    }

    private fun onTrafficModuleOutput(output: TrafficModuleComponent.Output) {
        when (output) {
            is TrafficModuleComponent.Output.LookLogsRequested -> {
                rootNavigation.push(RootChildConfig.TrafficModuleLogs)
            }

            TrafficModuleComponent.Output.OnBack -> {
                rootNavigation.pop()
            }
        }
    }

    private fun handlePrivacyPolicyOutput(output: WelcomeComponent.Output) {
        when (output) {
            is WelcomeComponent.Output.OnConfirm -> {
                if (output.hasBackStack) {
                    rootNavigation.pop()
                } else {
                    rootNavigation.replaceCurrent(RootChildConfig.Main)
                }
            }
        }
    }

    override fun onPermissionGrantedResult(result: Boolean) {
//        dispatch(RootFeature.Message.OnNotificationPermissionChecked(result))
    }

    private fun createDialogChild(
        childConfig: RootDialogConfig,
        childComponentContext: ComponentContext
    ): SimpleDialogComponent {
        return when (childConfig) {
            is RootDialogConfig.ForceUpdateConfig -> componentFactory.createUpdateComponent(
                childComponentContext,
                message = "",
                onDismiss = { dialogNavigation.dismiss() }
            )

            is RootDialogConfig.ChooseAppUsageModeConfig -> componentFactory.createTrafficOverComponent(
                childComponentContext,
                isCancellable = childConfig.isCancellable,
                onDismiss = { dialogNavigation.dismiss() }
            )

            is RootDialogConfig.Support -> componentFactory.createSupportComponent(
                childComponentContext,
                onDismissed = { dialogNavigation.dismiss() }
            )

            is RootDialogConfig.AlertDialog -> componentFactory.createCommonDialog(
                childComponentContext,
                dialogMessage = childConfig.dialogMessage,
                onConfirm = childConfig.onConfirm,
                onDismiss = childConfig.onDismiss
            )
        }
    }

    private fun handleAboutOutput(output: AboutComponent.Output) {
        when (output) {
            is AboutComponent.Output.OnBack -> {
                rootNavigation.pop()
            }
        }
    }

    private fun handleServerAddOutput(output: AddServerComponent.Output) {
        when (output) {
            is AddServerComponent.Output.OnBack -> {
                rootNavigation.pop()
            }

            is AddServerComponent.Output.OnServerAdded -> {
                val isMyServersInBackStack = childStack.backStack.firstOrNull {
                    it.configuration is RootChildConfig.MyServers
                } != null

                if (!isMyServersInBackStack) {
                    rootNavigation.replaceCurrent(RootChildConfig.MyServers)
                } else {
                    rootNavigation.pop()
                }
            }
        }
    }

    private fun handleMyServersOutput(output: MyServersComponent.Output) {
        when (output) {
            is MyServersComponent.Output.OnAddServerScreenRequested -> {
                rootNavigation.push(RootChildConfig.AddServer(output.serverToEdit?.uuid))
            }

            is MyServersComponent.Output.OnBack -> {
                rootNavigation.pop()
            }
        }
    }

    private fun handleMainOutput(output: MainComponent.Output) {
        when (output) {
            is MainComponent.Output.OnAuthScreenRequest -> {
                rootNavigation.push(RootChildConfig.Authorization)
            }

            is MainComponent.Output.OnAddServerScreenRequest -> {
                rootNavigation.push(RootChildConfig.AddServer(null))
            }

            is MainComponent.Output.OnMyServersScreenRequest -> {
                rootNavigation.push(RootChildConfig.MyServers)
            }

            is MainComponent.Output.OnAboutScreenRequest -> {
                rootNavigation.push(RootChildConfig.About)
            }
        }
    }

    private fun onAuthComponentOutput(output: AuthorizationComponent.Output) {
        when (output) {
            is AuthorizationComponent.Output.OnAuthorizationSuccess -> {
                rootNavigation.pop()
                syncDataRequest.syncServers()
            }

            is AuthorizationComponent.Output.OnCancel -> rootNavigation.pop()
        }
    }

    private fun handleBuySubscriptionOutput(output: BuySubscriptionComponent.Output) {
        when (output) {
            is BuySubscriptionComponent.Output.OnBack -> rootNavigation.pop()

            is BuySubscriptionComponent.Output.OnRefillBalanceRequested ->
                rootRouter.open(RootChildConfig.BalanceRefillConfig(output.cost))
        }
    }


}

