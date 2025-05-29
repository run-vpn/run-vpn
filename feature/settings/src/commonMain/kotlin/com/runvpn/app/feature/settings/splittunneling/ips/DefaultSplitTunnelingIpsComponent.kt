package com.runvpn.app.feature.settings.splittunneling.ips

import com.arkivanov.decompose.ComponentContext

internal class DefaultSplitTunnelingIpsComponent(
    componentContext: ComponentContext,
    private val output: (SplitTunnelingIpsComponent.Output) -> Unit
) : SplitTunnelingIpsComponent, ComponentContext by componentContext {
    override fun onAddIp() {

    }

    override fun onBackClick() {
        output(SplitTunnelingIpsComponent.Output.OnBack)
    }
}
