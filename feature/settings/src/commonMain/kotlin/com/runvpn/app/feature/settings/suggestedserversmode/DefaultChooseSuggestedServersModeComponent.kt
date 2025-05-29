package com.runvpn.app.feature.settings.suggestedserversmode

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import com.runvpn.app.tea.decompose.createCoroutineScope
import com.runvpn.app.tea.message.domain.NullWrapper
import kotlinx.coroutines.launch

internal class DefaultChooseSuggestedServersModeComponent(
    componentContext: ComponentContext,
    private val appSettingsRepository: AppSettingsRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val onModeChoose: (SuggestedServersMode) -> Unit,
    private val onDismissed: () -> Unit
) : ChooseSuggestedServersModeComponent, ComponentContext by componentContext {

    private val coroutineScope = createCoroutineScope()
    override val allModes: Value<List<SuggestedServersMode>>
        get() = MutableValue(appSettingsRepository.getSuggestedServersModes())

    private val _currentMode: MutableValue<NullWrapper<SuggestedServersMode>> =
        MutableValue(NullWrapper(null))

    override val currentMode: Value<NullWrapper<SuggestedServersMode>> = _currentMode

    init {
        coroutineScope.launch {
            userSettingsRepository.suggestedServersMode.collect {
                _currentMode.value = NullWrapper(it)
            }
        }
    }

    override fun onCurrentModeChoose(mode: SuggestedServersMode) {
        onModeChoose(mode)
        userSettingsRepository.setSuggestedServersMode(mode)
        onDismissed()
    }

    override fun onDismissClicked() {
        onDismissed()
    }
}
