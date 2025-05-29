package com.runvpn.app.android.vpn.ikev2

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.ServiceCompat
import co.touchlab.kermit.Logger
import com.runvpn.app.android.BuildConfig
import com.runvpn.app.android.MainActivity
import com.runvpn.app.android.MyApp
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.getParcelableExtraCompat
import com.runvpn.app.android.utils.VpnConstants
import com.runvpn.app.android.utils.NetworkUtils
import com.runvpn.app.android.vpn.NotificationHelper
import com.runvpn.app.core.common.presentation.Notification.NOTIFICATION_ID
import com.runvpn.app.data.connection.AppPackageId
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.tea.utils.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.strongswan.android.data.VpnProfile
import org.strongswan.android.data.VpnType
import org.strongswan.android.logic.CharonVpnService
import org.strongswan.android.logic.VpnStateService
import org.strongswan.android.security.LocalCertificateStore
import org.strongswan.android.utils.IPRangeSet
import java.net.UnknownHostException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.SortedSet


class Ikev2VpnService : CharonVpnService() {


    companion object {

        private const val ACTION_CONNECT = "action_connect"
        private const val ACTION_DISCONNECT = "action_disconnect"
        private const val ACTION_PAUSE = "action_pause"

        private const val EXTRA_CONFIG = "extra_config"

        private const val EXTRA_SPLIT_MODE = "split_tunneling_mode"
        private const val EXTRA_ALLOW_LAN = "extra_allow_lan"
        private const val EXTRA_DNS_SERVER_IP = "extra_dns_server_ip"

        private const val EXTRA_TURN_OFF_DELAY_IN_MILLIS = "turn_off_delay_in_millis"

        private val logger: Logger = Logger.withTag("Ikev2VpnService")


        fun makeIntentForConnect(
            context: Context,
            config: Ikev2ConnectConfig,
            splitMode: Int,
            allowLanConnection: Boolean,
            dnsServerIp: String,
            timeToShutDown: Long? = 0L
        ): Intent =
            Intent(context, Ikev2VpnService::class.java).apply {
                action = ACTION_CONNECT
                putExtra(EXTRA_CONFIG, config)
                putExtra(EXTRA_SPLIT_MODE, splitMode)
                putExtra(EXTRA_ALLOW_LAN, allowLanConnection)
                putExtra(EXTRA_TURN_OFF_DELAY_IN_MILLIS, timeToShutDown)
                putExtra(EXTRA_DNS_SERVER_IP, dnsServerIp)
            }

        fun makeIntentForDisconnect(context: Context): Intent =
            Intent(context, Ikev2VpnService::class.java).apply {
                action = ACTION_DISCONNECT
            }

        fun makeIntentForPause(context: Context): Intent =
            Intent(context, Ikev2VpnService::class.java).apply {
                action = ACTION_PAUSE
            }
    }


    private lateinit var notification: Notification

    private val localCertificateStore = LocalCertificateStore()

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

    private var ikev2ConnectConfig: Ikev2ConnectConfig? = null


    override fun onCreate() {
        super.onCreate()

        notification = notificationHelper.buildNotification(
            ConnectionStatus.Connecting("Init Ikev2"),
            getString(R.string.vpn_status_connecting),
        )

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            getServiceType()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY

        when (intent.action) {
            ACTION_CONNECT -> {
                getConnectionManager().setConnectionStatus(ConnectionStatus.Connecting("Connecting Ikev2"))

                splitMode = intent.getIntExtra(EXTRA_SPLIT_MODE, 0)
                allowLanConnection = intent.getBooleanExtra(EXTRA_ALLOW_LAN, false)
                dnsServerIp = intent.getStringExtra(EXTRA_DNS_SERVER_IP)
                timeToShutDown =
                    intent.getLongExtra(EXTRA_TURN_OFF_DELAY_IN_MILLIS, 0L)

                intent.getParcelableExtraCompat<Ikev2ConnectConfig>(
                    EXTRA_CONFIG
                )?.let {
                    ikev2ConnectConfig = it
                }

                logger.i("Ikev2Config = $ikev2ConnectConfig")

                if (ikev2ConnectConfig != null) {
                    getConnectionManager().setConnectionStatus(
                        ConnectionStatus.Connecting(null)
                    )
                    notificationHelper.updateNotification(
                        ConnectionStatus.Connecting(null),
                        null
                    )
                    startIkev2(ikev2ConnectConfig!!)
                } else {
                    getConnectionManager().setConnectionStatus(
                        ConnectionStatus.Error("Ikev2 config is empty")
                    )
                }


            }

            ACTION_DISCONNECT -> {
                stopIkev2()
                getConnectionManager().setConnectionStatus(ConnectionStatus.Disconnected)
                stopSelf()
            }

            ACTION_PAUSE -> {
                stopIkev2()
                getConnectionManager().setConnectionStatus(ConnectionStatus.Paused)
            }
        }

        return START_STICKY
    }

    override fun getMainActivityClass(): Class<*> {
        return MainActivity::class.java
    }

    /**
     * Calls by Ikev2 Core, when connection state or error state change
     * */
    override fun buildNotification(publicVersion: Boolean): Notification {
        updateVpnStatus()
        return notification
    }

    private fun updateVpnStatus() {
        val coreErrorState = mService?.errorState
        val coreVpnState = mService?.state

        val actualState = when {
            coreErrorState != VpnStateService.ErrorState.NO_ERROR ->
                ConnectionStatus.Error(coreErrorState?.name)

            coreVpnState == VpnStateService.State.CONNECTING -> ConnectionStatus.Connecting("Start Ikev2 Core")
            coreVpnState == VpnStateService.State.CONNECTED -> ConnectionStatus.Connected
            else -> ConnectionStatus.Disconnected
        }

        if (actualState is ConnectionStatus.Connected) {
            setupShutdownTimer()
        }

        getConnectionManager().setConnectionStatus(actualState)
        notificationHelper.updateNotification(actualState, null)
    }

    override fun getNotificationID(): Int {
        return NOTIFICATION_ID
    }

    private fun startIkev2(ikev2ConnectConfig: Ikev2ConnectConfig) {
        localCertificateStore.dropCertificates()
        val vpnProfile = getVpnProfile(
            ikev2ConnectConfig.host,
            ikev2ConnectConfig.username,
            ikev2ConnectConfig.password,
            ikev2ConnectConfig.certificate
        )
        setNextProfile(vpnProfile)
    }

    private fun stopIkev2() {
        setNextProfile(null)
    }

    private fun getVpnProfile(
        host: String,
        username: String,
        password: String,
        certificate: String?
    ): VpnProfile {
        val vpnProfile = VpnProfile()
        val certificateAlias = storeCertificate(certificate)

        val splitTunnellingApps = getSplitTunnellingApps()
        val splitMode = (applicationContext as MyApp).vpnConnectionManager.splitMode
        val excludedIps = calculateAllowedIpsIkev2()

        vpnProfile.vpnType = VpnType.IKEV2_EAP
        vpnProfile.name = host
        vpnProfile.gateway = host

        vpnProfile.username = username
        vpnProfile.password = password
        vpnProfile.natKeepAlive = 10
        vpnProfile.certificateAlias = certificateAlias
        vpnProfile.selectedApps = packageName

        vpnProfile.selectedAppsHandling = VpnProfile.SelectedAppsHandling.entries[splitMode]

        vpnProfile.splitTunneling = VpnProfile.SPLIT_TUNNELING_BLOCK_IPV4
        vpnProfile.setSelectedApps(splitTunnellingApps)

        if (excludedIps.isNotEmpty()) {
            vpnProfile.excludedSubnets = excludedIps
        }

        vpnProfile.dnsServers = dnsServerIp

        return vpnProfile
    }

    /**
     * Stores String certificate as X509Certificate
     * @param certificate an X509Certificate read from file in String type
     * @return 'alias' if certificate successfully parsed stored, 'null' if error occurred*/
    private fun storeCertificate(certificate: String?): String? {
        try {
            certificate?.byteInputStream().use { inStream ->
                val certificateFactory = CertificateFactory.getInstance("X.509")
                val x509Certificate =
                    certificateFactory.generateCertificate(inStream) as X509Certificate

                val alias = localCertificateStore.addCertificate(x509Certificate)
                return alias
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    private fun setupShutdownTimer() {
        if (timeToShutDown == 0L) return
        shutdownTimer?.stopTimer()
        shutdownTimer = null

        shutdownTimer = Timer.countdown(timeToShutDown, 1000L)

        coroutineScope.launch {
            shutdownTimer?.currentMillisFlow?.collect {
                if (it == 1000L) {
                    stopIkev2()
                    stopSelf()
                }
            }
        }
        shutdownTimer?.startTimer(coroutineScope)
    }


    private fun calculateAllowedIpsIkev2(): String {

        val excludedIps = StringBuilder()

        if (allowLanConnection) {
            splitIPs.addAll(NetworkUtils.getLocalNetworks(this))
        }

//        val allowedIps = mutableListOf<String>()
        val ipRangeSet: IPRangeSet = IPRangeSet.fromString(VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_4)

        if (splitIPs.isEmpty()) {
            excludedIps.append(VpnConstants.DEFAULT_ALLOWED_IP_PREFIX_4).append(" ")
            return excludedIps.toString()
        }

        for (ip in splitIPs) {
            try {
                excludedIps.append(ip).append(" ")
//                ipRangeSet.remove(IPRange(ip))
            } catch (e: UnknownHostException) {
                if (BuildConfig.DEBUG) logger.e(e.stackTraceToString())
            }
        }
        return excludedIps.toString()
    }

    private fun getSplitTunnellingApps(): SortedSet<String> {
        val excludedApps = ((applicationContext as MyApp)
            .vpnConnectionManager
            .excludedPackageIds + AppPackageId(packageName)).toMutableList()

        val mode = (applicationContext as MyApp).vpnConnectionManager.splitMode

        logger.d("Split mode = $mode excluded apps: $excludedApps")
        return excludedApps.map { it.packageId }.toSortedSet()
    }


    override fun onRevoke() {
        stopIkev2()
        stopSelf()
    }

    override fun onDestroy() {
        logger.d("onDestroy")
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


    private fun getConnectionManager(): VpnConnectionManager =
        (applicationContext as MyApp).let {
            return it.vpnConnectionManager
        }

    private fun getServiceType(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
    } else {
        0
    }
}
