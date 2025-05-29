package com.runvpn.app.feature.settings.suggestedserversmode

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent
import com.runvpn.app.tea.message.domain.NullWrapper

interface ChooseSuggestedServersModeComponent: SimpleDialogComponent {

    val allModes: Value<List<SuggestedServersMode>>
    val currentMode: Value<NullWrapper<SuggestedServersMode>>

    fun onCurrentModeChoose(mode: SuggestedServersMode)

}
