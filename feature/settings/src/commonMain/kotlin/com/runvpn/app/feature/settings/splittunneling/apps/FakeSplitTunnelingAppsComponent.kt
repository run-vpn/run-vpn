package com.runvpn.app.feature.settings.splittunneling.apps

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.SplitTunnelingApplication
import com.runvpn.app.data.settings.domain.SplitTunnelingMode
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

class FakeSplitTunnelingAppsComponent : SplitTunnelingAppsComponent {
    override val dialog: Value<ChildSlot<*, SimpleDialogComponent>>
        get() = MutableValue(
            ChildSlot<Nothing, Nothing>(null)
        )
    override val state: Value<SplitTunnelingAppsComponent.State>
        get() = MutableValue(
            SplitTunnelingAppsComponent.State(
                loading = false,
                splitMode = SplitTunnelingMode.ALL,
                listOf(),
                listOf(
                    SplitTunnelingApplication("com.runvpn", "RunVpn", false),
                    SplitTunnelingApplication("com.google.chrome", "GoogleChrome", false),
                    SplitTunnelingApplication("com.google.android.apps.photos", "Photos", true),
                    SplitTunnelingApplication("com.google.android.youtube", "YouTube", false),
                ).sortedBy { it.isSystem }
            )
        )

    override fun onAddApp(app: SplitTunnelingApplication) {
        TODO("Not yet implemented")
    }

    override fun onRemoveApp(app: SplitTunnelingApplication) {
        TODO("Not yet implemented")
    }

    override fun onBackClick() {
        TODO("Not yet implemented")
    }

    override fun onChangeSplitMode(mode: SplitTunnelingMode) {
        TODO("Not yet implemented")
    }
}
