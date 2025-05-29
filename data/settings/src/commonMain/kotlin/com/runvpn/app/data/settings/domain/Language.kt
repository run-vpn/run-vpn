package com.runvpn.app.data.settings.domain

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val isoCode: String,
    val flagIsoCode: String,
    val language: String,
    val languageInEnglish: String
)
