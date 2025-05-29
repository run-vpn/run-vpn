package com.runvpn.app.android.vpn.xray

import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.LocalSocket
import android.net.LocalSocketAddress
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.ServiceCompat
import co.touchlab.kermit.Logger
import com.runvpn.app.SharedSDK
import com.runvpn.app.android.BuildConfig
import com.runvpn.app.android.MyApp
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.getParcelableExtraCompat
import com.runvpn.app.android.utils.IPRange
import com.runvpn.app.android.utils.IPRangeSet
import com.runvpn.app.android.utils.NetworkUtils
import com.runvpn.app.android.utils.VpnConstants
import com.runvpn.app.android.utils.VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_4
import com.runvpn.app.android.utils.VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_6
import com.runvpn.app.android.utils.VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_6_NO_LAN
import com.runvpn.app.android.vpn.NotificationHelper
import com.runvpn.app.core.common.presentation.Notification.NOTIFICATION_ID
import com.runvpn.app.data.common.models.toVpnServer
import com.runvpn.app.data.connection.AppPackageId
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.servers.domain.usecases.SetCurrentServerUseCase
import com.runvpn.app.data.servers.utils.CustomConfigFields
import com.runvpn.app.tea.utils.Timer
import go.Seq
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vpn.CallbackSet
import vpn.Vpn
import vpn.XrayPoint
import java.io.File
import java.lang.ref.WeakReference
import java.net.UnknownHostException

class XRayService : VpnService() {

    companion object {
        const val ACTION_CONNECT = "action_connect" // Action for this service for connect to VPN
        const val ACTION_DISCONNECT = "action_disconnect" // Action to disconnect
        const val ACTION_PAUSE = "pause_vpn"

        const val EXTRA_XRAY_CONFIG_PARC = "xray_config_parc" // XRay config params (from backend)
        const val EXTRA_SPLIT_MODE = "split_tunneling_mode"
        const val EXTRA_ALLOW_LAN = "extra_allow_lan"
        const val EXTRA_DNS_SERVER_IP = "extra_dns_server_ip"

        const val EXTRA_TURN_OFF_DELAY_IN_MILLIS = "turn_off_delay_in_millis"

        private val logger: Logger = Logger.withTag("XRayService")

        fun makeIntentForConnect(
            context: Context,
            config: XRayConnectConfig,
            allowLanConnections: Boolean,
            splitMode: Int,
            dnsServerIP: String,
            timeToShutDown: Long? = 0L
        ): Intent =
            Intent(context, XRayService::class.java).apply {
                action = ACTION_CONNECT

                putExtra(EXTRA_XRAY_CONFIG_PARC, config)
                putExtra(EXTRA_SPLIT_MODE, splitMode)
                putExtra(EXTRA_ALLOW_LAN, allowLanConnections)
                putExtra(EXTRA_TURN_OFF_DELAY_IN_MILLIS, timeToShutDown)
                putExtra(EXTRA_DNS_SERVER_IP, dnsServerIP)
            }

        fun makeIntentForDisconnect(context: Context): Intent =
            Intent(context, XRayService::class.java).apply {
                action = ACTION_DISCONNECT
            }
    }

    private val notificationHelper: NotificationHelper by lazy {
        (applicationContext as MyApp).notificationHelper
    }

    private var xrayConfig: XRayConnectConfig? = null

    private var splitMode = 0
    private var dnsServerIP: String? = null
    private var allowLanConnection = false

    // XRay point
    private lateinit var v2RayPoint: XrayPoint

    private var splitIPs = mutableListOf<String>()
    private var mInterface: ParcelFileDescriptor? = null
    private var process: Process? = null
    private var isFdClosed: Boolean = true

    private var fdThread: Thread? = null
    private var v2rayThread: Thread? = null
    private var tun2socksThread: Thread? = null

    private var timeToShutDown = 0L
    private var shutdownTimer: Timer? = null

    var isStopped: Boolean = false
        private set

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val defaultCoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val v2RayCallbackReference: WeakReference<V2RayCallback> = WeakReference(
        V2RayCallback(WeakReference(this))
    )

    class V2RayCallback(private val service: WeakReference<XRayService>) : CallbackSet {
        override fun connectionError(error: String?) {
            logger.d("Callback error: $error")
            service.get()?.stopV2Ray()
        }

        override fun onEmitStatus(l: Long, s: String): Long {
            logger.d("Callback: onEmitStatus: l = $l, $s")

            service.get()?.let {
                if (it.isStopped) return@let

                if (l in 1..2) {
                    service.get()?.getConnectionManager()
                        ?.setConnectionStatus(ConnectionStatus.Connecting(s))
                }
            }

            return 0
        }


        override fun protect(l: Long): Boolean {
            logger.d("Callback: Protect $l")
            return service.get()?.requestProtect(l) ?: false
        }

        override fun setup() {
            logger.d("Callback: onSetup")
            service.get()?.setupV2Ray()
        }

        override fun shutdown(): Long { // called by go
            logger.d("Callback: shutdown")
            return 0
        }
    }

    fun requestProtect(l: Long): Boolean {
        return protect(l.toInt())
    }

    private val serverRepository: ServersRepository
        get() = SharedSDK.koinApplication.koin.get()

    private val makeXrayConfigFromServerUseCase: MakeXrayConfigFromServerUseCase
        get() = SharedSDK.koinApplication.koin.get()

    private val setCurrentServerUseCase: SetCurrentServerUseCase
        get() = SharedSDK.koinApplication.koin.get()

    override fun onCreate() {
        super.onCreate()

        v2RayPoint = Vpn.newXrayPoint(
            v2RayCallbackReference.get(),
        )

        val notification = notificationHelper.buildNotification(
            ConnectionStatus.Connecting(null),
            getString(R.string.vpn_status_connecting),
        )
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            getServiceType()
        )

        initXray()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requireNotNull(intent) { return START_NOT_STICKY }

        logger.d("onStartCommand ${intent.action}, isRunning: ${v2RayPoint.isRunning}")

        when (intent.action) {
            ACTION_CONNECT -> {
                splitMode = intent.getIntExtra(EXTRA_SPLIT_MODE, 0)
                dnsServerIP = intent.getStringExtra(EXTRA_DNS_SERVER_IP)
                timeToShutDown = intent.getLongExtra(EXTRA_TURN_OFF_DELAY_IN_MILLIS, 0L)
                allowLanConnection = intent.getBooleanExtra(EXTRA_ALLOW_LAN, false)

                intent.getParcelableExtraCompat<XRayConnectConfig>(EXTRA_XRAY_CONFIG_PARC)?.let {
                    xrayConfig = it
                }

                Logger.d(
                    "splitMode: $splitMode\ndnsServerIP: $dnsServerIP\n" +
                            "timeToShutdown: $timeToShutDown\nallowLanConnection: $allowLanConnection"
                )
                Logger.d("xrayConfig: $xrayConfig")

                if (xrayConfig != null) {
                    getConnectionManager().setConnectionStatus(
                        ConnectionStatus.Connecting(null)
                    )
                    notificationHelper.updateNotification(
                        ConnectionStatus.Connecting(null),
                        null
                    )
                    startV2RayPoint(xrayConfig!!)
                } else {
                    getConnectionManager().setConnectionStatus(
                        ConnectionStatus.Error("XRay config is empty")
                    )
                }
            }

            ACTION_DISCONNECT -> {
                isStopped = true
                stopVpn()
            }

            ACTION_PAUSE -> {
                pauseVpn()
            }

            else -> {
                connectToLastUsedXrayServer()
            }
        }

        return START_NOT_STICKY
    }

    private fun onVpnConnected() {
        logger.d("onVpnConnected")

        setupShutdownTimer()
        //todo can add message
        getConnectionManager().setConnectionStatus(ConnectionStatus.Connected)
    }

    private fun onVpnConnectionError(errorMessage: String? = null) {
        logger.d("MyTag! XRayService onVpnConnectionError = $errorMessage")
        getConnectionManager().setConnectionStatus(ConnectionStatus.Error(errorMessage))
        stopVpn()
    }

    private fun getConnectionManager(): VpnConnectionManager = (applicationContext as MyApp).let {
        return it.vpnConnectionManager
    }

    private fun initXray() {
        logger.d("initXRay method")
        this.getConnectionManager()
            .setConnectionStatus(ConnectionStatus.Connecting(getString(R.string.initializing_xray)))
        try {
            Seq.setContext(applicationContext)
            var assets = applicationContext.getExternalFilesDir("assets")
            if (assets == null) assets = applicationContext.getDir("assets", 0)
            Vpn.initV2Env(assets!!.absolutePath)
        } catch (e: Exception) {
            logger.e("initXRay $e")
        }
    }

    private fun startV2RayPoint(connectConfig: XRayConnectConfig) {
        logger.d("startV2rayPoint")

        if (v2RayPoint.isRunning) stopV2rayPoint()
        if (!v2RayPoint.isRunning) {
            try {
                with(connectConfig) {
                    v2rayThread = Thread {
                        if (customConfigs.isNullOrEmpty()) {
                            v2RayPoint.runLoop(serverIp, appUUID, sni)
                        } else {

                            val ip = customConfigs?.get(CustomConfigFields.XRAY_FIELD_HOST)
                            val port =
                                customConfigs?.get(CustomConfigFields.XRAY_FIELD_PORT)
                            val config = customConfigs?.get(CustomConfigFields.XRAY_FIELD_CONFIG)

                            v2RayPoint.runLoopWithConfig(ip, port, config)
                        }
                    }

                    v2rayThread?.start()
                }
            } catch (e: java.lang.Exception) {
                if (BuildConfig.DEBUG) logger.e(e.message ?: "Error while connecting")
            }
        }
    }

    private fun getServiceType(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
    } else {
        0
    }

    private fun setupV2Ray() {
        logger.d("setupV2Ray")
        if (isStopped) return

        val builder: Builder = Builder().apply {
            setMtu(VpnConstants.VPN_MTU)
            addAddress(VpnConstants.PRIVATE_VLAN4_CLIENT, 30)
        }

        for (ips in calculateAllowedIpsXRay()) {
            val addr = ips.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            builder.addRoute(addr[0], addr[1].toInt())
        }

        builder.addDnsServer(dnsServerIP ?: VpnConstants.DNS_SERVER_1)

        setupSplitTunnelingApps(builder)

        // Close the old interface since the parameters have been changed.
        try {
            if (!isFdClosed) {
                logger.d("FD: closing")
                mInterface?.close()
                isFdClosed = true
            }
        } catch (ignored: java.lang.Exception) {
            // ignored
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setMetered(false)
        }

        // Create a new interface using the builder and save the parameters.
        try {
            logger.d("FD: opening")
            mInterface = builder.establish()
            isFdClosed = false
            runTun2socks()
            logger.d("Before measure delay")
            checkConnection()
        } catch (e: java.lang.Exception) {
            logger.e("Error in setup: ${e.stackTraceToString()}")
            stopV2Ray()
        }
    }

    private fun setupSplitTunnelingApps(builder: VpnService.Builder) {
        val excludedApps = ((applicationContext as MyApp)
            .vpnConnectionManager
            .excludedPackageIds + AppPackageId(packageName)).toMutableList()

        val mode = (applicationContext as MyApp).vpnConnectionManager.splitMode

        when (mode) {
            0 -> {
                addDisallowedApplication(builder, this.packageName)
            }

            1 -> {
                if (!excludedApps.contains(AppPackageId(packageName))) {
                    excludedApps.add(AppPackageId(packageName))
                }
                excludedApps.forEach { excludedApp ->
                    addDisallowedApplication(builder, excludedApp.packageId)
                }
            }

            2 -> {
                excludedApps.remove(AppPackageId(packageName))
                excludedApps.forEach { excludedApp ->
                    addAllowedApplication(builder, excludedApp.packageId)
                }
            }
        }
        logger.d("Split mode = $mode excluded apps: $excludedApps")
    }

    private fun addDisallowedApplication(builder: VpnService.Builder, packageId: String) {
        try {
            builder.addDisallowedApplication(packageId)
        } catch (e: java.lang.Exception) {
            logger.e(e.stackTraceToString())
        }
    }

    private fun addAllowedApplication(builder: VpnService.Builder, packageId: String) {
        try {
            builder.addAllowedApplication(packageId)
        } catch (e: java.lang.Exception) {
            logger.e(e.stackTraceToString())
        }
    }

    private fun checkConnection() {
        logger.d("checkConnection")

        this.getConnectionManager()
            .setConnectionStatus(ConnectionStatus.Connecting(getString(R.string.check_connection)))
        var time = -1L
        var exception: Exception? = null

        try {
            time = v2RayPoint.measureDelay()
        } catch (e: Exception) {
            logger.d("Error: In checkConnection, measureDelay method")
            logger.e(e.stackTraceToString())

            exception = e
        }
        logger.d("time: $time")
        if (time == -1L) {
            val currentStatus = getConnectionManager().connectionStatus.value
            logger.d("current status is: $currentStatus")

            if (currentStatus !is ConnectionStatus.Disconnected) {
                onVpnConnectionError(exception?.localizedMessage ?: "Error while connecting")
            }
        } else {
            onVpnConnected()
        }
    }

    private fun calculateAllowedIpsXRay(): List<String> {
        if (allowLanConnection) {
            splitIPs.addAll(NetworkUtils.getLocalNetworks(this))
        }

        val allowedIps = mutableListOf<String>()
        val ipRangeSet: IPRangeSet = IPRangeSet.fromString(DEFAULT_ALLOWED_IP_PREFIX_4)

        if (splitIPs.isEmpty()) {
            allowedIps.add(DEFAULT_ALLOWED_IP_PREFIX_4)
            return allowedIps
        }

        for (ip in splitIPs) {
            try {
                ipRangeSet.remove(IPRange(ip))
            } catch (e: UnknownHostException) {
                if (BuildConfig.DEBUG) logger.e(e.stackTraceToString())
            }
        }

        for (ipRange in ipRangeSet.subnets()) {
            val startIP = ipRange.from?.hostAddress ?: continue
            val endIP = ipRange.to?.hostAddress ?: continue
            val mAllowedIPs = NetworkUtils.range2CIDRList(startIP, endIP)
            allowedIps.addAll(mAllowedIPs)
        }

        // IPRangeSet class does not support IPv6 so we need to add them here
        // explicitly to not leak IPv6 for Wireguard then split tunneling is used
        // Also ::/0 CIDR should not be used for IPv6 as it causes LAN connection issues
        allowedIps.add(
            if (allowLanConnection) {
                DEFAULT_ALLOWED_IP_PREFIX_6_NO_LAN
            } else {
                DEFAULT_ALLOWED_IP_PREFIX_6
            }
        )
        return allowedIps
    }

    private fun runTun2socks() {
        val cmd = listOf(
            File(
                applicationContext.applicationInfo.nativeLibraryDir,
                VpnConstants.TUN2SOCKS
            ).absolutePath,
            VpnConstants.netif_ipaddr,
            VpnConstants.PRIVATE_VLAN4_ROUTER,
            VpnConstants.netif_netmask,
            VpnConstants.netmask_ip,
            VpnConstants.socks_server_addr,
            VpnConstants.server_addr_ + VpnConstants.PORT_SOCKS,
            VpnConstants.tunmtuParam,
            VpnConstants.VPN_MTU.toString(),
            VpnConstants.sock_pathParam,
            VpnConstants.sock_path,
            VpnConstants.enable_udprelay,
            VpnConstants.loglevel,
            VpnConstants.notice
        )

        try {
            val proBuilder = ProcessBuilder(cmd)
            proBuilder.redirectErrorStream(true)
            process = proBuilder.directory(applicationContext.filesDir).start()

            tun2socksThread = Thread {
                if (BuildConfig.DEBUG) logger.d(VpnConstants.TUN2SOCKS + " running thread")

                try {
                    process?.waitFor()
                } catch (e: InterruptedException) {
                    process?.destroy()
                    logger.e(e.stackTraceToString())
                } catch (e: Exception) {
                    logger.e(e.stackTraceToString())
                }

                if (BuildConfig.DEBUG) logger.d(VpnConstants.TUN2SOCKS + " exited")
            }

            tun2socksThread?.start()
            if (BuildConfig.DEBUG) logger.d("process: " + process.toString())
            sendFileDescriptor()
        } catch (e: java.lang.Exception) {
            if (BuildConfig.DEBUG) logger.e(e.stackTraceToString())
        }
    }

    private fun sendFileDescriptor() {
        val fd = mInterface?.fileDescriptor ?: return
        val path = File(applicationContext.filesDir, "sock_path").absolutePath

        coroutineScope.launch {
            var tries = 0

            while (true) try {
                delay(50L shl tries)
                Log.d(packageName, "sendFd tries: $tries")
                logger.d("sendFd: tries: $tries")
                LocalSocket().use { localSocket ->
                    localSocket.connect(
                        LocalSocketAddress(
                            path,
                            LocalSocketAddress.Namespace.FILESYSTEM
                        )
                    )
                    localSocket.setFileDescriptorsForSend(arrayOf(fd))
                    localSocket.outputStream.write(42)
                }
                break
            } catch (e: Exception) {
                logger.e(e.toString())
                if (tries > 5) break
                tries += 1
            }
        }
    }

    private fun stopV2Ray(isForPause: Boolean = false) {
        logger.d("stopV2Ray")

        try {
            process?.destroy()
            fdThread?.interrupt()
        } catch (e: java.lang.Exception) {
            if (BuildConfig.DEBUG) logger.e(e.stackTraceToString())
        }

        stopV2rayPoint()
        if (!isForPause) stopSelf()

        try {
            if (!isFdClosed) {
                logger.d("FD: closing")
                mInterface!!.close()
                isFdClosed = true
            }
        } catch (ignored: java.lang.Exception) {
            // ignored
        }

        logger.d("After stopV2Ray")
    }

    private fun stopV2rayPoint() {
        logger.d("stopV2rayPoint")

        if (v2RayPoint.isRunning) {
            try {
                logger.d("stopLoop called")
                v2RayPoint.stopLoop()
            } catch (e: java.lang.Exception) {
                if (BuildConfig.DEBUG) logger.e(e.stackTraceToString())
            }
        }
        v2rayThread?.interrupt()
    }

    override fun onDestroy() {
        logger.d("onDestroy")
        val currentVpnStatus = getConnectionManager().connectionStatus.value
        if (currentVpnStatus is ConnectionStatus.Connected || currentVpnStatus is ConnectionStatus.Connecting) {
            getConnectionManager().setConnectionStatus(ConnectionStatus.Disconnected)
        }

        shutdownTimer?.stopTimer()
        shutdownTimer = null

        coroutineScope.cancel()
        defaultCoroutineScope.cancel()


        super.onDestroy()
    }


    private fun setupShutdownTimer() {
        if (timeToShutDown == 0L) return
        shutdownTimer?.stopTimer()
        shutdownTimer = null

        shutdownTimer = Timer.countdown(timeToShutDown, 1000L)

        defaultCoroutineScope.launch {
            shutdownTimer?.currentMillisFlow?.collect {
                if (it == 1000L) {
                    getConnectionManager().setConnectionStatus(ConnectionStatus.Idle())
                    stopVpn()
                }
            }
        }
        shutdownTimer?.startTimer(defaultCoroutineScope)
    }

    private fun stopVpn() {
        stopV2Ray()
        stopSelf()
    }

    private fun pauseVpn() {
        stopV2Ray(isForPause = true)
        getConnectionManager().setConnectionStatus(ConnectionStatus.Paused)
    }

    private fun connectToLastUsedXrayServer() {
        coroutineScope.launch {
            val server = serverRepository.getLatestConnectedXrayServer()

            server?.let {
                xrayConfig = makeXrayConfigFromServerUseCase(it.toVpnServer())
                getConnectionManager().setConnectionStatus(
                    ConnectionStatus.Connecting(null)
                )
                notificationHelper.updateNotification(
                    ConnectionStatus.Connecting(null),
                    null
                )
                startV2RayPoint(xrayConfig!!)
                setCurrentServerUseCase(it.uuid)
            }
        }
    }

}
