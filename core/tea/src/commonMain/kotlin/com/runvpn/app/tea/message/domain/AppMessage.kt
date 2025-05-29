package com.runvpn.app.tea.message.domain

sealed class AppMessage(
    val message: String,
    val actionTitle: String? = null,
    val action: (() -> Unit)? = null
) {
    class NotImplemented : AppMessage("")

    class Common(
        message: String,
        actionTitle: String? = null,
        action: (() -> Unit)? = null
    ) : AppMessage(message, actionTitle, action)

    class Persistent(
        text: String,
        actionTitle: String? = null,
        action: () -> Unit
    ) : AppMessage(text, actionTitle, action)

    class Error(val throwable: Throwable, textMessage: String? = null) : AppMessage(
        message = textMessage ?: throwable.message ?: ""
    )

    class AddServerToFavorites : AppMessage("")

    class IncorrectAuthCombination : AppMessage("")

    class UnsupportedFormat : AppMessage("")

    class CopyToClipboard : AppMessage("")

    class SubscriptionActivated(val deviceName: String?, val activeUntil: Long) : AppMessage("")

    class SuccessSubscriptionBuying : AppMessage("")

    class DeviceNameChanged : AppMessage("")
}
