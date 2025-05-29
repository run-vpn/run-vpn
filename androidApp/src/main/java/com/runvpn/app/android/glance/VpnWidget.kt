package com.runvpn.app.android.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import co.touchlab.kermit.Logger
import com.runvpn.app.SharedSDK
import com.runvpn.app.android.MainActivity
import com.runvpn.app.android.MyApp
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.getIconResId
import com.runvpn.app.android.ext.isNotificationsAllowed
import com.runvpn.app.core.ui.colorStrokeSeparator
import com.runvpn.app.core.ui.greenColor
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.toggleVpnOffTextColor
import com.runvpn.app.data.connection.ConnectionStatus
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.connection.domain.ConnectToVpnUseCase
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.servers.domain.usecases.SetCurrentServerUseCase

class VpnWidget : GlanceAppWidget() {

    companion object {
        private val logger = Logger.withTag("VpnWidget")
    }

    private var connectionManager: VpnConnectionManager? = null
    private var serverToConnect: Server? = null
    private var serversList: List<Server> = listOf()
    private var connectToVpnUseCase: ConnectToVpnUseCase? = null
    private var setCurrentServerUseCase: SetCurrentServerUseCase? = null

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        connectionManager = (context as? MyApp)?.vpnConnectionManager

        with(SharedSDK.koinApplication.koin) {
            serverToConnect = get<ServersRepository>().run {
                serversList = allServers.value
                    .filter { it.country != null }
                    .distinctBy { it.country }
                    .take(3)

                serversList.firstOrNull()
            }

            connectToVpnUseCase = get<ConnectToVpnUseCase>()
            setCurrentServerUseCase = get<SetCurrentServerUseCase>()
        }

        provideContent {
            WidgetContent()
        }
    }

    @Composable
    @GlanceComposable
    private fun WidgetContent() {
        val context = LocalContext.current
        val connectionStatus = connectionManager?.connectionStatus?.collectAsState()
        val size = LocalSize.current

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .clickable(actionStartActivity(MainActivity::class.java))
        ) {
            connectionStatus?.value?.let {
                serverToConnect?.let { server ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            provider = ImageProvider(
                                server.getIconResId(context)
                            ),
                            contentDescription = "",
                            modifier = GlanceModifier
                                .size(36.dp, 24.dp)
                                .cornerRadius(6.dp)
                        )
                        Spacer(modifier = GlanceModifier.width(6.dp))
                        Text(
                            text = server.country ?: server.name ?: server.host,
                            style = TextDefaults.defaultTextStyle.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                if (size.height > 120.dp) {
                    Spacer(modifier = GlanceModifier.height(8.dp))

                    LazyColumn(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                    ) {
                        items(serversList) { server ->
                            Column {
                                ServerItem(
                                    server = server,
                                    onClick = {
                                        onServerClicked(context, server)
                                    }
                                )
                                Spacer(modifier = GlanceModifier.height(4.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                Button(
                    text = context.getString(it.toButtonText()),
                    onClick = { onActionButtonClicked(context) },
                    enabled = it !is ConnectionStatus.Connecting,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorProvider(it.toButtonColor())
                    )
                )
            }
        }
    }

    @Composable
    private fun ServerItem(server: Server, onClick: () -> Unit) {
        val context = LocalContext.current

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(colorStrokeSeparator)
                .padding(horizontal = 6.dp, vertical = 6.dp)
                .cornerRadius(6.dp)
                .clickable { onClick() }
        ) {
            Image(
                provider = ImageProvider(
                    server.getIconResId(context)
                ),
                contentDescription = "",
                modifier = GlanceModifier
                    .size(32.dp, 20.dp)
                    .cornerRadius(6.dp)
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
                text = server.title,
                style = TextDefaults.defaultTextStyle.copy(
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
        }
    }

    private val Server.title: String
        get() {
            var t = country ?: name ?: host
            if (city != null) {
                t += ", $city"
            }
            return t
        }

    private fun ConnectionStatus.toButtonText(): Int {
        return when (this) {
            is ConnectionStatus.Connected -> R.string.vpn_action_disconnect
            is ConnectionStatus.Disconnected -> R.string.vpn_action_connect
            is ConnectionStatus.Paused -> R.string.vpn_action_connect
            is ConnectionStatus.Error -> R.string.vpn_action_connect
            is ConnectionStatus.Connecting -> R.string.vpn_status_connecting
            is ConnectionStatus.Idle->R.string.idle
        }
    }

    private fun ConnectionStatus.toButtonColor(): Color {
        return when (this) {
            is ConnectionStatus.Connected -> greenColor
            is ConnectionStatus.Connecting -> toggleVpnOffTextColor
            is ConnectionStatus.Error -> textErrorColor
            else -> primaryColor
        }
    }

    private fun onServerClicked(context: Context, server: Server) {
        if (!context.isNotificationsAllowed()) return
        serverToConnect = server

        connectToVpnUseCase?.invoke(server)
        setCurrentServerUseCase?.invoke(server.uuid)
    }

    private fun onActionButtonClicked(context: Context) {
        if (!context.isNotificationsAllowed()) return
        val status = connectionManager?.connectionStatus?.value ?: return
        when (status) {
            is ConnectionStatus.Connected -> connectionManager?.disconnect()

            is ConnectionStatus.Disconnected,
            is ConnectionStatus.Error,
            is ConnectionStatus.Paused -> {
                serverToConnect?.let {
                    connectToVpnUseCase?.invoke(it)
                    setCurrentServerUseCase?.invoke(it.uuid)
                }
            }

            else -> {}
        }
    }
}
