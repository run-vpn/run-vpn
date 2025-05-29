package com.runvpn.app.data.servers.data.dto

import com.runvpn.app.core.common.uuid4
import com.runvpn.app.data.servers.data.ext.DataExtensions.parseProtocol
import com.runvpn.app.data.servers.data.ext.DataExtensions.parseSource
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.common.models.ServerSource
import com.runvpn.app.db.cache.DbServer
import kotlinx.serialization.Serializable


@Serializable
data class CustomServerDto(
    val host: String,
    val name: String?,
    val protocol: String,
    val config: Map<String, String?>?,
    val isPublic: Boolean
)

fun CustomServerDto.toDbServer(uuid: String) = DbServer(
    uuid = uuid,
    name = name,
    host = host,
    protocol = protocol,
    source = ServerSource.CUSTOM_SERVER_DEFAULT,
    isFavorite = false,
    isPublic = isPublic,

    //Nullable Fields
    country = null,
    city = null,
    iso = null,
    latitude = null,
    longitude = null,
    lastConnectionTime = null
)

fun CustomServerDto.toDomainServerForTest(uuid: String? = null) = Server(
    uuid = uuid ?: uuid4(),
    name = name,
    host = host,
    protocol = protocol.parseProtocol(),
    source = ServerSource.CUSTOM_SERVER_DEFAULT.parseSource(),
    isFavorite = false,
    isPublic = isPublic,

    //Nullable Fields
    country = null,
    city = null,
    iso = null,
    latitude = null,
    longitude = null,
    config = config
)
