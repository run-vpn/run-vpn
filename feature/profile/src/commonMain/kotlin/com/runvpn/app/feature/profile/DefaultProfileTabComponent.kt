package com.runvpn.app.feature.profile

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.profile.main.ProfileComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.navigation.RootChildConfig
import com.runvpn.app.tea.navigation.RootRouter
import kotlinx.serialization.Serializable

internal class DefaultProfileTabComponent(
    componentContext: ComponentContext,
    private val decomposeComponentFactory: DecomposeComponentFactory,
    private val rootRouter: RootRouter,
    private val messageService: MessageService
) : ProfileTabComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: Value<ChildStack<*, ProfileTabComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.Main,
        key = "",
        childFactory = ::createChild,
        handleBackButton = true
    )

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): ProfileTabComponent.Child =
        when (config) {
            is ChildConfig.Main -> ProfileTabComponent.Child.Main(
                decomposeComponentFactory.createProfilesComponent(
                    componentContext,
                    ::handleMainChildOutput
                )
            )
        }

    private fun handleMainChildOutput(output: ProfileComponent.Output) {
        when (output) {
            is ProfileComponent.Output.OnAuthScreenRequest -> rootRouter.open(RootChildConfig.Authorization)

            is ProfileComponent.Output.OnTrafficModuleScreenRequest -> rootRouter.open(
                RootChildConfig.TrafficModule
            )

            is ProfileComponent.Output.OnBuySubscriptionRequested -> rootRouter.open(
                RootChildConfig.BuySubscriptionConfig
            )

            is ProfileComponent.Output.OnActivatePromoRequested -> {
                messageService.showMessage(AppMessage.NotImplemented())
            }
        }
    }


    @Serializable
    private sealed interface ChildConfig {

        @Serializable
        data object Main : ChildConfig


    }
}

