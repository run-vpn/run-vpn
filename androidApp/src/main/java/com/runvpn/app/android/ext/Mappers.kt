package com.runvpn.app.android.ext

import android.content.Context
import com.murgupluoglu.flagkit.FlagKit
import com.runvpn.app.android.R
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.common.models.ServerSource
import com.runvpn.app.data.device.domain.models.PasswordValidationResult
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.data.settings.domain.Tariff
import com.runvpn.app.data.subscription.domain.entities.WalletBlockchain


fun Server.getIconResId(context: Context): Int {
    if (this.source == ServerSource.MINE) {
        return getProtocolIcon(this.protocol)
    }

    val id = this.iso?.let { FlagKit.getResId(context, it) } ?: 0
    return if (id == 0) {
        R.drawable.ic_protocol_unspecified
    } else {
        id
    }
}

fun SuggestedServersMode.toLocaleString(context: Context): String {
    return when (this) {
        SuggestedServersMode.NONE -> context.getString(R.string.suggested_servers_not_show)
        SuggestedServersMode.AUTO -> context.getString(R.string.suggested_servers_auto)
        SuggestedServersMode.FAVORITES -> context.getString(R.string.suggested_servers_favorite)
        SuggestedServersMode.RECENT -> context.getString(R.string.suggested_servers_latest)
        SuggestedServersMode.RECOMMENDED -> context.getString(R.string.suggested_servers_recommended)
    }
}

fun getProtocolIcon(connectionProtocol: ConnectionProtocol) = when (connectionProtocol) {
    ConnectionProtocol.IKEV2 -> R.drawable.ic_protocol_ikev2
    ConnectionProtocol.XRAY -> R.drawable.ic_protocol_ikev2
    ConnectionProtocol.OVERSOCKS -> R.drawable.ic_protocol_ikev2
    ConnectionProtocol.OPENVPN -> R.drawable.ic_protocol_openvpn
    ConnectionProtocol.WIREGUARD -> R.drawable.ic_protocol_wireguard
    ConnectionProtocol.UNDEFINED ->R.drawable.ic_protocol_ikev2
}

fun getProtocolName(connectionProtocol: ConnectionProtocol) = when (connectionProtocol) {
    ConnectionProtocol.IKEV2 -> R.string.protocol_ikev2
    ConnectionProtocol.XRAY -> R.string.protocol_xray
    ConnectionProtocol.OVERSOCKS -> R.string.protocol_oversocks
    ConnectionProtocol.OPENVPN -> R.string.protocol_openvpn
    ConnectionProtocol.WIREGUARD -> R.string.protocol_wireguard
    ConnectionProtocol.UNDEFINED -> R.string.dev_unknown
}


fun WalletBlockchain.toBlockchainIconResource() = when (this) {
    WalletBlockchain.BITCOIN -> R.drawable.ic_crypto_btc
    WalletBlockchain.TRON -> R.drawable.ic_crypto_tether
}


//fun ConnectionStatus.toDescLocalizedRedId(): Int {
//    return when (this) {
//        ConnectionStatus.Connected -> R.string.vpn_status_connected
//        is ConnectionStatus.Connecting -> R.string.vpn_status_connecting
//        ConnectionStatus.Disconnected -> R.string.vpn_status_disconnected
//        is ConnectionStatus.Error -> R.string.vpn_status_error
//        ConnectionStatus.Paused -> R.string.vpn_notification_status_pause_desc
//        is ConnectionStatus.Idle -> R.string.idle
//    }
//}

//fun ConnectionStatus.toActionLocalizedResId(): Int {
//    return when (this) {
//        is ConnectionStatus.Connected -> R.string.vpn_action_disconnect
//        is ConnectionStatus.Disconnected -> R.string.vpn_action_connect
//        is ConnectionStatus.Paused -> R.string.vpn_action_connect
//        is ConnectionStatus.Error -> R.string.vpn_action_connect
//        is ConnectionStatus.Connecting -> R.string.vpn_action_disconnect
//    }
//}

fun PasswordValidationResult.toErrorMessage(context: Context): String? {
    return when (this) {
        PasswordValidationResult.ErrorEmpty -> context.getString(R.string.error_empty_password)
        PasswordValidationResult.ErrorMinimumSymbols -> context.getString(R.string.error_password_minimum)
        PasswordValidationResult.Valid -> null
        PasswordValidationResult.ErrorConfirmPasswordDoesNotMatch ->
            context.getString(R.string.error_password_not_match)
    }
}

fun getTariffName(mode: Tariff) = when (mode) {
    Tariff.FREE -> R.string.variant_free
    Tariff.PAID -> R.string.variant_paid
    Tariff.EXPIRED -> R.string.variant_expired
}


fun getTariffNameForDevice(mode: Tariff) = when (mode) {
    Tariff.FREE -> R.string.you_not_activated
    Tariff.PAID -> R.string.you_activated
    Tariff.EXPIRED -> R.string.variant_expired
}
