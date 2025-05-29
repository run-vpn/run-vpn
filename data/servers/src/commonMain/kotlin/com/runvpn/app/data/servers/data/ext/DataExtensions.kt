package com.runvpn.app.data.servers.data.ext

import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.common.models.ServerSource
import com.runvpn.app.db.cache.DbCustomVpnConfig
import com.runvpn.app.db.cache.DbServer

internal object DataExtensions {


    fun Map<String, String?>.toDbConfigList(uuid: String) =
        this.map { it.toDbConfig(uuid) }


    /**
     * Extension functions
     * */


    fun DbServer.toDomain(configParams: List<DbCustomVpnConfig>?) = Server(
        uuid = uuid,
        name = name,
        host = host,
        protocol = protocol.parseProtocol(),
        source = source.parseSource(),
        isFavorite = isFavorite,
        isPublic = isPublic,

        //Nullable Fields
        country = country,
        city = city,
        iso = iso,
        latitude = latitude,
        longitude = longitude,
        config = configParams?.parseConfig(),
    )

    fun String.parseProtocol() =
        ConnectionProtocol.entries.toTypedArray().find { it.name == this.uppercase() }
            ?: ConnectionProtocol.UNDEFINED

    fun String.parseSource() =
        ServerSource.entries.toTypedArray().find { it.name == this.uppercase() }
            ?: ServerSource.UNDEFINED

    fun List<DbCustomVpnConfig>.parseConfig(): Map<String, String?> {
        return this.associate {
            Pair(it.confKey, it.confValue)
        }
    }

    fun Map.Entry<String, String?>.toDbConfig(uuid: String): DbCustomVpnConfig {
        return DbCustomVpnConfig(
            uniqueUpdateKey = uuid + this.key,
            serverUuid = uuid,
            confKey = this.key,
            confValue = this.value
        )
    }
}
