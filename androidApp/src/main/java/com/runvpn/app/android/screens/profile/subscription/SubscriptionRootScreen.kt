package com.runvpn.app.android.screens.profile.subscription

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.runvpn.app.android.screens.profile.subscription.buysubscription.ChooseRateBsDialog
import com.runvpn.app.feature.subscription.SubscriptionRootComponent

@Composable
fun SubscriptionRootScreen(component: SubscriptionRootComponent) {

    Children(stack = component.childStack, animation = stackAnimation(slide())) { childInstance ->
        when (val child = childInstance.instance) {
            is SubscriptionRootComponent.Child.Subscription -> SubscriptionScreen(
                component = child.component
            )

            is SubscriptionRootComponent.Child.ChooseRate -> ChooseRateBsDialog(
                component = child.component
            )

            is SubscriptionRootComponent.Child.ActivateSubscription -> SubscriptionActivateScreen(
                component = child.component
            )

            is SubscriptionRootComponent.Child.ConfirmSubscription -> SubscriptionConfirmScreen(
                component = child.component
            )

            is SubscriptionRootComponent.Child.AboutDevice -> AboutDeviceScreen(
                component = child.component
            )

            is SubscriptionRootComponent.Child.ActivatePromo -> ActivatePromoScreen(
                component = child.component
            )
        }
    }
}
