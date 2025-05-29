package com.runvpn.app.data.settings.data

import com.runvpn.app.data.settings.domain.AppTheme
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DefaultUserSettingsRepository(
    private val settings: Settings,
    private val observableSettings: ObservableSettings,
) : UserSettingsRepository {

    private var listener: ((AppTheme) -> Unit)? = null

    override var showFavouriteServers: Boolean
        get() = settings.getBoolean(UserSettingsRepository.KEY_SHOW_FAVOURITE_SERVER, true)
        set(value) {
            settings[UserSettingsRepository.KEY_SHOW_FAVOURITE_SERVER] = value
        }
    override var runAfterDeviceStart: Boolean
        get() = settings.getBoolean(UserSettingsRepository.KEY_AUTO_CONNECT_VPN, false)
        set(value) {
            settings[UserSettingsRepository.KEY_AUTO_CONNECT_VPN] = value
        }
    override var reconnectToNextServer: Boolean
        get() = settings.getBoolean(UserSettingsRepository.KEY_RECONNECT_TO_NEXT_SERVER, true)
        set(value) {
            settings[UserSettingsRepository.KEY_RECONNECT_TO_NEXT_SERVER] = value
        }
    override var autoConnect: Boolean
        get() = settings.getBoolean(UserSettingsRepository.KEY_AUTO_CONNECT, false)
        set(value) {
            settings[UserSettingsRepository.KEY_AUTO_CONNECT] = value
        }
    override var connectOnNetworkEnabled: Boolean
        get() = settings.getBoolean(UserSettingsRepository.KEY_CONNECT_ON_NETWORK_ENABLED, false)
        set(value) {
            settings[UserSettingsRepository.KEY_CONNECT_ON_NETWORK_ENABLED] = value
        }
    override var connectOnDeviceStartUp: Boolean
        get() = settings.getBoolean(UserSettingsRepository.KEY_CONNECT_ON_DEVICE_START_UP, false)
        set(value) {
            settings[UserSettingsRepository.KEY_CONNECT_ON_DEVICE_START_UP] = value
        }
    override var tactileFeedbackOnConnect: Boolean
        get() = settings.getBoolean(UserSettingsRepository.KEY_TACTILE_FEEDBACK_ON_CONNECT, true)
        set(value) {
            settings[UserSettingsRepository.KEY_TACTILE_FEEDBACK_ON_CONNECT] = value
        }

    override var theme: AppTheme
        get() = AppTheme.valueOf(
            settings.getString(
                UserSettingsRepository.KEY_THEME,
                AppTheme.LIGHT.name
            )
        )
        set(value) {
            this.listener?.invoke(value)
            settings[UserSettingsRepository.KEY_THEME] = value.name
        }

    @OptIn(ExperimentalSettingsApi::class)
    override val suggestedServersMode: Flow<SuggestedServersMode>
        get() = observableSettings.getStringFlow(
            UserSettingsRepository.KEY_SUGGESTED_SERVERS_MODE,
            SuggestedServersMode.DEFAULT.name
        ).map { SuggestedServersMode.valueOf(it) }

    override fun setSuggestedServersMode(mode: SuggestedServersMode) {
        observableSettings.putString(
            UserSettingsRepository.KEY_SUGGESTED_SERVERS_MODE,
            mode.name
        )
    }


    override fun setAppThemeListener(listener: (AppTheme) -> Unit) {
        this.listener = listener
    }
}
