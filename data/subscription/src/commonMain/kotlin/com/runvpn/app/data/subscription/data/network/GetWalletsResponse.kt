package com.runvpn.app.data.subscription.data.network

import com.runvpn.app.data.subscription.domain.entities.WalletBlockchain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CryptoWalletWithCost(
    @SerialName("blockchain")
    val blockchain: WalletBlockchain,
    @SerialName("currency")
    val currency: String,
    @SerialName("address")
    val address: String,
    @SerialName("rate")
    val rate: String
)

@Serializable
data class GetWalletsResponse(
    @SerialName("items")
    val wallets: List<CryptoWalletWithCost>
)

