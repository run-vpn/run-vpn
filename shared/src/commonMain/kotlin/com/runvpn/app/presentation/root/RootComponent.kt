package com.runvpn.app.presentation.root

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.AppTheme
import com.runvpn.app.feature.authorization.AuthorizationComponent
import com.runvpn.app.feature.home.welcome.WelcomeComponent
import com.runvpn.app.feature.myservers.MyServersComponent
import com.runvpn.app.feature.serveradd.AddServerComponent
import com.runvpn.app.feature.settings.dns.ChooseDnsServerComponent
import com.runvpn.app.feature.settings.splittunneling.SplitTunnelingComponent
import com.runvpn.app.feature.settings.support.about.AboutComponent
import com.runvpn.app.feature.settings.support.faq.FaqComponent
import com.runvpn.app.feature.settings.support.feedback.FeedbackComponent
import com.runvpn.app.feature.settings.support.main.SupportComponent
import com.runvpn.app.feature.settings.support.reportproblem.ReportProblemComponent
import com.runvpn.app.feature.subscription.SubscriptionRootComponent
import com.runvpn.app.feature.subscription.balancerefill.BalanceRefillComponent
import com.runvpn.app.feature.subscription.buy.BuySubscriptionComponent
import com.runvpn.app.feature.trafficmodule.TrafficModuleComponent
import com.runvpn.app.presentation.main.MainComponent
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent
import com.runvpn.app.tea.message.presentation.MessageComponent
import kotlinx.coroutines.flow.Flow

interface RootComponent {

    val messageComponent: MessageComponent

    val childStack: Value<ChildStack<*, Child>>
    val dialog: Value<ChildSlot<*, SimpleDialogComponent>>

    val appTheme: Value<AppTheme>
    val state: Value<RootFeature.State>
    val oneTimeEvent: Flow<OneTimeEvent>

    fun onPermissionGrantedResult(result: Boolean)

    sealed interface Child {
        data class Main(val component: MainComponent) : Child
        data class Authorization(val component: AuthorizationComponent) : Child
        data class SubscriptionRoot(val component: SubscriptionRootComponent) : Child
        data class BuySubscription(val component: BuySubscriptionComponent) : Child
        data class About(val component: AboutComponent) : Child
        data class ServerAdd(val component: AddServerComponent) : Child
        data class MyServers(val component: MyServersComponent) : Child
        data class SplitTunneling(val component: SplitTunnelingComponent) : Child
        data class SupportMain(val component: SupportComponent) : Child
        data class Faq(val component: FaqComponent) : Child
        data class ReportProblem(val component: ReportProblemComponent) : Child
        data class FeedBack(val component: FeedbackComponent) : Child
        data class PrivacyPolicy(val component: WelcomeComponent) : Child
        data class ChooseDnsServer(val component: ChooseDnsServerComponent) : Child
        data class TrafficModule(val component: TrafficModuleComponent) : Child
        data class TrafficModuleLogs(val component: TrafficModuleComponent) : Child
        data class BalanceRefill(val component: BalanceRefillComponent) : Child
    }

    sealed interface OneTimeEvent {
        data class InstallUpdate(val filePath: String) : OneTimeEvent
    }
}

