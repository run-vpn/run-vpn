package com.runvpn.app.feature.settings.splittunneling

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.settings.createSplitTunnelingAppsComponent
import com.runvpn.app.feature.settings.createSplitTunnelingIpsComponent
import com.runvpn.app.feature.settings.createSplitTunnelingMainComponent
import com.runvpn.app.feature.settings.splittunneling.apps.SplitTunnelingAppsComponent
import com.runvpn.app.feature.settings.splittunneling.ips.SplitTunnelingIpsComponent
import com.runvpn.app.feature.settings.splittunneling.main.SplitTunnelingMainComponent
import com.runvpn.app.tea.decompose.DecomposeComponentFactory
import com.runvpn.app.tea.navigation.RootRouter
import kotlinx.serialization.Serializable

internal class DefaultSplitTunnelingComponent(
    componentContext: ComponentContext,
    private val rootRouter: RootRouter,
    private val decomposeComponentFactory: DecomposeComponentFactory,
) : SplitTunnelingComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()


    override val childStack: Value<ChildStack<*, SplitTunnelingComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.SplitTunnelingMain,
        key = "",
        childFactory = ::createChild,
        handleBackButton = true
    )


    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): SplitTunnelingComponent.Child =
        when (config) {
            is ChildConfig.SplitTunnelingMain -> SplitTunnelingComponent.Child.Main(
                decomposeComponentFactory.createSplitTunnelingMainComponent(
                    componentContext,
                    ::handleMainScreenOutput
                )
            )

            is ChildConfig.SplitTunnelingApps -> SplitTunnelingComponent.Child.Apps(
                decomposeComponentFactory.createSplitTunnelingAppsComponent(
                    componentContext,
                    ::handleAppsScreenOutput
                )
            )

            is ChildConfig.SplitTunnelingIps -> SplitTunnelingComponent.Child.Ips(
                decomposeComponentFactory.createSplitTunnelingIpsComponent(
                    componentContext,
                    ::handleIpsScreenOutput
                )
            )
        }


    private fun handleMainScreenOutput(output: SplitTunnelingMainComponent.Output) {
        when (output) {
            SplitTunnelingMainComponent.Output.OnBack -> {
                rootRouter.pop()
            }

            SplitTunnelingMainComponent.Output.OnAppsScreenRequested -> {
                navigation.push(ChildConfig.SplitTunnelingApps)
            }

            SplitTunnelingMainComponent.Output.OnIpsScreenRequest -> {
                navigation.push(ChildConfig.SplitTunnelingIps)
            }

        }
    }

    private fun handleAppsScreenOutput(output: SplitTunnelingAppsComponent.Output) {
        when (output) {
            SplitTunnelingAppsComponent.Output.OnBack -> {
                navigation.pop()
            }
        }
    }

    private fun handleIpsScreenOutput(output: SplitTunnelingIpsComponent.Output) {
        when (output) {
            SplitTunnelingIpsComponent.Output.OnBack -> {
                navigation.pop()
            }
        }
    }


    @Serializable
    internal sealed interface ChildConfig {

        @Serializable
        data object SplitTunnelingMain : ChildConfig

        @Serializable
        data object SplitTunnelingApps : ChildConfig

        @Serializable
        data object SplitTunnelingIps : ChildConfig

    }

}
