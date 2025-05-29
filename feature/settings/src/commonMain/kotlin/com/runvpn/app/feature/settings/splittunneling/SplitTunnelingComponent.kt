package com.runvpn.app.feature.settings.splittunneling

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.runvpn.app.feature.settings.splittunneling.apps.SplitTunnelingAppsComponent
import com.runvpn.app.feature.settings.splittunneling.ips.SplitTunnelingIpsComponent
import com.runvpn.app.feature.settings.splittunneling.main.SplitTunnelingMainComponent

interface SplitTunnelingComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {

        data class Main(
            val component: SplitTunnelingMainComponent
        ) : Child

        data class Apps(
            val component: SplitTunnelingAppsComponent
        ) : Child

        data class Ips(val component: SplitTunnelingIpsComponent) : Child
    }
}
