package com.runvpn.app.data.settings.data

import com.runvpn.app.data.settings.domain.AppUseMode
import com.runvpn.app.data.settings.domain.Language
import com.runvpn.app.data.settings.domain.SplitTunnelingApplication
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.models.LocalXrayConfig
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
internal class DefaultAppSettingsRepository(
    private val settings: Settings,
    private val observableSettings: ObservableSettings
) : AppSettingsRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val deviceUuidFlow: Flow<String?>
        get() = observableSettings
            .getStringFlow(AppSettingsRepository.KEY_DEVICE_UUID, "")
            .flatMapLatest { flowOf(if (it == "") null else it) }

    override var lastShowReviewDialog: Long
        get() = settings.getLong(AppSettingsRepository.KEY_LAST_SHOW_REVIEW_DIALOG_TIME, 0L)
        set(value) {
            settings[AppSettingsRepository.KEY_LAST_SHOW_REVIEW_DIALOG_TIME] = value
        }

    override var deviceUuid: String?
        get() = settings.getString(
            AppSettingsRepository.KEY_DEVICE_UUID,
            ""
        ) //"44c7271e-26ac-4012-b7de-19bb8c4d0284"//
        set(value) {
            settings[AppSettingsRepository.KEY_DEVICE_UUID] = value
        }

    override var appToken: String?
        get() = settings.getString(
            AppSettingsRepository.KEY_APP_TOKEN,
            ""
        ) //"Rny7yxKZAsq54AYTFGjijsBLb3kuzqUUAXQcSGiX447fa99d"//
        set(value) {
            settings[AppSettingsRepository.KEY_APP_TOKEN] = value
        }

    override var appAnonymousToken: String?
        get() = settings.getString(AppSettingsRepository.KEY_APP_ANONYMOUS_TOKEN, "")
        set(value) {
            settings[AppSettingsRepository.KEY_APP_ANONYMOUS_TOKEN] = value
        }

    override var email: String?
        get() = settings.getString(AppSettingsRepository.KEY_EMAIL, "")
        set(value) {
            settings[AppSettingsRepository.KEY_EMAIL] = value
        }
    override var userSelectedLocale: Language
        get() {
            return settings.decodeValueOrNull(
                Language.serializer(),
                AppSettingsRepository.KEY_LOCALE
            ) ?: getAvailableLanguages().first()
        }
        set(value) {
            settings.encodeValue(
                Language.serializer(),
                AppSettingsRepository.KEY_LOCALE,
                value
            )
        }
    override var lastTimerValue: Long?
        get() = settings.getLong(AppSettingsRepository.KEY_LAST_TIMER_VALUE, 0L).toNullIfZero()
        set(value) {
            settings[AppSettingsRepository.KEY_LAST_TIMER_VALUE] = value
        }

    override var updateDownloaded: Boolean?
        get() = settings.getBoolean(AppSettingsRepository.KEY_UPDATE_DOWNLOADED, false)
        set(value) {
            settings[AppSettingsRepository.KEY_UPDATE_DOWNLOADED] = value
        }
    override var timeToShutdown: Long
        get() = settings[AppSettingsRepository.KEY_TIME_TO_SHUTDOWN_IN_MILLIS, 0L]
        set(value) {
            settings[AppSettingsRepository.KEY_TIME_TO_SHUTDOWN_IN_MILLIS] = value
        }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    override var xrayConfig: LocalXrayConfig?
        get() = settings.decodeValueOrNull(
            LocalXrayConfig.serializer(),
            AppSettingsRepository.KEY_XRAY_CONFIG
        )
        set(value) {
            if (value == null) {
                settings.remove(key = AppSettingsRepository.KEY_XRAY_CONFIG)
            } else {
                settings.encodeValue(
                    serializer = LocalXrayConfig.serializer(),
                    key = AppSettingsRepository.KEY_XRAY_CONFIG,
                    value = value
                )
            }
        }

    override val excludedPackageIds: List<SplitTunnelingApplication>
        get() {
            val value = settings.decodeValueOrNull(
                ListSerializer(SplitTunnelingApplication.serializer()),
                AppSettingsRepository.KEY_EXCLUDED_PACKAGES
            )
            return value ?: listOf()
        }


    override var splitMode: Int
        get() = settings.getInt(AppSettingsRepository.KEY_SPLIT_MODE, 0)
        set(value) {
            settings[AppSettingsRepository.KEY_SPLIT_MODE] = value
        }

    override var selectedDnsServerId: Long?
        get() = settings.getLongOrNull(AppSettingsRepository.KEY_SELECTED_DNS_SERVER_ID)
        set(value) {
            settings[AppSettingsRepository.KEY_SELECTED_DNS_SERVER_ID] = value
        }

    override var selectedDnsServerIP: String
        get() = settings.getStringOrNull(AppSettingsRepository.KEY_SELECTED_DNS_SERVER_IP)
            ?: "1.1.1.1"
        set(value) {
            settings[AppSettingsRepository.KEY_SELECTED_DNS_SERVER_IP] = value
        }

    override var allowLanConnection: Boolean
        get() = settings.getBoolean(AppSettingsRepository.KEY_ALLOW_LAN_CONNECTION, false)
        set(value) {
            settings[AppSettingsRepository.KEY_ALLOW_LAN_CONNECTION] = value
        }
    override val isDomainReachable: Flow<Boolean>
        get() = observableSettings.getBooleanFlow(
            AppSettingsRepository.KEY_IS_DOMAIN_REACHABLE,
            true
        )


    override var appUsageMode: AppUseMode
        get() = AppUseMode.valueOf(
            settings.getString(
                AppSettingsRepository.KEY_APP_USAGE_MODE,
                AppUseMode.FREE.name
            )
        )
        set(value) {
            settings[AppSettingsRepository.KEY_APP_USAGE_MODE] = value.name
        }

    override var isPrivacyPolicyAccepted: Boolean
        get() = settings.getBoolean(AppSettingsRepository.KEY_PRIVACY_POLICY_ACCEPTED, true) // TODO! Warning! set false to enable Privacy Dialog. Welcome Dialog
        set(value) {
            settings[AppSettingsRepository.KEY_PRIVACY_POLICY_ACCEPTED] = value
        }

    override fun setIsDomainReachable(isReachable: Boolean) {
        observableSettings.putBoolean(
            AppSettingsRepository.KEY_IS_DOMAIN_REACHABLE,
            isReachable
        )
    }

    override fun getAvailableLanguages(): List<Language> {
        return listOf(
            Language(
                isoCode = "ru",
                flagIsoCode = "ru",
                language = "Русский",
                languageInEnglish = "Russian"
            ),
            Language(
                isoCode = "en",
                flagIsoCode = "us",
                language = "English",
                languageInEnglish = "English"
            ),
        )
    }

    override fun getSuggestedServersModes(): List<SuggestedServersMode> {
        return SuggestedServersMode.entries
    }

    override fun addExcludedApplication(app: SplitTunnelingApplication) {
        val curr = excludedPackageIds.toMutableList()
        curr.add(app)

        settings.encodeValue(
            ListSerializer(SplitTunnelingApplication.serializer()),
            AppSettingsRepository.KEY_EXCLUDED_PACKAGES,
            curr
        )
    }

    override fun removeExcludedPackageId(app: SplitTunnelingApplication) {
        val curr = excludedPackageIds.toMutableList()
        curr.remove(app)

        settings.encodeValue(
            ListSerializer(SplitTunnelingApplication.serializer()),
            AppSettingsRepository.KEY_EXCLUDED_PACKAGES,
            curr
        )
    }
}

private fun Long.toNullIfZero(): Long? {
    if (this == 0L) return null
    return this
}
