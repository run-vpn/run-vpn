package com.runvpn.app.feature.settings.support.about

import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.settings.models.AppVersion

interface AboutComponent {

    data class State(
        val aboutInfo: AppVersion,
        val siteUrl: String,
        val ourCompany: String,
        val deviceUuid: String?
    )

    val state: Value<State>

    fun onDeviceUuidClick(uuid:String)
    fun onBack()


    sealed interface Output {
        data object OnBack : Output
    }

}
