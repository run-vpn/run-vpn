package com.runvpn.app.feature.settings.language

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.Language

class FakeChooseLanguageComponent : ChooseLanguageComponent {
    override val languages: Value<List<Language>>
        get() = MutableValue(
            listOf(
                Language(
                    isoCode = "en",
                    flagIsoCode = "us",
                    language = "English",
                    languageInEnglish = "English"
                ),
                Language(
                    isoCode = "ru",
                    flagIsoCode = "ru",
                    language = "Русский",
                    languageInEnglish = "Russian"
                ),
            )
        )
    override val currentLanguage: Value<Language>
        get() = MutableValue(languages.value.first())

    override fun onLanguageChosen(lang: Language) {
        TODO("Not yet implemented")
    }

    override fun onDismissClicked() {
        TODO("Not yet implemented")
    }
}
