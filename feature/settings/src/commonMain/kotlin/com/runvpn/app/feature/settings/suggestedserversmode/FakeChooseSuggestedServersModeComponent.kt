package com.runvpn.app.feature.settings.suggestedserversmode

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.tea.message.domain.NullWrapper

class FakeChooseSuggestedServersModeComponent : ChooseSuggestedServersModeComponent {
    override val allModes: Value<List<SuggestedServersMode>>
        get() = MutableValue(SuggestedServersMode.entries)
    override val currentMode: Value<NullWrapper<SuggestedServersMode>>
        get() = MutableValue(NullWrapper(SuggestedServersMode.AUTO))

    override fun onCurrentModeChoose(mode: SuggestedServersMode) {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }
}
