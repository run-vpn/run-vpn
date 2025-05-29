package com.runvpn.app.tea.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class RootChildConfig {
    @Serializable
    data object Main : RootChildConfig()

    @Serializable
    data object SubscriptionRoot: RootChildConfig()
    @Serializable
    data object BuySubscriptionConfig : RootChildConfig()

    @Serializable
    data object About : RootChildConfig()

    @Serializable
    data object Authorization : RootChildConfig()

    @Serializable
    data class AddServer(val serverToEdit: String?) : RootChildConfig()

    @Serializable
    data object MyServers : RootChildConfig()

    @Serializable
    data object SplitTunneling : RootChildConfig()

    @Serializable
    data object SupportMain : RootChildConfig()

    @Serializable
    data object Faq : RootChildConfig()

    @Serializable
    data object ReportProblem : RootChildConfig()

    @Serializable
    data object Feedback : RootChildConfig()

    @Serializable
    data class PrivacyPolicy(val hasBackStack: Boolean) : RootChildConfig()

    @Serializable
    data object ChooseDnsServer : RootChildConfig()

    @Serializable
    data object TrafficModule : RootChildConfig()

    @Serializable
    data object TrafficModuleLogs : RootChildConfig()

    @Serializable
    data class BalanceRefillConfig(val cost: Double = 0.0) : RootChildConfig()
}
