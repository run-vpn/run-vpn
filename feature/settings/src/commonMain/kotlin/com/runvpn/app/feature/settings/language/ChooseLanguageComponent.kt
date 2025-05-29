package com.runvpn.app.feature.settings.language

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.domain.Language
import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface ChooseLanguageComponent : SimpleDialogComponent {

    val languages: Value<List<Language>>
    val currentLanguage: Value<Language>

    fun onLanguageChosen(lang: Language)
}
