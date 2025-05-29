package com.runvpn.app.feature.subscription

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.feature.subscription.aboutdevice.AboutDeviceComponent
import com.runvpn.app.feature.subscription.activate.SubscriptionActivateComponent
import com.runvpn.app.feature.subscription.confirm.SubscriptionConfirmComponent
import com.runvpn.app.feature.subscription.info.SubscriptionInfoComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.dialog.DialogManager
import com.runvpn.app.tea.dialog.RootDialogConfig
import com.runvpn.app.tea.navigation.RootChildConfig
import com.runvpn.app.tea.navigation.RootRouter
import kotlinx.serialization.Serializable

internal class DefaultSubscriptionRootComponent(
    componentContext: ComponentContext,
    private val decomposeComponentFactory: DecomposeComponentFactory,
    private val rootRouter: RootRouter,
    private val dialogManager: DialogManager
) : SubscriptionRootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()
    override val childStack: Value<ChildStack<*, SubscriptionRootComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.SubscriptionConfig,
        key = "",
        childFactory = ::createChild,
        handleBackButton = true
    )

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): SubscriptionRootComponent.Child =
        when (config) {
            is ChildConfig.SubscriptionConfig -> SubscriptionRootComponent.Child.Subscription(
                decomposeComponentFactory.createSubscriptionInfoComponent(
                    componentContext, ::onSubscriptionInfoOutput
                )
            )

            is ChildConfig.ActivateSubscriptionConfig -> SubscriptionRootComponent.Child.ActivateSubscription(
                decomposeComponentFactory.createSubscriptionActivateComponent(
                    componentContext,
                    ::onSubscriptionActivateOutput
                )
            )

            is ChildConfig.ConfirmSubscriptionConfig -> SubscriptionRootComponent.Child.ConfirmSubscription(
                decomposeComponentFactory.createSubscriptionConfirmComponent(
                    componentContext,
                    config.subscriptionId,
                    config.device,
                    ::handleSubscriptionConfirmOutput
                )
            )


            is ChildConfig.AboutDeviceConfig -> SubscriptionRootComponent.Child.AboutDevice(
                decomposeComponentFactory.createAboutDeviceComponent(
                    componentContext,
                    config.device,
                    ::handleAboutDeviceOutput
                )
            )

            is ChildConfig.ActivatePromoConfig -> SubscriptionRootComponent.Child.ActivatePromo(
                decomposeComponentFactory.createActivatePromoComponent(
                    componentContext
                )
            )

        }

    private fun handleAboutDeviceOutput(output: AboutDeviceComponent.Output) {
        when (output) {
            is AboutDeviceComponent.Output.OnActivatePromoRequested -> navigation.push(ChildConfig.ActivatePromoConfig)

            is AboutDeviceComponent.Output.OnBack -> navigation.pop()
        }
    }

    private fun handleSubscriptionConfirmOutput(output: SubscriptionConfirmComponent.Output) {
        when (output) {
            is SubscriptionConfirmComponent.Output.OnActivationSuccess -> {
                navigation.popWhile { topStack: ChildConfig -> topStack !is ChildConfig.SubscriptionConfig }
            }
        }
    }

    private fun onSubscriptionActivateOutput(output: SubscriptionActivateComponent.Output) {
        when (output) {
            is SubscriptionActivateComponent.Output.OnBack -> navigation.pop()

            is SubscriptionActivateComponent.Output.OnConfirmScreenRequested -> {
                navigation.push(
                    ChildConfig.ConfirmSubscriptionConfig(
                        subscriptionId = output.subscriptionId,
                        device = output.device
                    )
                )
            }
        }
    }

    private fun onSubscriptionInfoOutput(output: SubscriptionInfoComponent.Output) {
        when (output) {
            is SubscriptionInfoComponent.Output.BuySubscriptionRequested ->
                rootRouter.open(RootChildConfig.BuySubscriptionConfig)

            is SubscriptionInfoComponent.Output.SubscriptionActivateRequested ->
                navigation.push(ChildConfig.ActivateSubscriptionConfig)

            is SubscriptionInfoComponent.Output.AboutDeviceRequested ->
                navigation.push(ChildConfig.AboutDeviceConfig(output.device))

            is SubscriptionInfoComponent.Output.SwitchTariffRequested ->
                dialogManager.showDialog(RootDialogConfig.ChooseAppUsageModeConfig(isCancellable = true))

            is SubscriptionInfoComponent.Output.OnBack -> rootRouter.pop()
        }
    }


    @Serializable
    sealed interface ChildConfig {

        @Serializable
        data object SubscriptionConfig : ChildConfig

        @Serializable
        data object ActivateSubscriptionConfig : ChildConfig

        @Serializable
        data class ConfirmSubscriptionConfig(
            val subscriptionId: String,
            val device: Device
        ) : ChildConfig

        @Serializable
        data class AboutDeviceConfig(val device: Device) : ChildConfig

        @Serializable
        data object ActivatePromoConfig : ChildConfig
    }
}
