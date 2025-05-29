package com.runvpn.app.data.settings.domain.repositories

import com.runvpn.app.data.settings.domain.AppTheme
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {

    companion object {
        internal const val KEY_SHOW_FAVOURITE_SERVER = "key_show_favourite_server"
        internal const val KEY_AUTO_CONNECT_VPN = "key_auto_connect_vpn"
        internal const val KEY_RECONNECT_TO_NEXT_SERVER = "key_reconnect_to_next_server"
        internal const val KEY_AUTO_CONNECT = "key_auto_connect"
        internal const val KEY_CONNECT_ON_NETWORK_ENABLED = "key_connect_on_network_enabled"
        internal const val KEY_CONNECT_ON_DEVICE_START_UP = "key_connect_on_device_start_up"
        internal const val KEY_TACTILE_FEEDBACK_ON_CONNECT = "key_tactile_feedback_on_connect"
        internal const val KEY_SUGGESTED_SERVERS_MODE = "key_suggested_servers_mode"
        internal const val KEY_THEME = "key_theme"
        internal const val KEY_LANGUAGE = "key_language"
        const val DEFAULT_LANGUAGE = "key_language"
    }

    var showFavouriteServers: Boolean
    var runAfterDeviceStart: Boolean
    var reconnectToNextServer: Boolean
    var autoConnect: Boolean
    var connectOnNetworkEnabled: Boolean
    var connectOnDeviceStartUp: Boolean
    var tactileFeedbackOnConnect: Boolean
    var theme: AppTheme

    val suggestedServersMode: Flow<SuggestedServersMode>

    fun setSuggestedServersMode(mode: SuggestedServersMode)

    fun setAppThemeListener(listener: (AppTheme) -> Unit)
}
