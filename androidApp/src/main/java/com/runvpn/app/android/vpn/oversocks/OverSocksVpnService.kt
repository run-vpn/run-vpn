package com.runvpn.app.android.vpn.oversocks

import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.ServiceCompat
import co.touchlab.kermit.Logger
import com.runvpn.app.android.BuildConfig
import com.runvpn.app.android.MyApp
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.getParcelableExtraCompat
import com.runvpn.app.android.utils.IPRange
import com.runvpn.app.android.utils.IPRangeSet
import com.runvpn.app.android.utils.NetworkUtils
import com.runvpn.app.android.utils.VpnConstants
import com.runvpn.app.android.utils.VpnConstants.CONF_FILE_NAME
import com.runvpn.app.android.utils.VpnConstants.OVERSOCKS_LOCAL_IP
import com.runvpn.app.android.utils.VpnConstants.OVERSOCKS_LOCAL_IP_PREFIX
import com.runvpn.app.android.utils.VpnConstants.OVERSOCKS_MTU
import com.runvpn.app.android.vpn.NotificationHelper
import com.runvpn.app.core.common.presentation.Notification
import com.runvpn.app.data.connection.AppPackageId
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.tea.utils.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.UnknownHostException

class OverSocksVpnService : VpnService() {


    private external fun startOverSocksVpn(configPath: String, fd: Int)
    private external fun stopOverSocksVpn()

    companion object {


        init {
            System.loadLibrary("hev-socks5-tunnel")
        }

        const val ACTION_CONNECT = "action_connect" // Action for this service for connect to VPN
        const val ACTION_DISCONNECT = "action_disconnect" // Action to disconnect
        const val ACTION_PAUSE = "pause_vpn"

        const val EXTRA_OVERSOCKS_CONFIG_PARC = "oversocks_config_parc"
        const val EXTRA_SPLIT_MODE = "split_tunneling_mode"
        const val EXTRA_ALLOW_LAN = "extra_allow_lan"
        const val EXTRA_DNS_SERVER_IP = "extra_dns_server_ip"

        const val EXTRA_TURN_OFF_DELAY_IN_MILLIS = "turn_off_delay_in_millis"

        private val logger: Logger = Logger.withTag("OverSocksVpnService")

        fun makeIntentForConnect(
            context: Context,
            config: OverSocksConnectConfig,
            splitMode: Int,
            allowLanConnection: Boolean,
            dnsServerIp: String,
            timeToShutDown: Long? = 0L
        ): Intent =
            Intent(context, OverSocksVpnService::class.java).apply {
                action = ACTION_CONNECT

                putExtra(EXTRA_OVERSOCKS_CONFIG_PARC, config)
                putExtra(EXTRA_SPLIT_MODE, splitMode)
                putExtra(EXTRA_ALLOW_LAN, allowLanConnection)
                putExtra(EXTRA_TURN_OFF_DELAY_IN_MILLIS, timeToShutDown)
                putExtra(EXTRA_DNS_SERVER_IP, dnsServerIp)
            }

        fun makeIntentForDisconnect(context: Context): Intent =
            Intent(context, OverSocksVpnService::class.java).apply {
                action = ACTION_DISCONNECT
            }

        fun makeIntentForPause(context: Context): Intent =
            Intent(context, OverSocksVpnService::class.java).apply {
                action = ACTION_PAUSE
            }

    }


    private val notificationHelper: NotificationHelper by lazy {
        (applicationContext as MyApp).notificationHelper
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var splitMode = 0
    private var dnsServerIp: String? = null
    private var timeToShutDown = 0L
    private var shutdownTimer: Timer? = null
    private var allowLanConnection = false

    private var splitIPs = mutableListOf<String>()


    private var tunFileDescriptor: ParcelFileDescriptor? = null


    private var overSocksConnectConfig: OverSocksConnectConfig? = null


    override fun onCreate() {
        super.onCreate()

        val notification = notificationHelper.buildNotification(
            ConnectionStatus.Connecting(null),
            getString(R.string.vpn_status_connecting),
        )

        ServiceCompat.startForeground(
            this,
            Notification.NOTIFICATION_ID,
            notification,
            getServiceType()
        )
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requireNotNull(intent) { return START_NOT_STICKY }

        when (intent.action) {
            ACTION_CONNECT -> {
                splitMode = intent.getIntExtra(EXTRA_SPLIT_MODE, 0)
                allowLanConnection = intent.getBooleanExtra(EXTRA_ALLOW_LAN, false)
                dnsServerIp = intent.getStringExtra(EXTRA_DNS_SERVER_IP)
                timeToShutDown =
                    intent.getLongExtra(EXTRA_TURN_OFF_DELAY_IN_MILLIS, 0L)

                intent.getParcelableExtraCompat<OverSocksConnectConfig>(
                    EXTRA_OVERSOCKS_CONFIG_PARC
                )?.let {
                    overSocksConnectConfig = it
                }

                if (overSocksConnectConfig != null) {
                    getConnectionManager().setConnectionStatus(
                        ConnectionStatus.Connecting(getString(R.string.vpn_status_connecting))
                    )
                    notificationHelper.updateNotification(
                        ConnectionStatus.Connecting(getString(R.string.vpn_status_connecting)),
                        null
                    )
                    logger.i("OverSocksConfig = $overSocksConnectConfig")
                    initOverSocks()
                } else {
                    getConnectionManager().setConnectionStatus(
                        ConnectionStatus.Error("OverSocks config is empty")
                    )
                }
            }

            ACTION_DISCONNECT -> {
                stopVpn(resultStatus = ConnectionStatus.Disconnected)
                stopSelf()
            }

            ACTION_PAUSE -> {
                stopVpn(resultStatus = ConnectionStatus.Paused)
            }

            else -> {
                // ignored
            }
        }

        return START_NOT_STICKY
    }


    private fun initOverSocks() {
        if (tunFileDescriptor != null) return

        /* VPN */
        val builder = Builder()
        builder.setBlocking(false)
        builder.setMtu(OVERSOCKS_MTU)

        builder.addAddress(OVERSOCKS_LOCAL_IP, OVERSOCKS_LOCAL_IP_PREFIX)

        for (ips in calculateAllowedIps()) {
            val addr = ips.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            builder.addRoute(addr[0], addr[1].toInt())
        }

        builder.addDnsServer(dnsServerIp ?: VpnConstants.DNS_SERVER_1)

        setupSplitTunnelingApps(builder)

        tunFileDescriptor = builder.establish()

        if (tunFileDescriptor == null) {
            stopSelf()
            return
        }
        startOverSockVpn()
    }

    private fun calculateAllowedIps(): List<String> {
        if (allowLanConnection) {
            splitIPs.addAll(NetworkUtils.getLocalNetworks(this))
        }

        val allowedIps = mutableListOf<String>()
        val ipRangeSet: IPRangeSet = IPRangeSet.fromString(VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_4)

        if (splitIPs.isEmpty()) {
            allowedIps.add(VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_4)
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
                VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_6_NO_LAN
            } else {
                VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_6
            }
        )
        return allowedIps
    }

    private fun startOverSockVpn() {
        /* TProxy */
        val tproxyFile = File(cacheDir, CONF_FILE_NAME)
        try {
            tproxyFile.createNewFile()
            val fileOutputStream = FileOutputStream(tproxyFile, false)
            val overSocksConf = makeOverSocksConfig()
            fileOutputStream.write(overSocksConf.toByteArray())
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        checkNotNull(tunFileDescriptor)
        startOverSocksVpn(tproxyFile.absolutePath, tunFileDescriptor!!.fd)
        getConnectionManager().setConnectionStatus(ConnectionStatus.Connected)
        setupShutdownTimer()
    }

    private fun makeOverSocksConfig() = "misc:\n" +
            "  task-stack-size: 20480\n" +
            "tunnel:\n" +
            "  mtu: $OVERSOCKS_MTU\n" +   //can be changed?
            "socks5:\n" +
            "  port: ${overSocksConnectConfig?.port}\n" +  //can be changed
            "  address: '${overSocksConnectConfig?.host}'\n" +    //can be changed
            "  udp: '${overSocksConnectConfig?.udpOverTcp}'\n" +  //can be changed
            "  username: '${overSocksConnectConfig?.userName}'\n" +    //can be changed
            "  password: '${overSocksConnectConfig?.password}'"    //can be changed

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

    private fun getConnectionManager(): VpnConnectionManager =
        (applicationContext as MyApp).let {
            return it.vpnConnectionManager
        }

    private fun getServiceType() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        } else {
            0
        }

    private fun stopVpn(resultStatus: ConnectionStatus) {
        shutdownTimer?.stopTimer()
        shutdownTimer = null
        coroutineScope.cancel()

        stopOverSocksVpn()
        getConnectionManager().setConnectionStatus(resultStatus)
        try {
            tunFileDescriptor?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        tunFileDescriptor = null
    }

    override fun onDestroy() {
        val currentVpnStatus = getConnectionManager().connectionStatus.value
        if (currentVpnStatus is ConnectionStatus.Connected || currentVpnStatus is ConnectionStatus.Connecting) {
            getConnectionManager().setConnectionStatus(ConnectionStatus.Disconnected)
        }

        shutdownTimer?.stopTimer()
        shutdownTimer = null
        coroutineScope.cancel()
        super.onDestroy()
    }

    private fun setupShutdownTimer() {
        logger.d("OverSocks Setup Shutdown Timer $timeToShutDown")
        if (timeToShutDown == 0L) return
        shutdownTimer?.stopTimer()
        shutdownTimer = null

        shutdownTimer = Timer.countdown(timeToShutDown, 1000L)

        coroutineScope.launch {
            shutdownTimer?.currentMillisFlow?.collect {
                logger.d("OverSocks Setup Shutdown Timer $it")
                if (it == 1000L) {
                    stopVpn(resultStatus = ConnectionStatus.Idle())
                    stopSelf()
                }
            }
        }
        shutdownTimer?.startTimer(coroutineScope)
    }
}
