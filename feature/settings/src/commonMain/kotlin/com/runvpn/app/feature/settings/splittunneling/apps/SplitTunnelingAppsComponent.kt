package com.runvpn.app.feature.settings.splittunneling.apps

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.SplitTunnelingApplication
import com.runvpn.app.data.settings.domain.SplitTunnelingMode
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface SplitTunnelingAppsComponent {
    data class State(
        val loading: Boolean,
        val splitMode: SplitTunnelingMode,
        val excludedApps: List<SplitTunnelingApplication>,
        val allApps: List<SplitTunnelingApplication>
    )

    val dialog: Value<ChildSlot<*, SimpleDialogComponent>>

    val state: Value<State>

    fun onChangeSplitMode(mode: SplitTunnelingMode)
    fun onAddApp(app: SplitTunnelingApplication)
    fun onRemoveApp(app: SplitTunnelingApplication)

    fun onBackClick()

    sealed interface Output {
        data object OnBack : Output
    }
}
