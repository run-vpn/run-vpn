package com.runvpn.app

import co.touchlab.kermit.Logger
import com.runvpn.app.analytics.SentryReportProvider
import com.runvpn.app.core.analytics.analytics.AnalyticsProvider
import com.runvpn.app.core.analytics.analytics.AnalyticsService
import com.runvpn.app.core.analytics.analytics.impl.DefaultAnalyticsService
import com.runvpn.app.core.analytics.analytics.impl.LoggerAnalyticsProvider
import com.runvpn.app.core.analytics.reports.ReportProvider
import com.runvpn.app.core.analytics.reports.ReportService
import com.runvpn.app.core.analytics.reports.impl.DefaultReportService
import com.runvpn.app.core.analytics.reports.impl.LoggerReportProvider
import com.runvpn.app.core.common.coreCommonModule
import com.runvpn.app.core.di.appInternalDirectoryQualifier
import com.runvpn.app.core.di.appPackageNameQualifier
import com.runvpn.app.core.di.authorizedKtorfitQualifier
import com.runvpn.app.core.network.NetworkApiFactory
import com.runvpn.app.core.network.NetworkStatus
import com.runvpn.app.core.networkModule
import com.runvpn.app.data.connection.ConnectionStatisticsManager
import com.runvpn.app.data.connection.VpnConnectionFactory
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.models.AppVersion
import com.runvpn.app.di.allModules
import com.runvpn.app.di.platformModule
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun initializeSentry(
    deviceInfo: DeviceInfo,
    appVersion: AppVersion,
    environment: String,
    isDebug: Boolean
)

//@ThreadLocal
object SharedSDK {

    lateinit var koinApplication: KoinApplication
        private set

    val vpnConnectionManager: VpnConnectionManager
        get() = koinApplication.koin.get()

    /**
     * Initialize SDK
     */
    fun initializeSdk(
        backendUrl: String,
        environment: String,
        localeCode: String,
        deviceInfo: DeviceInfo,
        appVersion: AppVersion,
        isDebug: Boolean,
        appInternalDirectory: String,
        connectionStatisticsManager: ConnectionStatisticsManager,
        analyticsProviders: List<AnalyticsProvider>,
        reportProviders: List<ReportProvider>,
        vpnConnectionFactory: VpnConnectionFactory? = null,
        additionalModules: List<Module>? = null
    ) {
        initializeSentry(
            deviceInfo = deviceInfo,
            appVersion = appVersion,
            environment = environment,
            isDebug = isDebug
        )

        val aProviders: List<AnalyticsProvider> = if (isDebug) {
            listOf(LoggerAnalyticsProvider()) + analyticsProviders
        } else analyticsProviders
        val rProviders: List<ReportProvider> = if (isDebug) {
            listOf(LoggerReportProvider()) + reportProviders
        } else reportProviders + listOf(SentryReportProvider())

        val defaultReportMetadata = mapOf(
            "app_version" to deviceInfo.software.versionName
        )

        val sdkModule = module {
            single<AnalyticsService> { DefaultAnalyticsService(aProviders) }
            single<ReportService> { DefaultReportService(rProviders, defaultReportMetadata) }

            if (vpnConnectionFactory != null) {
                single<VpnConnectionFactory> { vpnConnectionFactory }
            }

            single { deviceInfo }
            single(appPackageNameQualifier) { deviceInfo.applicationPackageName }
            single(appInternalDirectoryQualifier) { appInternalDirectory }
            single<AppVersion> { appVersion }

            single { connectionStatisticsManager }

            single(authorizedKtorfitQualifier) {
                get<NetworkApiFactory>().createAuthorizedKtorfit(
                    getTokenDelegate = {
                        get<AppSettingsRepository>().appToken ?: ""
                    }
                )
            }
        }

        val diModules = mutableListOf(
            sdkModule,
            platformModule,
            coreCommonModule,
            networkModule(
                baseUrl = backendUrl,
                appVersionCode = appVersion.versionCode.toInt(),
                appVersionName = appVersion.versionName,
                platformName = appVersion.platform,
                ::onConnectionStatusChanged
            )
        ).apply {
            addAll(allModules)
            if (additionalModules != null) {
                addAll(additionalModules)
            }
        }

        koinApplication = startKoin {
            modules(diModules)
            koin.declare(DecomposeComponentFactory(koin))
        }

        checkLanguageAndSet(localeCode)
    }

    private fun checkLanguageAndSet(localeCode: String) {
        val repo = koinApplication.koin.get<AppSettingsRepository>()
        val languageByLocale = repo.getAvailableLanguages().firstOrNull { it.isoCode == localeCode }

        if (repo.userSelectedLocale == null && languageByLocale != null) {
            repo.userSelectedLocale = languageByLocale
        }
    }

    private fun onConnectionStatusChanged(networkStatus: NetworkStatus) {
        val appSettingsRepository = koinApplication.koin.get<AppSettingsRepository>()
        Logger.i("OnConnectionStatus Changed $networkStatus")
        appSettingsRepository.setIsDomainReachable(networkStatus == NetworkStatus.AVAILABLE)
    }

    fun getDecomposeComponents(): DecomposeComponentFactory = koinApplication.koin.get()
}

