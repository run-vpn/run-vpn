package com.runvpn.app.android

import android.app.Application
import android.app.Notification
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import co.touchlab.kermit.Logger
import com.getkeepsafe.relinker.ReLinker
import com.runvpn.app.SharedSDK
import com.runvpn.app.android.ext.isNotificationsAllowed
import com.runvpn.app.android.utils.AndroidUtils
import com.runvpn.app.android.vpn.AppConnectionStatistics
import com.runvpn.app.android.vpn.NotificationHelper
import com.runvpn.app.android.vpn.VpnConnectionFactoryImpl
import com.runvpn.app.android.vpn.xray.MakeXrayConfigFromServerUseCase
import com.runvpn.app.core.common.presentation.Notification.NOTIFICATION_ID
import com.runvpn.app.core.common.presentation.Notification.NOTIFICATION_ID_OFF
import com.runvpn.app.data.connection.ConnectionStatisticsManager
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionFactory
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.connection.domain.ConnectToVpnUseCase
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import com.runvpn.app.data.settings.models.AppVersion
import com.runvpn.app.sdkSelectChannel
import com.runvpn.app.sdkStart
import com.runvpn.app.tea.utils.asStateFlow
import de.blinkt.openvpn.OpenVpnNotificationBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.strongswan.android.logic.StrongSwanApplication


class MyApp : Application() {

    companion object {
        private val logger: Logger = Logger.withTag("MyApp")
    }

    val vpnConnectionManager: VpnConnectionManager
        get() = SharedSDK.koinApplication.koin.get()


    private val appSettingsRepository: AppSettingsRepository
        get() = SharedSDK.koinApplication.koin.get()

    private val settingsRepository: UserSettingsRepository
        get() = SharedSDK.koinApplication.koin.get()

    private val serverRepository: ServersRepository
        get() = SharedSDK.koinApplication.koin.get()

    private val connectToVpnService: ConnectToVpnUseCase
        get() = SharedSDK.koinApplication.koin.get()

    private val connectionManager: VpnConnectionManager
        get() = SharedSDK.koinApplication.koin.get()


    val notificationHelper: NotificationHelper by lazy {
        NotificationHelper(this)
    }

    val openVpnNotificationBuilder: OpenVpnNotificationBuilder by lazy {
        object : OpenVpnNotificationBuilder {
            override val notificationId: Int = NOTIFICATION_ID

            override fun buildNotification(): Notification {
                return notificationHelper.buildNotification(
                    vpnConnectionManager.connectionStatus.value,
                    ""
                )
            }
        }
    }


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            checkConnectOnNetworkEnabled()
        }
    }


    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        notificationHelper.createNotificationChannels()

        initRunVpnSdk()
        subscribeToNetworkStatistics()
        initNetworkListener()
        initStrongSwan()
        initSdk()
    }

    private fun initRunVpnSdk() {
        val currentLocaleCode = resources.configuration.locales[0]
        SharedSDK.initializeSdk(
            backendUrl = BuildConfig.BACKEND_URL,
            environment = BuildConfig.ENVIRONMENT,
            localeCode = currentLocaleCode.language,
            deviceInfo = AndroidUtils.getDeviceInfo(this@MyApp),
            appVersion = AppVersion(
                versionCode = BuildConfig.VERSION_CODE,
                versionName = BuildConfig.VERSION_NAME,
                platform = "Android"
            ),
            isDebug = BuildConfig.DEBUG,
            appInternalDirectory = cacheDir.toString(),
            connectionStatisticsManager = AppConnectionStatistics(),
            analyticsProviders = emptyList(),
            reportProviders = emptyList(),
            additionalModules = mutableListOf(
                module {
                    single<Context> { this@MyApp }
                    single<VpnConnectionFactory> { VpnConnectionFactoryImpl(get(), get()) }
//                    single {
//                        SdkNotificationChannelInfo(
//                            iconResId = R.drawable.ic_notification_icon,
//                            ntfTitle = getString(R.string.vpn_status_disconnected),
//                            channelName = getString(R.string.vpn_status_disconnected),
//                            channelDescription = getString(R.string.ntf_disconnected_channel_desc),
//                            channelId = ChannelIdDisconnected,
//                            notificationId = NOTIFICATION_ID_OFF,
//                        )
//                    }
                    factory { MakeXrayConfigFromServerUseCase(get()) }
                }
            ).apply {
//                addAll(sdkWrapperModules)
            }
        )
    }

    private fun initNetworkListener() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    private fun checkConnectOnNetworkEnabled() {
        if (!this.isNotificationsAllowed()) return
        if (connectionManager.connectionStatus.value.isDisconnected() && settingsRepository.connectOnNetworkEnabled) {
            coroutineScope.launch {
                delay(2000L)
                serverRepository.currentServer.collectLatest { currentServer ->
                    currentServer?.let {
                        connectToVpnService(it)
                    }
                }
            }
        }
    }

    private fun subscribeToNetworkStatistics() {
        val nsm = SharedSDK.koinApplication.koin.get<ConnectionStatisticsManager>()
        coroutineScope.launch {
            combine(
                nsm.stats.asStateFlow(),
                vpnConnectionManager.connectionStatus
            ) { stats, status ->
                Logger.i("NotificationChange. Update Notification $status")
//                if (status !is ConnectionStatus.Disconnected) {
//                    delay(500)
                    notificationHelper.updateNotification(status, stats.wrappedValue)
//                }
            }.collect()
        }
    }

    private fun initSdk() {
        coroutineScope.launch {
            vpnConnectionManager.connectionStatus.collectLatest {
                Logger.i("NotificationChange. Update SDK $it")
                if (this@MyApp.baseContext.isNotificationsAllowed()) {
                    appSettingsRepository.deviceUuid?.let { deviceUuid ->
                        sdkStart(deviceUuid, coroutineScope)
                        if (it is ConnectionStatus.Disconnected) {
                            Logger.i("Build Notification sdk OFF")
                            sdkSelectChannel(NOTIFICATION_ID_OFF)
                        } else {
                            Logger.i("Build Notification sdk ON")
                            sdkSelectChannel(NOTIFICATION_ID)
                        }
                    }
                }
            }
        }
    }

    private fun initStrongSwan() {
        try {
            ReLinker.loadLibrary(this, "androidbridge")
            ReLinker.loadLibrary(this, "strongswan")

            ReLinker.loadLibrary(this, "tncif")
            ReLinker.loadLibrary(this, "tnccs")
            ReLinker.loadLibrary(this, "imcv")

            ReLinker.loadLibrary(this, "charon")
            ReLinker.loadLibrary(this, "ipsec")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        StrongSwanApplication.setContext(applicationContext)
    }

}
