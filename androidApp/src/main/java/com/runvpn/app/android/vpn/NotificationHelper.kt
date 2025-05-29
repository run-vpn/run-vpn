package com.runvpn.app.android.vpn

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.runvpn.app.android.MainActivity
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.byteToSizeRounded
import com.runvpn.app.android.ext.isNotificationsAllowed
import com.runvpn.app.android.ext.toTimeLocalized
import com.runvpn.app.android.ext.toTimerFormat
import com.runvpn.app.core.common.presentation.Notification.ChannelIdDisconnected
import com.runvpn.app.core.common.presentation.Notification.ChannelIdProcessing
import com.runvpn.app.core.common.presentation.Notification.NOTIFICATION_ID
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.ConnectionStatisticsManager


class NotificationHelper(
    private val appContext: Context,
) {

    private val notificationManager = NotificationManagerCompat.from(appContext)

    private val contentIntent: PendingIntent
        get() {
            val intent = Intent(appContext, MainActivity::class.java)
            return PendingIntent.getActivity(
                appContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val nmc = NotificationManagerCompat.from(appContext)

        nmc.createNotificationChannel(
            makeChannel(
                ChannelIdProcessing,
                appContext.getString(R.string.notification_channel_name),
                appContext.getString(R.string.notification_channel_desc),
                NotificationManagerCompat.IMPORTANCE_LOW
            )
        )

        nmc.createNotificationChannel(
            makeChannel(
                ChannelIdDisconnected,
                appContext.getString(R.string.vpn_status_disconnected),
                appContext.getString(R.string.ntf_disconnected_channel_desc),
                NotificationManagerCompat.IMPORTANCE_LOW
            )
        )
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun makeChannel(
        channelId: String,
        channelName: String,
        channelDesc: String,
        importance: Int
    ): NotificationChannelCompat {
        return NotificationChannelCompat.Builder(channelId, importance).run {
            setName(channelName)
            setDescription(channelDesc)

            setLightsEnabled(false)
            setVibrationEnabled(false)
            setVibrationPattern(null)
            setShowBadge(false)
            setSound(null, null)

            return@run build()
        }
    }

    fun updateNotification(
        connectionStatus: ConnectionStatus,
        stats: ConnectionStatisticsManager.ConnectionStats?
    ) {
        if (!appContext.isNotificationsAllowed()) return

        if (connectionStatus is ConnectionStatus.Disconnected){
            notificationManager.cancel(NOTIFICATION_ID)
            return
        }

        val contentText = getNotificationContentTextByStatus(connectionStatus, stats)
        val titleText = getNotificationTitleTextByStatus(connectionStatus, stats)

        val notification = buildNotification(connectionStatus, contentText, titleText)

        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun buildNotification(
        connectionStatus: ConnectionStatus,
        contentText: String,
        titleText: String = connectionStatus.getTitle(),
    ): Notification {
        return NotificationCompat.Builder(
            appContext,
            ChannelIdProcessing
        ).run {
            setVibrate(null)

            setOngoing(true)
            setSmallIcon(R.drawable.ic_notification_icon)
            color = 0x196EFF

            setContentTitle(titleText)
            setContentText(contentText)

            setCategory(NotificationCompat.CATEGORY_SERVICE)
            setContentIntent(contentIntent)
            setChannelId(ChannelIdProcessing)

            // For Android 7.1 and lower
            priority = NotificationCompat.PRIORITY_LOW

            val mainActionIntent = makeMainActionIntent(connectionStatus)

            if (connectionStatus !is ConnectionStatus.Connecting) {
                mainActionIntent?.let {
                    addAction(0, connectionStatus.toNotificationActionTitle(), it)
                }
            }

            if (connectionStatus is ConnectionStatus.Connected) {
                addAction(
                    0,
                    appContext.getString(R.string.vpn_action_pause),
                    makePauseAction()
                )
            }

            //Custom View implementing
//            val remoteViews = RemoteViews(appContext.packageName, R.layout.notification_layout)
//            remoteViews.setImageViewResource(R.id.image, FlagKit.getResId(appContext, "RU"))
//            remoteViews.setTextViewText(R.id.title, "Title")
//            remoteViews.setOnClickPendingIntent(R.id.tvDisconnect, mainActionIntent)
//            remoteViews.setOnClickPendingIntent(R.id.tvPause, makePauseAction())
//            remoteViews.setTextViewText(R.id.text, "text")
//            setCustomContentView(remoteViews)

            return@run build()
        }
    }


    private fun getNotificationTitleTextByStatus(
        connectionStatus: ConnectionStatus,
        connectionStats: ConnectionStatisticsManager.ConnectionStats?
    ): String {
        return when (connectionStats?.connectionStatsTimerType) {
            ConnectionStatisticsManager.ConnectionStatsTimerType.Countdown -> {
                appContext.getString(
                    R.string.vpn_status_disconnects_in_,
                    connectionStats.connectionTime.toTimerFormat()
                )
            }

            else -> connectionStatus.getTitle()
        }
    }

    private fun getNotificationContentTextByStatus(
        connectionStatus: ConnectionStatus,
        connectionStats: ConnectionStatisticsManager.ConnectionStats?
    ): String {
        return when (connectionStatus) {
            is ConnectionStatus.Connected -> {
                if (connectionStats != null) {
                    getStatisticsText(connectionStats)
                } else ""
            }

            ConnectionStatus.Paused -> {
                appContext.getString(R.string.vpn_notification_status_pause_desc)
            }

            else -> connectionStatus.toLocalizedStatus()
        }
    }


    private fun getStatisticsText(stats: ConnectionStatisticsManager.ConnectionStats) =
        when (stats.connectionStatsTimerType) {
            ConnectionStatisticsManager.ConnectionStatsTimerType.Countdown -> {
                appContext.getString(R.string.vpn_status_auto_disconnect_activated)
            }

            ConnectionStatisticsManager.ConnectionStatsTimerType.Stopwatch -> {
                (stats.totalDownloaded + stats.totalUploaded).byteToSizeRounded() + " | " +
                        " ↓ " + stats.downloadSpeedPerSecond.byteToSizeRounded() + "/s" +
                        " ↑ " + stats.uploadSpeedPerSecond.byteToSizeRounded() + "/s"
            }
        }

    private fun makeMainActionIntent(
        connectionStatus: ConnectionStatus,
    ): PendingIntent? {
        val intentAction = when (connectionStatus) {
            is ConnectionStatus.Connected -> VpnCommandBroadcastReceiver.ACTION_DISCONNECT
            is ConnectionStatus.Connecting -> VpnCommandBroadcastReceiver.ACTION_DISCONNECT
            ConnectionStatus.Disconnected -> VpnCommandBroadcastReceiver.ACTION_CONNECT
            is ConnectionStatus.Error -> VpnCommandBroadcastReceiver.ACTION_CONNECT
            ConnectionStatus.Paused -> VpnCommandBroadcastReceiver.ACTION_CONNECT
            is ConnectionStatus.Idle -> VpnCommandBroadcastReceiver.ACTION_CONNECT
        }

        val someIntent = Intent(appContext, VpnCommandBroadcastReceiver::class.java)
            .apply {
                action = intentAction
            }
            .run {
                PendingIntent.getBroadcast(
                    appContext,
                    0,
                    this,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

        return someIntent
    }

    private fun makePauseAction(): PendingIntent {

        val intent = Intent(appContext, VpnCommandBroadcastReceiver::class.java)
            .apply {
                action = VpnCommandBroadcastReceiver.ACTION_PAUSE
            }
            .run {
                PendingIntent.getBroadcast(
                    appContext,
                    0,
                    this,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

        return intent
    }

    private fun ConnectionStatus.getTitle(): String {
        return when (this) {
            is ConnectionStatus.Disconnected -> appContext.getString(R.string.vpn_notification_status_disconnected)
            is ConnectionStatus.Connected -> appContext.getString(R.string.vpn_notification_status_connected)
            is ConnectionStatus.Connecting -> appContext.getString(R.string.vpn_notification_status_connecting)
            is ConnectionStatus.Error -> appContext.getString(R.string.vpn_notification_status_error)
            ConnectionStatus.Paused -> appContext.getString(R.string.vpn_notification_status_pause)
            is ConnectionStatus.Idle -> {
                when (this.reason) {
                    ConnectionStatus.IdleReason.AUTO_DISCONNECT ->
                        appContext.getString(
                            R.string.vpn_status_idle_reason_autodisconnect_title,
                            this.idleTime.toTimeLocalized()
                        )
                }
            }
        }
    }

    private fun ConnectionStatus.toLocalizedStatus(): String {
        return when (this) {
            is ConnectionStatus.Connected -> appContext.getString(R.string.vpn_status_connected)
            is ConnectionStatus.Connecting -> appContext.getString(R.string.vpn_status_connecting)
            ConnectionStatus.Disconnected -> appContext.getString(R.string.vpn_status_disconnected)
            is ConnectionStatus.Error -> appContext.getString(R.string.vpn_status_error)
            ConnectionStatus.Paused -> appContext.getString(R.string.vpn_notification_status_pause_desc)
            is ConnectionStatus.Idle -> when (this.reason) {
                ConnectionStatus.IdleReason.AUTO_DISCONNECT ->
                    appContext.getString(R.string.vpn_status_idle_reason_autodisconnect_message)
            }
        }
    }

    private fun ConnectionStatus.toNotificationActionTitle() = when (this) {
        is ConnectionStatus.Connected -> appContext.getString(R.string.vpn_action_disconnect)
        is ConnectionStatus.Connecting -> appContext.getString(R.string.vpn_action_disconnect)
        ConnectionStatus.Disconnected -> appContext.getString(R.string.vpn_action_connect)
        is ConnectionStatus.Error -> appContext.getString(R.string.vpn_action_retry)
        ConnectionStatus.Paused -> appContext.getString(R.string.vpn_action_connect)
        is ConnectionStatus.Idle -> appContext.getString(R.string.connect)
    }
}

