package com.runvpn.app.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.retainedComponent
import com.runvpn.app.SharedSDK
import com.runvpn.app.android.ext.isNotificationsAllowed
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.connection.domain.ConnectToVpnUseCase
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import com.runvpn.app.presentation.root.RootComponent
import com.runvpn.app.presentation.root.createRootComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    companion object {
        private val logger = Logger.withTag("MainActivity")
    }

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

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootComponent: RootComponent = retainedComponent { componentContext ->
            SharedSDK.getDecomposeComponents().createRootComponent(componentContext)
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            )
        )

        checkAutoConnect()

        setContent {
            RootView(
                component = rootComponent,
                modifier = Modifier
                    .background(Color.White)
                    .safeDrawingPadding()
            )
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val currentLocale = appSettingsRepository.userSelectedLocale
        logger.d("currentLocale is ${currentLocale?.isoCode}")
        if (newBase == null || currentLocale == null) {
            super.attachBaseContext(newBase)
            return
        }

        val newConfiguration = newBase.resources.configuration
        logger.d("Locale is ${currentLocale.isoCode}")
        newConfiguration.setLocale(Locale(currentLocale.isoCode))

        val wrappedContext = newBase.createConfigurationContext(newConfiguration)

        super.attachBaseContext(wrappedContext)
    }

    private fun checkAutoConnect() {
        if (!this.isNotificationsAllowed()) return
        if (connectionManager.connectionStatus.value.isDisconnected() && settingsRepository.autoConnect) {
            if (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY !=
                Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
            ) {
                coroutineScope.launch {
                    serverRepository.currentServer.collectLatest { currentServer ->
                        currentServer?.let {
                            connectToVpnService(it)
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    RunVpnTheme {
        Scaffold {
        }
    }
}

