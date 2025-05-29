package de.blinkt.openvpn

import android.app.Notification

interface OpenVpnNotificationBuilder {
    val notificationId: Int

    fun buildNotification(): Notification
}
