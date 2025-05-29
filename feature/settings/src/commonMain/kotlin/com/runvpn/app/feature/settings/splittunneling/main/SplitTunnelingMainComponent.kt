package com.runvpn.app.feature.settings.splittunneling.main

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.SplitTunnelingApplication
import com.runvpn.app.data.settings.domain.SplitTunnelingMode

interface SplitTunnelingMainComponent {

    data class State(
        val ipSplitMode: SplitTunnelingMode,
        val appsSplitMode: SplitTunnelingMode,
        val ips: List<String>,
        val apps: List<SplitTunnelingApplication>
    )

    val state: Value<State>

    fun onSplitAppsClick()

    fun onSplitIpsClick()

    fun onBackClick()

    sealed interface Output {
        data object OnBack : Output
        data object OnAppsScreenRequested : Output
        data object OnIpsScreenRequest : Output
    }
}
