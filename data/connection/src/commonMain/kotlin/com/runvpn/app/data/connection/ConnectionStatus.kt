package com.runvpn.app.data.connection

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


sealed interface ConnectionStatus {
    data object Connected : ConnectionStatus
    data class Connecting(val statusMessage: String?) : ConnectionStatus
    data class Error(val message: String?) : ConnectionStatus

    data object Paused : ConnectionStatus

    data object Disconnected : ConnectionStatus

    data class Idle(
        val reason: IdleReason = IdleReason.DEFAULT,
        val idleTime: Instant = Clock.System.now()
    ) : ConnectionStatus

    fun isDisconnected(): Boolean {
        return this is Disconnected || this is Error || this is Paused || this is Idle
    }

    enum class IdleReason {
        AUTO_DISCONNECT;

        companion object {
            val DEFAULT get() = AUTO_DISCONNECT
        }
    }
}
