package com.runvpn.app.data.servers.data.dto

import com.runvpn.app.db.cache.DbServer
import kotlinx.serialization.Serializable

@Serializable
data class RemoteServer(
    val uuid: String,
    val host: String,
    val protocol: String,
    val source: String,
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
)



fun RemoteServer.toDbServer() = DbServer(
    uuid = uuid,
    name = name,
    host = host,
    protocol = protocol,
    source = source,
    isFavorite = isFavorite,
    isPublic = isPublic,

    //Nullable Fields
    country = country,
    city = city,
    iso = iso,
    latitude = latitude,
    longitude = longitude,
    lastConnectionTime = null
)

