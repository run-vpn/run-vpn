package com.runvpn.app.feature.settings.language

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.Language
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

internal class DefaultChooseLanguageComponent(
    componentContext: ComponentContext,
    private val appSettingsRepository: AppSettingsRepository,
    private val onLanguageSelected: (Language) -> Unit,
    private val onDismissed: () -> Unit
) : ChooseLanguageComponent, ComponentContext by componentContext {

    override val languages: Value<List<Language>> =
        MutableValue(appSettingsRepository.getAvailableLanguages())
    override val currentLanguage: Value<Language> = MutableValue(
        appSettingsRepository.userSelectedLocale!!
    )

    override fun onLanguageChosen(lang: Language) {
        appSettingsRepository.userSelectedLocale = lang

        onLanguageSelected(lang)
        onDismissed()
    }

    override fun onDismissClicked() {
        onDismissed()
    }

}
