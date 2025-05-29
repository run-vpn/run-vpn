package com.runvpn.app.feature.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.settings.connection.ConnectionSettingsComponent
import com.runvpn.app.feature.settings.main.MainSettingsComponent
import com.runvpn.app.feature.settings.support.about.AboutComponent
import com.runvpn.app.feature.settings.support.createSupportComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.navigation.RootChildConfig
import com.runvpn.app.tea.navigation.RootRouter
import kotlinx.serialization.Serializable

class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val componentFactory: DecomposeComponentFactory,
    private val rootRouter: RootRouter
) : SettingsComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: Value<ChildStack<*, SettingsComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.Main,
        key = "NewSettingsComponent",
        childFactory = ::createChild,
        handleBackButton = true
    )

    private fun createChild(
        childConfig: ChildConfig,
        componentContext: ComponentContext
    ): SettingsComponent.Child = when (childConfig) {
        ChildConfig.AboutApp -> SettingsComponent.Child.AboutApp(
            component = componentFactory.createAboutComponent(
                componentContext,
                ::onAboutScreenOutput
            )
        )

        ChildConfig.Common -> SettingsComponent.Child.Common(
            component = componentFactory.createCommonSettingsComponent(componentContext)
        )

        ChildConfig.Connection -> SettingsComponent.Child.Connection(
            component = componentFactory.createConnectionSettingsComponent(
                componentContext,
                ::onConnectionSettingsOutput
            )
        )

        ChildConfig.Main -> SettingsComponent.Child.Main(
            componentFactory.createMainSettingsComponent(componentContext, ::onMainSettingsOutput)
        )

        ChildConfig.Support -> SettingsComponent.Child.Support(
            componentFactory.createSupportComponent(componentContext)
        )
    }

    private fun onMainSettingsOutput(output: MainSettingsComponent.Output) {
        when (output) {
            is MainSettingsComponent.Output.OnConnectionSettingsRequested -> {
                navigation.push(ChildConfig.Connection)
            }

            is MainSettingsComponent.Output.OnSupportScreenRequested -> {
                navigation.push(ChildConfig.Support)
            }

            is MainSettingsComponent.Output.OnAboutScreenRequested -> {
                navigation.push(ChildConfig.AboutApp)
            }

            is MainSettingsComponent.Output.OnCommonSettingsRequested -> {
                navigation.push(ChildConfig.Common)
            }
        }
    }

    private fun onConnectionSettingsOutput(output: ConnectionSettingsComponent.Output) {
        when (output) {
            is ConnectionSettingsComponent.Output.OnSplitTunnellingScreenRequested -> {
                rootRouter.open(RootChildConfig.SplitTunneling)
            }

            is ConnectionSettingsComponent.Output.OnDnsServersScreenRequested -> {
                rootRouter.open(RootChildConfig.ChooseDnsServer)
            }
        }
    }

    private fun onAboutScreenOutput(output: AboutComponent.Output) {
        when (output) {
            is AboutComponent.Output.OnBack -> navigation.pop()
        }
    }

    @Serializable
    sealed interface ChildConfig {
        @Serializable
        data object Main : ChildConfig

        @Serializable
        data object Common : ChildConfig

        @Serializable
        data object Connection : ChildConfig

        @Serializable
        data object Support : ChildConfig

        @Serializable
        data object AboutApp : ChildConfig
    }
}
