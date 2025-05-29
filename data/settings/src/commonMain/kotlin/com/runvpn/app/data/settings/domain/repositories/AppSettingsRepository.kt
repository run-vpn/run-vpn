package com.runvpn.app.data.settings.domain.repositories

import com.runvpn.app.data.settings.domain.AppUseMode
import com.runvpn.app.data.settings.domain.Language
import com.runvpn.app.data.settings.domain.SplitTunnelingApplication
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.data.settings.models.LocalXrayConfig
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {

    companion object {
        internal const val KEY_LAST_SHOW_REVIEW_DIALOG_TIME = "key_last_show_review_dialog_time"
        internal const val KEY_DEVICE_UUID = "key_app_uuid"
        internal const val KEY_APP_TOKEN = "key_app_token"
        internal const val KEY_APP_ANONYMOUS_TOKEN = "key_app_anonymous_token"
        internal const val KEY_EMAIL = "key_email"
        internal const val KEY_LOCALE = "key_locale"
        internal const val KEY_LAST_TIMER_VALUE = "key_last_timer_value"
        internal const val KEY_XRAY_CONFIG = "key_xray_config"
        internal const val KEY_EXCLUDED_PACKAGES = "key_excluded_packages"
        internal const val KEY_CURRENT_SERVER_ID = "key_current_server_id"
        internal const val KEY_SPLIT_MODE = "key_split_mode"
        internal const val KEY_ALLOW_LAN_CONNECTION = "key_allow_lan_connection"
        internal const val KEY_TIME_TO_SHUTDOWN_IN_MILLIS = "key_to_shutdown_in_millis"
        internal const val KEY_SELECTED_DNS_SERVER_ID = "key_selected_dns_server_id"
        internal const val KEY_SELECTED_DNS_SERVER_IP = "key_selected_dns_server_ip"

        internal const val KEY_IS_DOMAIN_REACHABLE = "key_domain_reachable"

        internal const val KEY_APP_USAGE_MODE = "key_app_usage_mode"
        internal const val KEY_PRIVACY_POLICY_ACCEPTED = "key_privacy_policy_accepted"

        internal const val KEY_UPDATE_DOWNLOADED = "key_update_downloaded"

        const val APP_UUID_DEBUG = "3c7e032b-904e-4818-b800-be5a5a4a2944"
        const val APP_UUID_RELEASE = "0f84cd32-373c-46c1-b2fc-d79826633295"
    }

    val deviceUuidFlow: Flow<String?>
    var deviceUuid: String?

    var appToken: String?
    var appAnonymousToken: String?

    var email: String?


    var lastShowReviewDialog: Long
    var userSelectedLocale: Language
    var lastTimerValue: Long?
    var updateDownloaded: Boolean?
    var timeToShutdown: Long

    var xrayConfig: LocalXrayConfig?

    val excludedPackageIds: List<SplitTunnelingApplication>

    var splitMode: Int
    var selectedDnsServerId: Long?
    var selectedDnsServerIP: String

    var allowLanConnection: Boolean

    val isDomainReachable: Flow<Boolean>

    var appUsageMode: AppUseMode
    var isPrivacyPolicyAccepted: Boolean

    fun setIsDomainReachable(isReachable: Boolean)
    fun getAvailableLanguages(): List<Language>
    fun getSuggestedServersModes(): List<SuggestedServersMode>

    fun addExcludedApplication(app: SplitTunnelingApplication)
    fun removeExcludedPackageId(app: SplitTunnelingApplication)
}

