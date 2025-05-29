package com.runvpn.app.data.subscription.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class WalletWithRateCost(
    val blockchain: WalletBlockchain,
    val currency: String,
    val address: String,
    val rate: String
){
    val ticket = currency.split(".").firstOrNull() ?: currency
    val name = currency.split(".").getOrNull(1) ?: currency
}
