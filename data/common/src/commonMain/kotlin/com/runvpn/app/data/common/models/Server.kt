package com.runvpn.app.data.common.models

import kotlinx.serialization.Serializable

@Serializable
data class Server(
    val uuid: String,
    val host: String,
    val protocol: ConnectionProtocol,
    val source: ServerSource,
    val isFavorite: Boolean,
    val isPublic: Boolean,

    //Nullable Fields
    val name: String?,
    val country: String?,
    val city: String?,
    val iso: String?,
    val latitude: String?,
    val longitude: String?,
    val config: Map<String, String?>?
) {
    val lastConnectionTime: Long? = null
    val hasValidCountry = !(country.isNullOrEmpty() || country == "undefined")

    val isMine = source == ServerSource.MINE

}


fun Server.toVpnServer() = VpnServer(
    uuid = uuid,
    host = host,
    protocol = protocol,
    config = config
)
