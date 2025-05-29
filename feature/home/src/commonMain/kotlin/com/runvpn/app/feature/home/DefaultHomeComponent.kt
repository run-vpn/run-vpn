package com.runvpn.app.feature.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnResume
import com.runvpn.app.core.analytics.reports.ReportService
import com.runvpn.app.core.common.PingHelper
import com.runvpn.app.core.common.VibratorManager
import com.runvpn.app.data.common.domain.usecases.CheckReviewRequiredUseCase
import com.runvpn.app.data.connection.ConnectionStatisticsManager
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.connection.domain.ConnectToNextServerUseCase
import com.runvpn.app.data.connection.domain.ConnectToVpnUseCase
import com.runvpn.app.data.connection.domain.ConnectionStatistics
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.device.domain.UpdateRepository
import com.runvpn.app.data.device.domain.usecases.device.CheckDeviceRegisterUseCase
import com.runvpn.app.data.device.domain.usecases.device.RegisterDeviceUseCase
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.domain.usecases.GetSuggestedServersUseCase
import com.runvpn.app.data.servers.domain.usecases.SetCurrentServerUseCase
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import com.runvpn.app.data.settings.models.AppVersion
import com.runvpn.app.feature.common.createConnectionErrorComponent
import com.runvpn.app.feature.common.createShareQrCodeComponent
import com.runvpn.app.feature.common.dialogs.DialogComponent
import com.runvpn.app.feature.home.sharewithfriends.ShareWithFriendsComponent
import com.runvpn.app.tea.decompose.BaseComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.utils.Timer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class DefaultHomeComponent(
    componentContext: ComponentContext,
    serversRepository: ServersRepository,
    deviceRepository: DeviceRepository,
    appSettingsRepository: AppSettingsRepository,
    userSettingsRepository: UserSettingsRepository,
    appVersion: AppVersion,
    updateRepository: UpdateRepository,
    connectionStatisticsManager: ConnectionStatisticsManager,
    private val componentFactory: DecomposeComponentFactory,
    vpnConnectionManager: VpnConnectionManager,
    vibratorManager: VibratorManager,
    setCurrentServerUseCase: SetCurrentServerUseCase,
    getSuggestedServersUseCase: GetSuggestedServersUseCase,
    connectToNextServerUseCase: ConnectToNextServerUseCase,
    connectToVpnUseCase: ConnectToVpnUseCase,
    private val reportService: ReportService,
    checkDeviceRegisterUseCase: CheckDeviceRegisterUseCase,
    registerDeviceUseCase: RegisterDeviceUseCase,
    checkReviewRequiredUseCase: CheckReviewRequiredUseCase,
    deviceInfo: DeviceInfo,
    pingHelper: PingHelper,
    json: Json,
    private val coroutineHandler: CoroutineExceptionHandler,
    private val onOutput: (HomeComponent.Output) -> Unit
) : BaseComponent<HomeFeature.State, HomeFeature.Message, HomeFeature.Dependencies>(
    initialState = HomeFeature.Logic.initialUpdate,
    restore = HomeFeature.Logic::restore,
    update = HomeFeature.Logic::update,
    dependencies = HomeFeature.Dependencies(
        serversRepository = serversRepository,
        deviceRepository = deviceRepository,
        appSettingsRepository = appSettingsRepository,
        userSettingsRepository = userSettingsRepository,
        appVersion = appVersion,
        updateRepository = updateRepository,
        onCurrentServerClicked = {
            onOutput(HomeComponent.Output.OnCurrentServerClicked)
        },
        timer = Timer.stopwatch(),
        connectionStatisticsManager = connectionStatisticsManager,
        vpnConnectionManager = vpnConnectionManager,
        vibratorManager = vibratorManager,
        setCurrentServerUseCase = setCurrentServerUseCase,
        getSuggestedServersUseCase = getSuggestedServersUseCase,
        connectToVpnUseCase = connectToVpnUseCase,
        connectToNextServerUseCase = connectToNextServerUseCase,
        reportService = reportService,
        json = json,
        checkDeviceRegisterUseCase = checkDeviceRegisterUseCase,
        registerDeviceUseCase = registerDeviceUseCase,
        deviceInfo = deviceInfo,
        pingHelper = pingHelper,
        pingJobHandler = PingJobHandler(pingHelper),
        checkReviewRequiredUseCase = checkReviewRequiredUseCase
    ),
    enableLogs = true
), HomeComponent, ComponentContext by componentContext {

    private val dialogNavigation = SlotNavigation<DialogConfig>()

    private var isNeedShowStatsDialog = true

    override val coroutineExceptionHandler: CoroutineExceptionHandler = coroutineHandler

    override val reviewDialog: Value<ChildSlot<*, DialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = DialogConfig.serializer(),
        handleBackButton = false
    ) { configuration, componentContext ->
        val server = state.value.currentServer

        checkNotNull(server) { "Current Server must not be null before dialog will appear" }

        when (configuration) {
            is DialogConfig.ReviewDialog -> {
                return@childSlot componentFactory.createConnectionReviewComponent(
                    componentContext,
                    configuration.connectionStats,
                    server,
                    dialogNavigation::dismiss
                )
            }

            is DialogConfig.ShareWithFriendsDialog -> {
                return@childSlot componentFactory.createShareWithFriendsComponent(
                    componentContext,
                    ::handleShareWithFriendOutput
                )
            }

            is DialogConfig.ShareQrCodeDialog -> {
                return@childSlot componentFactory.createShareQrCodeComponent(
                    componentContext,
                    configuration.link,
                    dialogNavigation::dismiss
                )
            }

            is DialogConfig.ConnectionErrorDialog -> {
                return@childSlot componentFactory.createConnectionErrorComponent(
                    componentContext,
                    dialogNavigation::dismiss
                )
            }
        }

    }

    init {
        dispatch(HomeFeature.Message.Init)

        lifecycle.doOnDestroy {
            dispatch(HomeFeature.Message.OnDestroy)
        }

        lifecycle.doOnResume {
            dispatch(HomeFeature.Message.OnResume)
        }
    }

    override fun handleUpdateMessage(message: HomeFeature.Message) {
        when (message) {
            is HomeFeature.Message.CheckReviewRequiredResponse -> {
                if (message.need) {
                    showConnectionReviewDialog(message.stats)
                }
            }

            else -> super.handleUpdateMessage(message)
        }
    }

    private fun showConnectionReviewDialog(stats: ConnectionStatistics) {
        dialogNavigation.activate(DialogConfig.ReviewDialog(stats))
    }

    override fun onServerClicked(server: Server) {
        dispatch(HomeFeature.Message.OnFavouriteServerClick(server))
    }

    override fun onCurrentServerClick() {
        dispatch(HomeFeature.Message.OnCurrentServerClick)
    }

    override fun onVpnButtonClick() {//remove?

    }

    override fun onShopClick() {

    }

    override fun onSubscribeInfoClick() {

    }

    override fun onConnectClick() {
        dispatch(HomeFeature.Message.OnConnectClicked)
    }

    override fun onDisconnectClick() {
        // isNeedShowStatsDialog = true -- disabled while backend don't send to us special flag
        dispatch(HomeFeature.Message.OnDisconnectClicked)
    }

    override fun onPermissionsGranted() {
        onOutput(HomeComponent.Output.OnPermissionGranted)
    }

    override fun offlineElementClicked() {
        dialogNavigation.activate(DialogConfig.ConnectionErrorDialog)
    }

    private fun handleShareWithFriendOutput(output: ShareWithFriendsComponent.Output) {
        when (output) {
            is ShareWithFriendsComponent.Output.OnDismiss -> dialogNavigation.dismiss()
            is ShareWithFriendsComponent.Output.OnShowQrCode ->
                dialogNavigation.activate(DialogConfig.ShareQrCodeDialog(output.link))
        }
    }

    @Serializable
    sealed interface DialogConfig {
        @Serializable
        data class ReviewDialog(
            @Contextual
            val connectionStats: ConnectionStatistics
        ) : DialogConfig

        /** Activate this Config in dialogNavigation, to show Share BottomSheet*/
        @Serializable
        data object ShareWithFriendsDialog : DialogConfig

        @Serializable
        data class ShareQrCodeDialog(val link: String) : DialogConfig

        @Serializable
        data object ConnectionErrorDialog : DialogConfig
    }
}
