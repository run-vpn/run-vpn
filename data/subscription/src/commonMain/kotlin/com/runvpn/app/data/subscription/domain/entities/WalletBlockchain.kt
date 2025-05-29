package com.runvpn.app.data.subscription.domain.entities

import kotlinx.serialization.SerialName

enum class WalletBlockchain {
    @SerialName("bitcoin")
    BITCOIN,
    @SerialName("tron")
    TRON
}
