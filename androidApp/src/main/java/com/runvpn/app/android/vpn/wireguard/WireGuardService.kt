package com.runvpn.app.android.vpn.wireguard

import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import androidx.core.app.ServiceCompat
import co.touchlab.kermit.Logger
import com.runvpn.app.android.BuildConfig
import com.runvpn.app.android.MyApp
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.getParcelableExtraCompat
import com.runvpn.app.android.utils.VpnConstants
import com.runvpn.app.android.utils.IPRange
import com.runvpn.app.android.utils.IPRangeSet
import com.runvpn.app.android.utils.NetworkUtils
import com.runvpn.app.android.vpn.NotificationHelper
import com.runvpn.app.core.common.presentation.Notification
import com.runvpn.app.data.connection.AppPackageId
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.tea.utils.Timer
import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.BackendException
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import com.wireguard.config.Interface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.util.concurrent.Executors


class WireGuardService : VpnService() {


    companion object {
        const val ACTION_CONNECT = "action_connect" // Action for this service for connect to VPN
        const val ACTION_DISCONNECT = "action_disconnect" // Action to disconnect
        const val ACTION_PAUSE = "pause_vpn"

        const val EXTRA_WIREGUARD_CONFIG_PARC = "wireguard_config_parc"
        const val EXTRA_SPLIT_MODE = "split_tunneling_mode"
        const val EXTRA_ALLOW_LAN = "extra_allow_lan"
        const val EXTRA_DNS_SERVER_IP = "extra_dns_server_ip"

        const val EXTRA_TURN_OFF_DELAY_IN_MILLIS = "turn_off_delay_in_millis"

        private val logger: Logger = Logger.withTag("WireGuardService")

        fun makeIntentForConnect(
            context: Context,
            config: WireGuardConnectConfig,
            splitMode: Int,
            allowLanConnection: Boolean,
            dnsServerIp: String,
            timeToShutDown: Long? = 0L
        ): Intent =
            Intent(context, WireGuardService::class.java).apply {
                action = ACTION_CONNECT

                putExtra(EXTRA_ALLOW_LAN, allowLanConnection)
                putExtra(EXTRA_WIREGUARD_CONFIG_PARC, config)
                putExtra(EXTRA_SPLIT_MODE, splitMode)
                putExtra(EXTRA_TURN_OFF_DELAY_IN_MILLIS, timeToShutDown)
                putExtra(EXTRA_DNS_SERVER_IP, dnsServerIp)
            }

        fun makeIntentForDisconnect(context: Context): Intent =
            Intent(context, WireGuardService::class.java).apply {
                action = ACTION_DISCONNECT
            }

        fun makeIntentForPause(context: Context): Intent =
            Intent(context, WireGuardService::class.java).apply {
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

    private lateinit var backend: Backend
    private lateinit var tunnel: WireGuardTunnel

    private var splitIPs = ArrayList<String>()

    private var wireGuardConfig: WireGuardConnectConfig? = null

    private var wireGuardExecutor = Executors.newSingleThreadExecutor()


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

        initWireGuard()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requireNotNull(intent) { return START_NOT_STICKY }

        var downStatus: ConnectionStatus = ConnectionStatus.Disconnected

        when (intent.action) {
            ACTION_CONNECT -> {
                splitMode = intent.getIntExtra(EXTRA_SPLIT_MODE, 0)
                timeToShutDown = intent.getLongExtra(EXTRA_TURN_OFF_DELAY_IN_MILLIS, 0L)
                allowLanConnection = intent.getBooleanExtra(EXTRA_ALLOW_LAN, false)

                intent.getParcelableExtraCompat<WireGuardConnectConfig>(
                    EXTRA_WIREGUARD_CONFIG_PARC
                )?.let {
                    wireGuardConfig = it
                }

                if (wireGuardConfig != null) {
                    getConnectionManager().setConnectionStatus(
                        ConnectionStatus.Connecting(null)
                    )
                    notificationHelper.updateNotification(
                        ConnectionStatus.Connecting(null),
                        null
                    )
                    startWireGuardVpn()
                } else {
                    getConnectionManager().setConnectionStatus(
                        ConnectionStatus.Error("WireGuard config is empty")
                    )
                }
            }

            ACTION_DISCONNECT -> {
                stopWireGuardVPN()
                stopSelf()
            }

            ACTION_PAUSE -> {
                stopWireGuardVPN()
                downStatus = ConnectionStatus.Paused
            }

            else -> {
                // ignored
            }
        }

        tunnel.setOnVpnStateListener(object : WireGuardTunnel.OnVpnStateListener {
            override fun onVpnStateChanged(newState: Tunnel.State) {
                Logger.i("WireGuardVpnState Listener: $newState")
                when (newState) {
                    Tunnel.State.DOWN -> {
                        getConnectionManager().setConnectionStatus(downStatus)
                    }

                    Tunnel.State.TOGGLE -> {
                        //ignored
                    }

                    Tunnel.State.UP -> {
                        setupShutdownTimer()
                        //todo can add message
                        getConnectionManager().setConnectionStatus(ConnectionStatus.Connected)
                    }
                }
            }
        })

        return START_NOT_STICKY
    }

    private fun initWireGuard() {
        backend = GoBackend(this)
        tunnel = WireGuardTunnel()
    }

    private fun startWireGuardVpn() {
        wireGuardConfig?.let {
            val config =
                getTunnelConfig(it.ip, it.privateKey, it.dnsServers, it.peers)
            wireGuardExecutor.execute {
                try {
                    backend.setState(tunnel, Tunnel.State.UP, config)
                } catch (e: BackendException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun stopWireGuardVPN(isPause: Boolean = false) {
        wireGuardExecutor.execute {
            try {
                backend.setState(tunnel, Tunnel.State.DOWN, null)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                getConnectionManager().setConnectionStatus(
                    if (isPause) ConnectionStatus.Disconnected else ConnectionStatus.Paused
                )
            }
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

    private fun getTunnelConfig(
        ip: String,
        privateKey: String,
        dnsServers: String?,
        peers: List<WireGuardConnectPeer>
    ): Config? {
        val resultDnsServers = dnsServers?.split(",")?.toMutableList()?.apply {
            add(0, dnsServerIp ?: VpnConstants.DNS_SERVER_1)
        }?.joinToString(",")

        try {
            val allowedIps = calculateAllowedIps()
            val configPeers = peers.map { it.toWireGuardConfigPeer(allowedIps) }
            val wgInterfaceBuilder = Interface.Builder()
                .parseAddresses(ip)
                .parseDnsServers(resultDnsServers.toString())
                .parsePrivateKey(privateKey)

            setupSplitTunnelingApps(wgInterfaceBuilder)
            return Config.Builder().addPeers(configPeers).setInterface(wgInterfaceBuilder.build())
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun setupSplitTunnelingApps(builder: Interface.Builder) {
        val excludedApps = ((applicationContext as MyApp)
            .vpnConnectionManager
            .excludedPackageIds + AppPackageId(packageName)).toMutableList()

        val mode = (applicationContext as MyApp).vpnConnectionManager.splitMode

        when (mode) {
            0 -> {
                builder.excludeApplication(packageName)
            }

            1 -> {
                if (!excludedApps.contains(AppPackageId(packageName))) {
                    excludedApps.add(AppPackageId(packageName))
                }
                builder.excludeApplications(excludedApps.map { it.packageId })
            }

            2 -> {
                excludedApps.remove(AppPackageId(packageName))
                builder.includeApplications(excludedApps.map { it.packageId })
            }
        }
        logger.d("Split mode = $mode excluded apps: $excludedApps")
    }

    private fun calculateAllowedIps(): String {
        if (allowLanConnection) {
            splitIPs.addAll(NetworkUtils.getLocalNetworks(this))
        }

        val allowedIps = StringBuilder()
        val ipRangeSet: IPRangeSet = IPRangeSet.fromString(VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_4)

        if (splitIPs.isEmpty()) {
            allowedIps.append(VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_4).append(", ")
            return allowedIps.toString()
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
            mAllowedIPs.forEach {
                allowedIps.append(it).append(", ")
            }
        }

        // IPRangeSet class does not support IPv6 so we need to add them here
        // explicitly to not leak IPv6 for Wireguard then split tunneling is used
        // Also ::/0 CIDR should not be used for IPv6 as it causes LAN connection issues
        allowedIps.append(
            if (allowLanConnection) {
                VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_6_NO_LAN
            } else {
                VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_6
            }
        ).append(", ")
        return allowedIps.toString()
    }

    override fun onDestroy() {
        val currentShutdownTime = shutdownTimer?.currentMillisFlow?.value ?: 0L
        if (currentShutdownTime > 0L) {
            getConnectionManager().setConnectionStatus(ConnectionStatus.Idle())
        } else if (getConnectionManager().connectionStatus.value !is ConnectionStatus.Error) {
            getConnectionManager().setConnectionStatus(ConnectionStatus.Disconnected)
        }

        shutdownTimer?.stopTimer()
        shutdownTimer = null
        coroutineScope.cancel()
        super.onDestroy()
    }

    private fun setupShutdownTimer() {
        if (timeToShutDown == 0L) return
        shutdownTimer?.stopTimer()
        shutdownTimer = null

        shutdownTimer = Timer.countdown(timeToShutDown, 1000L)

        coroutineScope.launch {
            shutdownTimer?.currentMillisFlow?.collect {
                if (it == 1000L) {
                    stopWireGuardVPN()
                }
            }
        }
        shutdownTimer?.startTimer(coroutineScope)
    }

}
