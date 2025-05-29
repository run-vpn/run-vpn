package com.runvpn.app.android.ext

import android.content.Context
import com.runvpn.app.android.BuildConfig
import com.runvpn.app.android.R
import com.runvpn.app.core.exceptions.DeserializationException
import com.runvpn.app.core.exceptions.NoInternetException
import com.runvpn.app.core.exceptions.NoServerResponseException
import com.runvpn.app.core.exceptions.ServerException
import com.runvpn.app.tea.dialog.DialogMessage
import com.runvpn.app.tea.message.domain.AppMessage


fun AppMessage.getMessage(context: Context): String = when (this) {
    is AppMessage.Common -> this.message
    is AppMessage.Error -> getErrorMessage(context, this)
    is AppMessage.AddServerToFavorites -> context.getString(R.string.server_added_to_favorites)
    is AppMessage.IncorrectAuthCombination -> context.getString(R.string.incorrect_auth_combination)
    is AppMessage.UnsupportedFormat -> context.getString(R.string.unsupported_format)
    is AppMessage.CopyToClipboard -> context.getString(R.string.copied_to_clipboard)
    is AppMessage.SubscriptionActivated -> String.format(
        context.getString(
            R.string.activation_success_message
        ),
        this.deviceName ?: context.getString(R.string.dev_unknown),
        this.activeUntil.secondToDateTimeFormat2()
    )

    is AppMessage.SuccessSubscriptionBuying -> context.getString(R.string.subscription_success_buyed)
    is AppMessage.DeviceNameChanged -> context.getString(R.string.device_name_changed)
    is AppMessage.Persistent -> this.message
    is AppMessage.NotImplemented -> context.getString(R.string.function_not_implemented_yet)
}

private fun getErrorMessage(context: Context, message: AppMessage.Error): String =
    when (message.throwable) {
        is NoServerResponseException -> context.getString(R.string.error_no_server_response)
        is NoInternetException -> context.getString(R.string.error_no_internet)
        is ServerException -> context.getString(R.string.error_invalid_response)
        is DeserializationException -> context.getString(R.string.error_unexpected)
        else -> {
            val desc = message.throwable.message
            if (desc != null && BuildConfig.DEBUG) {
                context.getString(R.string.error_unexpected_with_description, desc)
            } else {
                context.getString(R.string.error_unexpected)
            }
        }
    }


fun DialogMessage.getLocalizedMessage(context: Context): DialogMessage.Common = when (this) {

    is DialogMessage.ReconnectVPN -> DialogMessage.Common(
        title = context.getString(R.string.title_dialog_reconnect),
        message = context.getString(R.string.message_dialog_reconnect),
        negativeButtonText = context.getString(R.string.later),
        positiveButtonText = context.getString(R.string.reconnect)
    )

    is DialogMessage.SoftUpdate -> DialogMessage.Common(
        title = context.getString(R.string.common_have_update_title),
        message = context.getString(R.string.common_have_update_desc),
        positiveButtonText = context.getString(R.string.common_install)
    )

    is DialogMessage.Common -> this
}
