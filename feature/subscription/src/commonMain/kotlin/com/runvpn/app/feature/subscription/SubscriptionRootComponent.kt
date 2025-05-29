package com.runvpn.app.feature.subscription

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.subscription.aboutdevice.AboutDeviceComponent
import com.runvpn.app.feature.subscription.activate.SubscriptionActivateComponent
import com.runvpn.app.feature.subscription.confirm.SubscriptionConfirmComponent
import com.runvpn.app.feature.subscription.info.SubscriptionInfoComponent
import com.runvpn.app.feature.subscription.promo.ActivatePromoComponent
import com.runvpn.app.feature.subscription.tariff.ChooseRateComponent

interface SubscriptionRootComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {

        data class Subscription(val component: SubscriptionInfoComponent) : Child
        data class ChooseRate(val component: ChooseRateComponent) : Child
        data class ActivateSubscription(val component: SubscriptionActivateComponent) : Child
        data class ConfirmSubscription(val component: SubscriptionConfirmComponent) : Child
        data class AboutDevice(val component: AboutDeviceComponent) : Child
        data class ActivatePromo(val component: ActivatePromoComponent) : Child
    }

}
