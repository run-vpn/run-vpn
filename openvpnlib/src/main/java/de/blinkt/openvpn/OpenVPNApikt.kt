package de.blinkt.openvpn

import android.content.Context
import android.content.Intent
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import com.app.openvpnlib.R
import com.runvpn.app.SharedSDK.koinApplication
import com.runvpn.app.data.connection.VpnConnectionManager
import de.blinkt.openvpn.core.ConfigParser
import de.blinkt.openvpn.core.ConfigParser.ConfigParseError
import de.blinkt.openvpn.core.ConnectionStatus
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import de.blinkt.openvpn.core.VPNLaunchHelper
import java.io.IOException
import java.io.StringReader

object OpenVPNApikt {


    private const val TAG = "OpenVpnApikt"

    var notificationBuilder: OpenVpnNotificationBuilder? = null
        private set

    @Throws(RemoteException::class)
    fun startVpn(
        context: Context,
        inlineConfig: String?,
        sCountry: String?,
        userName: String?,
        pw: String?,
        notificationBuilder: OpenVpnNotificationBuilder,
        excludedAppIds: List<String>,
        splitMode: Int,
        allowLanConnection: Boolean,
        dnsServerIp: String,
        timeToShutdownInMillis: Long?
    ) {
        this.notificationBuilder = notificationBuilder

        Log.e(TAG, "startVpn: ")
        if (TextUtils.isEmpty(inlineConfig)) throw RemoteException("config is empty")
        startVpnInternal(
            context,
            inlineConfig,
            sCountry,
            userName,
            pw,
            excludedAppIds,
            splitMode,
            allowLanConnection,
            dnsServerIp,
            timeToShutdownInMillis
        )
    }

    fun stopVpn(context: Context) {
        Log.e(TAG, "startVpn: ")
        val disconnect = Intent(context, OpenVPNService::class.java)
        disconnect.setAction(OpenVPNService.DISCONNECT_VPN)
        context.startService(disconnect)
    }

    fun pauseVpn(context: Context) {
        val intent = Intent(context, OpenVPNService::class.java).apply {
            action = OpenVPNService.ACTION_PAUSE_VPN
        }
        context.startService(intent)
    }

    @JvmStatic
    fun setStatus(connectionStatus: ConnectionStatus, action: String? = null) {
        Log.d(TAG, "action is: $action")
        Log.d(TAG, "setStatus: $connectionStatus")
        val status = when (connectionStatus) {
            ConnectionStatus.LEVEL_START -> {
                com.runvpn.app.data.connection.ConnectionStatus.Connecting("Start")
            }

            //todo: can add message here
            ConnectionStatus.LEVEL_CONNECTED -> {
                com.runvpn.app.data.connection.ConnectionStatus.Connected
            }

            ConnectionStatus.LEVEL_NONETWORK -> {
                com.runvpn.app.data.connection.ConnectionStatus.Error("No Network")
            }

            ConnectionStatus.LEVEL_CONNECTING_NO_SERVER_REPLY_YET -> {
                com.runvpn.app.data.connection.ConnectionStatus.Connecting("Waiting for server reply...")
            }

            ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED -> {
                com.runvpn.app.data.connection.ConnectionStatus.Connecting("Server replied")
            }

            ConnectionStatus.LEVEL_AUTH_FAILED -> {
                com.runvpn.app.data.connection.ConnectionStatus.Error("Auth filed")
            }

            ConnectionStatus.LEVEL_NOTCONNECTED -> {
                com.runvpn.app.data.connection.ConnectionStatus.Disconnected
            }

            ConnectionStatus.LEVEL_VPNPAUSED -> {
                com.runvpn.app.data.connection.ConnectionStatus.Paused
            }

            else -> {
                com.runvpn.app.data.connection.ConnectionStatus.Error("$connectionStatus")
            }

        }

        koinApplication.koin.get<VpnConnectionManager>().setConnectionStatus(status)

    }


    private fun startVpnInternal(
        context: Context,
        inlineConfig: String?,
        sCountry: String?,
        userName: String?,
        pw: String?,
        excludedAppIds: List<String>,
        splitMode: Int,
        allowLanConnection: Boolean,
        dnsServerIp: String,
        timeToShutdownInMillis: Long?
    ) {
        val cp = ConfigParser()
        Log.e(TAG, "startVpnInternal: ")
        try {
            cp.parseConfig(StringReader(inlineConfig))
            val vp = cp.convertProfile() // Analysis.ovpn
            Log.d(TAG, "startVpnInternal: ==============$cp\n$vp")
            vp.mName = sCountry
            if (vp.checkProfile(context) != R.string.no_error_found) {
                throw RemoteException(context.getString(vp.checkProfile(context)))
            }
            val needpw = vp.needUserPWInput(vp.mUsername, vp.mPassword)
            Log.d(TAG, "startVpnInternal: $needpw")
            vp.mProfileCreator = context.packageName
            vp.mUsername = userName
            vp.mPassword = pw
            ProfileManager.setTemporaryProfile(context, vp)
            VPNLaunchHelper.startOpenVpn(
                vp,
                context,
                excludedAppIds,
                splitMode,
                dnsServerIp,
                allowLanConnection,
                timeToShutdownInMillis ?: 0L
            )
        } catch (e: IOException) {
            throw RemoteException(e.message)
        } catch (e: ConfigParseError) {
            throw RemoteException(e.message)
        }
    }
}

