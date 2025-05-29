package com.runvpn.app.android.vpn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import com.runvpn.app.SharedSDK
import com.runvpn.app.android.ext.isNotificationsAllowed
import com.runvpn.app.data.connection.VpnConnectionManager

class VpnCommandBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_CONNECT = "action_connect"
        const val ACTION_DISCONNECT = "action_disconnect"
        const val ACTION_PAUSE = "action_pause"

        private val logger = Logger.withTag("VpnCommandBroadcast")
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (!context.isNotificationsAllowed()) return
        if (intent == null) return
        val connectionManager = SharedSDK.koinApplication.koin.get<VpnConnectionManager>()

        when (intent.action) {
            ACTION_CONNECT -> {
                connectionManager.connectLatest()
            }

            ACTION_DISCONNECT -> {
                connectionManager.disconnect()
            }

            ACTION_PAUSE -> {
                connectionManager.pause()
            }
        }
    }
}
