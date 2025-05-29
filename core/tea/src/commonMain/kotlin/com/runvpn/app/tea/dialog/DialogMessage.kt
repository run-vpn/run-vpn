package com.runvpn.app.tea.dialog

import kotlinx.serialization.Serializable


@Serializable
sealed class DialogMessage {

    @Serializable
    class Common(
        val title: String,
        val message: String,
        val negativeButtonText: String? = null,
        val positiveButtonText: String
    ) : DialogMessage()


    @Serializable
    data object SoftUpdate : DialogMessage()

    @Serializable
    data object ReconnectVPN : DialogMessage()

}
