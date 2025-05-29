package com.runvpn.app.feature.settings.splittunneling.ips

interface SplitTunnelingIpsComponent {

    fun onAddIp()

    fun onBackClick()
    sealed interface Output {
        data object OnBack : Output
    }

}
