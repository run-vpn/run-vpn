package com.runvpn.app.data.servers.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.touchlab.kermit.Logger
import com.runvpn.app.core.common.uuid4
import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.common.models.ServerSource
import com.runvpn.app.data.servers.data.dto.CustomServerDto
import com.runvpn.app.data.servers.data.dto.RemoteServer
import com.runvpn.app.data.servers.data.dto.toDbServer
import com.runvpn.app.data.servers.data.dto.toDomainServerForTest
import com.runvpn.app.data.servers.data.dto.toLocalXray
import com.runvpn.app.data.servers.data.ext.DataExtensions.toDbConfig
import com.runvpn.app.data.servers.data.ext.DataExtensions.toDomain
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.servers.utils.CustomConfigFields
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.db.cache.DbCustomVpnConfig
import com.runvpn.app.db.cache.DbCustomVpnConfigQueries
import com.runvpn.app.db.cache.DbServer
import com.runvpn.app.db.cache.DbServerQueries
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlin.coroutines.coroutineContext

internal class DefaultServersRepository(
    private val serversApi: ServersApi,
    private val serverQueries: DbServerQueries,
    private val configQueries: DbCustomVpnConfigQueries,
    private val settings: Settings,
    private val appSettingsRepository: AppSettingsRepository
) : ServersRepository {

    companion object {
        private const val KEY_CURRENT_SERVER = "current_server"

        private val logger: Logger = Logger.withTag("DefaultServersRepository")
    }

    private val _currentServer = MutableStateFlow<Server?>(null)
    override val currentServer: StateFlow<Server?> = _currentServer

    private val _allServers = MutableStateFlow<List<Server>>(listOf())
    override val allServers: StateFlow<List<Server>> = _allServers

    init {


//        if (currentServerId.isNotEmpty()) {
//            val server = serverQueries.selectAll()
//                .executeAsList()
//                .find { it.uuid == currentServerId }
//                ?.toDomain(configQueries.selectByServerId(currentServerId).executeAsList())
//
//            _currentServer.value = server
//        }
    }


    override suspend fun initServers() {
        val currentServerUuid = settings.getStringOrNull(KEY_CURRENT_SERVER)


        addCustomServers()

        serverQueries.selectAll()
            .asFlow()
            .mapToList(coroutineContext).collectLatest { dbServers ->
                _allServers.value = dbServers.map {
                    val config = configQueries
                        .selectByServerId(it.uuid)
                        .executeAsList()
                    it.toDomain(config)
                }

                if (_currentServer.value == null) {
                    setCurrentServer(currentServerUuid)
                }
            }


    }

    override suspend fun syncServers() {
//        setCurrentServer(uuid = "uuid2")
        val apiResult = serversApi.getAllServers().getOrThrow()
        val remoteServers = apiResult.items
        val remoteXrayConfig = apiResult.defaultConfig.xray
        appSettingsRepository.xrayConfig = remoteXrayConfig.toLocalXray()

        serverQueries.transaction {
            val localIds = serverQueries.selectIds().executeAsList()
            val remoteIds = remoteServers.map { it.uuid }
            localIds.forEach { localId ->
                if (!remoteIds.contains(localId)) {
                    deleteServerFromDataBase(localId)
                }
            }
        }

        remoteServers.map { server ->
            server.config?.let { config ->
                saveConfigsToDatabase(server.uuid, config)
            }
        }

        saveServersToDatabase(remoteServers)
    }

    private fun saveServersToDatabase(remoteServers: List<RemoteServer>) {
        remoteServers.map { it.toDbServer() }.forEach(serverQueries::upsertDbServer)
    }

    private fun saveConfigsToDatabase(uuid: String, configs: Map<String, String?>) {
        configs.map { it.toDbConfig(uuid) }.forEach(configQueries::upsertDbCustomConfig)
    }


    override fun getAllServersSync(): List<Server> {
        return _allServers.value
    }

    override suspend fun getById(uuid: String): Server? {
        return serverQueries.getById(uuid).executeAsOneOrNull()
            ?.toDomain(configQueries.selectByServerId(uuid).executeAsList())
    }

    override suspend fun setLastConnectionTime(uuid: String, timeInMillis: Long) {
        serverQueries.updateServerLastConnectionTime(uuid, timeInMillis)
    }

    override suspend fun getAllServers(): Flow<List<Server>> = flow {
        emit(
            serverQueries.selectAll().executeAsList().map { dbServer ->
                val config = configQueries
                    .selectByServerId(dbServer.uuid)
                    .executeAsList()
                dbServer.toDomain(config)
            }
        )

        val apiResult = serversApi.getAllServers().getOrThrow()
        emit(apiResult.items.map {
            it.config?.forEach { configs ->
                configQueries.upsertDbCustomConfig(configs.toDbConfig(it.uuid))
            }
            val configs = configQueries.selectByServerId(it.uuid).executeAsList()
            it.toDbServer().toDomain(configs)
        })


//        if (settings.getStringOrNull(KEY_CURRENT_SERVER) == null) {
//            setCurrentServer(
//                apiResult.items.first { it.source == RemoteServerSource.SERVICE }.toDbServer()
//                    .toDomain(null)
//            )
//        }

        emitAll(
            serverQueries.selectAll()
                .asFlow()
                .mapToList(coroutineContext)
                .distinctUntilChanged()
                .map {
                    it.map { dbServer ->
                        val config = configQueries
                            .selectByServerId(dbServer.uuid)
                            .executeAsList()
                        dbServer.toDomain(config)
                    }
                }
        )
    }.flowOn(Dispatchers.IO)


    override fun getLatestConnectedXrayServer(): Server? {
        return serverQueries.selectAll()
            .executeAsList()
            .filter { it.protocol.uppercase() == ConnectionProtocol.XRAY.name }
            .sortedByDescending { it.lastConnectionTime }
            .first().run {
                toDomain(configQueries.selectByServerId(this.uuid).executeAsList())
            }
    }

    /** If [uuid] is null, first [ServerSource.SERVICE] server will set as [currentServer]*/
    override fun setCurrentServer(uuid: String?) {
        Logger.i("Custom Server set($uuid)")
        val currentServer = _allServers.value.find { it.uuid == uuid }
            ?: _allServers.value.firstOrNull { it.source == ServerSource.SERVICE }
        _currentServer.value = currentServer
        settings[KEY_CURRENT_SERVER] = uuid
    }

    private fun setCurrentServer(server: Server) {
        _currentServer.value = server
        settings[KEY_CURRENT_SERVER] = server.uuid
    }

    override suspend fun addCustomServer(server: CustomServerDto) {
//        val response = serversApi.sendCustomServer(customServerDto = server)
        val uuid = uuid4()
        addCustomServerToDatabase(uuid, server)
        Logger.d("DEBUGG!!! config = ${server.config?.get("config")}")

        setCurrentServer(server.toDomainServerForTest(uuid))
    }

    override suspend fun editCustomServer(uuid: String, server: CustomServerDto) {
//        serversApi.editCustomServer(uuid = uuid, customServerDto = server)
        editCustomServerInDatabase(uuid, server)
    }

    override suspend fun deleteCustomServer(uuid: String) {
//        serversApi.deleteCustomServer(uuid = uuid)
        deleteServerFromDataBase(uuid)
    }

    override suspend fun setServerFavorite(uuid: String, isFavourite: Boolean) {
//        if (isFavourite) {
//            serversApi.addServerToFavorite(uuid)
//        } else {
//            serversApi.deleteServerFromFavorite(uuid)
//        }
        serverQueries.updateServerIsFavourite(uuid, isFavourite)
    }

    /** Clear [allServers] in Local Database and [currentServer]*/
    override fun clearServers() {
        serverQueries.clearServers()
        _currentServer.value = null
        settings[KEY_CURRENT_SERVER] = null
    }

    private fun addCustomServerToDatabase(uuid: String, server: CustomServerDto) {
        logger.i("Add Custom server")
        settings[KEY_CURRENT_SERVER] = uuid

        saveConfigsToDatabase(uuid = uuid, configs = server.config ?: mapOf())
        serverQueries.addServer(server.toDbServer(uuid))
        server.config?.forEach {
            configQueries.addConfig(it.toDbConfig(uuid))
        }
    }

    private fun editCustomServerInDatabase(uuid: String, serverData: CustomServerDto) {
        serverQueries.updateServerName(uuid, serverData.name)
        serverQueries.updateServerIsPublic(uuid, serverData.isPublic)
        serverData.config?.forEach {
            configQueries.updateConfig(
                uuid,
                it.key,
                it.value
            )
        }
    }

    private fun deleteServerFromDataBase(uuid: String) {
        if (_currentServer.value?.uuid == uuid) {
            _currentServer.value = null
        }
        serverQueries.deleteServer(uuid)
    }


    private fun addCustomServers() {

        addCustomServerToDatabase(
            uuid = "uuid0",
            CustomServerDto(
                host = "45.12.132.106",
                name = "🇭🇺 Hungary",
                isPublic = false,
                config = mapOf(
                    "ip" to "45.12.132.106",
                    "port" to "443",
                    "uud" to "f2d1801b-4f29-410a-b737-f309702ad07d",
                    "type" to "TCP",
                    "pbk" to "_8_osxsVmQLiZPGd8s5COReCf5KdivZyOeCIvO6LuzE",
                    "sid" to "9abde835b1c6d366",
                    "security" to "REALITY",
                    "flow" to "XTLS",
                    "xrayConfig" to generateXrayConfig(
                        address = "45.12.132.106",
                        port = 443,
                        uuid = "f2d1801b-4f29-410a-b737-f309702ad07d",
                        pbk = "_8_osxsVmQLiZPGd8s5COReCf5KdivZyOeCIvO6LuzE",
                        sid = "9abde835b1c6d366"
                    )
                ),
                protocol = ConnectionProtocol.XRAY.name
            )
        )

        addCustomServerToDatabase(
            uuid = "uuid1",
            CustomServerDto(
                host = "95.164.3.58",
                name = "🇳🇱 Netherlands",
                isPublic = false,
                config = mapOf(
                    "ip" to "95.164.3.58",
                    "port" to "443",
                    "uud" to "9c40d628-8ee7-47ec-a90e-9f832b8029ec",
                    "type" to "TCP",
                    "pbk" to "OspPCT0hnnsUv3qow8M40XCQ787svmqmO5j5SVy18F0",
                    "sid" to "cffb6fc1cb9a05be",
                    "security" to "REALITY",
                    "flow" to "XTLS",
                    "xrayConfig" to generateXrayConfig(
                        address = "95.164.3.58",
                        port = 443,
                        uuid = "9c40d628-8ee7-47ec-a90e-9f832b8029ec",
                        pbk = "OspPCT0hnnsUv3qow8M40XCQ787svmqmO5j5SVy18F0",
                        sid = "cffb6fc1cb9a05be"
                    )
                ),
                protocol = ConnectionProtocol.XRAY.name
            )
        )

        addCustomServerToDatabase(
            uuid = "uuid2",
            CustomServerDto(
                host = "103.35.189.137",
                name = "🇺🇸 United States",
                isPublic = false,
                config = mapOf(
                    "ip" to "103.35.189.137",
                    "port" to "443",
                    "uud" to "417e051c-0332-461c-b11d-bc69a0decb6c",
                    "type" to "TCP",
                    "pbk" to "ywJsXdwAQ7rjMm2WAhNGZTvURhs7wnQcUeNSTHedSRw",
                    "sid" to "841a96b99c532951",
                    "security" to "REALITY",
                    "flow" to "XTLS",
                    "xrayConfig" to generateXrayConfig(
                        address = "103.35.189.137",
                        port = 443,
                        uuid = "417e051c-0332-461c-b11d-bc69a0decb6c",
                        pbk = "ywJsXdwAQ7rjMm2WAhNGZTvURhs7wnQcUeNSTHedSRw",
                        sid = "841a96b99c532951"
                    )
                ),
                protocol = ConnectionProtocol.XRAY.name
            )
        )

        addCustomServerToDatabase(
            uuid = "uuid3",
            CustomServerDto(
                host = "185.252.215.166",
                name = "🇩🇪 Germany",
                isPublic = false,
                config = mapOf(
                    "ip" to "185.252.215.166",
                    "port" to "443",
                    "uud" to "d0d2618f-8939-4f7d-a2be-7e2fd53599fc",
                    "type" to "TCP",
                    "pbk" to "-LlfTnW0WiE3uIqE6uF9wc8XmdBnhuxwj5tC3LZ4zEE",
                    "sid" to "98eb1478482b58a6",
                    "security" to "REALITY",
                    "flow" to "XTLS",
                    "xrayConfig" to generateXrayConfig(
                        address = "185.252.215.166",
                        port = 443,
                        uuid = "d0d2618f-8939-4f7d-a2be-7e2fd53599fc",
                        pbk = "-LlfTnW0WiE3uIqE6uF9wc8XmdBnhuxwj5tC3LZ4zEE",
                        sid = "98eb1478482b58a6"
                    )
                ),
                protocol = ConnectionProtocol.XRAY.name
            )
        )

        addCustomServerToDatabase(
            uuid = "uuid4",
            CustomServerDto(
                host = "185.74.222.141",
                name = "🇭🇰 Hong Kong",
                isPublic = false,
                config = mapOf(
                    "ip" to "185.74.222.141",
                    "port" to "443",
                    "uud" to "5dfdee2c-4914-420f-a3e2-b157bb3042d5",
                    "type" to "TCP",
                    "pbk" to "6lp3aFhqM1IW0UR7AVmmJ6j26E9b8PNhJpfuXaMowl0",
                    "sid" to "12b5184c1938238c",
                    "security" to "REALITY",
                    "flow" to "XTLS",
                    "xrayConfig" to generateXrayConfig(
                        address = "185.74.222.141",
                        port = 443,
                        uuid = "5dfdee2c-4914-420f-a3e2-b157bb3042d5",
                        pbk = "6lp3aFhqM1IW0UR7AVmmJ6j26E9b8PNhJpfuXaMowl0",
                        sid = "12b5184c1938238c"
                    )
                ),
                protocol = ConnectionProtocol.XRAY.name
            )
        )

        addCustomServerToDatabase(
            uuid = "uuid5",
            CustomServerDto(
                host = "103.35.189.137",
                name = "🇺🇸 United States (Open VPN)",
                isPublic = false,
                config = mapOf(
                    Pair(
                        CustomConfigFields.OVPN_FIELD_CONFIG, """client
proto udp
explicit-exit-notify
remote 103.35.189.137 1194
dev tun
resolv-retry infinite
nobind
persist-key
persist-tun
remote-cert-tls server
verify-x509-name server_YmWZ6PodugyhGSUZ name
auth SHA256
auth-nocache
cipher AES-128-GCM
tls-client
tls-version-min 1.2
tls-cipher TLS-ECDHE-ECDSA-WITH-AES-128-GCM-SHA256
ignore-unknown-option block-outside-dns
setenv opt block-outside-dns # Prevent Windows 10 DNS leak
verb 3
<ca>
-----BEGIN CERTIFICATE-----
MIIB1jCCAX2gAwIBAgIUQQe8zdPo/1Yy3H1O2Rch1zG4GsUwCgYIKoZIzj0EAwIw
HjEcMBoGA1UEAwwTY25fajd1dDRGWVpCSnBMTWNUUDAeFw0yNTA1MjMxMzE5NTFa
Fw0zNTA1MjExMzE5NTFaMB4xHDAaBgNVBAMME2NuX2o3dXQ0RllaQkpwTE1jVFAw
WTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAATRZOfmIEWaV87cTVGI01imf7nrTuik
KZ6/j2S63s5k9vBPe+Y8Ki89Qx6FWyoq75Tz1BZlmxUFCJUTezzaJUSKo4GYMIGV
MAwGA1UdEwQFMAMBAf8wHQYDVR0OBBYEFLaZYQe/iZ2JPw68Re6ArjmebIW8MFkG
A1UdIwRSMFCAFLaZYQe/iZ2JPw68Re6ArjmebIW8oSKkIDAeMRwwGgYDVQQDDBNj
bl9qN3V0NEZZWkJKcExNY1RQghRBB7zN0+j/VjLcfU7ZFyHXMbgaxTALBgNVHQ8E
BAMCAQYwCgYIKoZIzj0EAwIDRwAwRAIgZ+2N40KI6ne5qUv34Wf3UyvNVmZP9+cN
a+M/WfrT5kACIDvex2hA6V4OEPlfJEfEzVCudc5sht343dJ0lHCqjIwT
-----END CERTIFICATE-----
</ca>
<cert>
-----BEGIN CERTIFICATE-----
MIIB2TCCAX6gAwIBAgIQF44eUm1OWYPWbCvAyWpLaDAKBggqhkjOPQQDAjAeMRww
GgYDVQQDDBNjbl9qN3V0NEZZWkJKcExNY1RQMB4XDTI1MDUyMzEzMjAwMloXDTM1
MDUyMTEzMjAwMlowETEPMA0GA1UEAwwGdnBucnVuMFkwEwYHKoZIzj0CAQYIKoZI
zj0DAQcDQgAE6wnwcBR6P9zyo/X/vtay4oRs8UyExWwY7JQ2kwO7iNcnS44L8H6i
Noo1KdJ8CkwOLMcA4YM+aTr+EjWGQUmoaKOBqjCBpzAJBgNVHRMEAjAAMB0GA1Ud
DgQWBBSSxBpg7i3UsH7/q39Yp4nG27ZYqjBZBgNVHSMEUjBQgBS2mWEHv4mdiT8O
vEXugK45nmyFvKEipCAwHjEcMBoGA1UEAwwTY25fajd1dDRGWVpCSnBMTWNUUIIU
QQe8zdPo/1Yy3H1O2Rch1zG4GsUwEwYDVR0lBAwwCgYIKwYBBQUHAwIwCwYDVR0P
BAQDAgeAMAoGCCqGSM49BAMCA0kAMEYCIQCMZybxl+QMWVv5Ib9jvTnxwwLAtjua
z9/uu42E/wbtZAIhAIVcrlQuBSE/HRmtb6y2qgQvc0/ZWNzzTQkRRNZUXOUK
-----END CERTIFICATE-----
</cert>
<key>
-----BEGIN PRIVATE KEY-----
MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgDx3LQw4TuXSnKsQS
p8BVgzmpgXxLdziFSuqdkTxgZzWhRANCAATrCfBwFHo/3PKj9f++1rLihGzxTITF
bBjslDaTA7uI1ydLjgvwfqI2ijUp0nwKTA4sxwDhgz5pOv4SNYZBSaho
-----END PRIVATE KEY-----
</key>
<tls-crypt>
-----BEGIN OpenVPN Static key V1-----
9f3799456c5c0ead3d0ab823492023db
1f42135430c768d87936e5d03e2e2ca0
81f37047196f815713281b17619ddb9b
c05d6ad53d6a8773e2cb560d190338fe
32479bd17e8b01234d0418372437b644
790ed6152799d3e0250b1894f35b8c18
fbc36258d61af050ced2914a25a0ea91
ef695fde34e2db8d99d680d9716148c0
6e2d4d963523dd06037673ba689c842f
753513efb64580285226dff7cfd0657b
1d35b4ec879e62ceb4ad88bda51c49ab
6e4f3f02ed69ee10d85977177da82805
a70fa451df571f6fec97c80306233954
5f7d1e9529d68523fc894bcde440bdcd
534bab1216d1dd8ccccfe5ee77e1374e
1682bd1abc7b4ad2f8be65d5ad2af029
-----END OpenVPN Static key V1-----
</tls-crypt>
""".trimIndent()
                    ),
                    Pair(CustomConfigFields.OVPN_FIELD_USERNAME, ""),
                    Pair(CustomConfigFields.OVPN_FIELD_PASSWORD, "")
                ),
                protocol = ConnectionProtocol.OPENVPN.name
            )
        )



        addCustomServerToDatabase(
            uuid = "uuid6",
            CustomServerDto(
                host = "185.74.222.141",
                name = "🇭🇰 Hong Kong (Open VPN)",
                isPublic = false,
                config = mapOf(
                    Pair(
                        CustomConfigFields.OVPN_FIELD_CONFIG, """client
proto udp
explicit-exit-notify
remote 185.74.222.141 1194
dev tun
resolv-retry infinite
nobind
persist-key
persist-tun
remote-cert-tls server
verify-x509-name server_eJwe6RenshaY1PvI name
auth SHA256
auth-nocache
cipher AES-128-GCM
tls-client
tls-version-min 1.2
tls-cipher TLS-ECDHE-ECDSA-WITH-AES-128-GCM-SHA256
ignore-unknown-option block-outside-dns
setenv opt block-outside-dns # Prevent Windows 10 DNS leak
verb 3
<ca>
-----BEGIN CERTIFICATE-----
MIIB2DCCAX2gAwIBAgIUP0cXvclLokACcyBAOT/+h1uWZcwwCgYIKoZIzj0EAwIw
HjEcMBoGA1UEAwwTY25fUVJ5ZDkxc0VIeUlUZ0xTRDAeFw0yNTA1MjMxMzI0MTNa
Fw0zNTA1MjExMzI0MTNaMB4xHDAaBgNVBAMME2NuX1FSeWQ5MXNFSHlJVGdMU0Qw
WTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAQgaWSVCbXxhpd/iiRpEIwevOxcymJB
wFptvKER3sb5dYwmkvKJoEFTFLEZf648ielUXOC6iCFaxJUjj4CGN8YKo4GYMIGV
MAwGA1UdEwQFMAMBAf8wHQYDVR0OBBYEFAGcWiVh3yzOGOHb+gbmELZMislQMFkG
A1UdIwRSMFCAFAGcWiVh3yzOGOHb+gbmELZMislQoSKkIDAeMRwwGgYDVQQDDBNj
bl9RUnlkOTFzRUh5SVRnTFNEghQ/Rxe9yUuiQAJzIEA5P/6HW5ZlzDALBgNVHQ8E
BAMCAQYwCgYIKoZIzj0EAwIDSQAwRgIhALjpiwYjSkHm+frxSjSaRXDhPbId3M9X
UCY5rlIcZbggAiEA/iXzGhmByaR7C3+nMvPpDbiKb7qcZzZ9O596QqEVx/k=
-----END CERTIFICATE-----
</ca>
<cert>
-----BEGIN CERTIFICATE-----
MIIB2jCCAYCgAwIBAgIRAMAnA1972lxhd9s12O946CowCgYIKoZIzj0EAwIwHjEc
MBoGA1UEAwwTY25fUVJ5ZDkxc0VIeUlUZ0xTRDAeFw0yNTA1MjMxMzI0MjNaFw0z
NTA1MjExMzI0MjNaMBIxEDAOBgNVBAMMB3J1bnZwbjIwWTATBgcqhkjOPQIBBggq
hkjOPQMBBwNCAASjmfT/sivKvrEawJZLaD3g123+n5fTuPkL3blUrEJWqp69MkYr
MCQptbwZZfxRKOp2EWqkMzez9DpvCLCaGz7go4GqMIGnMAkGA1UdEwQCMAAwHQYD
VR0OBBYEFOW8B060lv5bmqBUty5uvvoJQ23wMFkGA1UdIwRSMFCAFAGcWiVh3yzO
GOHb+gbmELZMislQoSKkIDAeMRwwGgYDVQQDDBNjbl9RUnlkOTFzRUh5SVRnTFNE
ghQ/Rxe9yUuiQAJzIEA5P/6HW5ZlzDATBgNVHSUEDDAKBggrBgEFBQcDAjALBgNV
HQ8EBAMCB4AwCgYIKoZIzj0EAwIDSAAwRQIhAJDpcDukcmIg/LgUKvtd49IbNhgY
qyQThHhNA7QzvpTHAiABJjlfBllrzLj67ilcDPOBZSoCeW1nq0Rb0RF5y6kEIg==
-----END CERTIFICATE-----
</cert>
<key>
-----BEGIN PRIVATE KEY-----
MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgQ5DL6g7Faf30gF/T
+nLpvlSA68EfrhI8O5yjwP3d+rahRANCAASjmfT/sivKvrEawJZLaD3g123+n5fT
uPkL3blUrEJWqp69MkYrMCQptbwZZfxRKOp2EWqkMzez9DpvCLCaGz7g
-----END PRIVATE KEY-----
</key>
<tls-crypt>
-----BEGIN OpenVPN Static key V1-----
55c11397603d8f106104eb3e98d210f6
bf4dd4e38df91790a3dddd002b0b1bb9
fa6e809b5605ace87ffa9255c0820cd0
ad345410483ecc0234a7548c8fbece82
8f0c202a6615725ce97357f491412b03
5fdfcc9d573d94a2043722a694fce7d8
3c7e36b3cd24b26327c8a85c33c1e85a
3e806e2fd74ca3547fe345e7c7d91528
dc8cd40e4e92721a9cfcb88bf7d87f1c
a1491b127fb6e8b392bf921a396d4882
a5f79e7ceea9ae33ae6e86f979042822
c15f29d1cbde1d734e82f822dde18bc1
71089b4e5ccb2599d30233cd91b20c97
481b86166669a5b041308d909654bf05
6f92835a888072197bb0aaba879a8ff2
64f21eec86141cbdca204e234949fead
-----END OpenVPN Static key V1-----
</tls-crypt>
""".trimIndent()
                    ),
                    Pair(CustomConfigFields.OVPN_FIELD_USERNAME, ""),
                    Pair(CustomConfigFields.OVPN_FIELD_PASSWORD, "")
                ),
                protocol = ConnectionProtocol.OPENVPN.name
            )
        )

    }


    fun generateXrayConfig(
        address: String,
        port: Int,
        uuid: String,
        pbk: String,
        sid: String
    ): String {
        return """
    {
      "dns": {
        "hosts": {
          "domain:googleapis.cn": "googleapis.com"
        },
        "servers": ["1.1.1.1"]
      },
      "inbounds": [
        {
          "listen": "127.0.0.1",
          "port": 10808,
          "protocol": "socks",
          "settings": {
            "auth": "noauth",
            "udp": true,
            "userLevel": 8
          },
          "sniffing": {
            "enabled": false
          }
        },
        {
          "listen": "127.0.0.1",
          "port": 10809,
          "protocol": "http",
          "settings": {
            "userLevel": 8
          },
          "tag": "http"
        }
      ],
      "log": {
        "loglevel": "warning"
      },
      "outbounds": [
        {
          "mux": {
            "concurrency": -1,
            "enabled": false,
            "xudpConcurrency": 8,
            "xudpProxyUDP443": ""
          },
          "protocol": "vless",
          "settings": {
            "vnext": [
              {
                "address": "$address",
                "port": $port,
                "users": [
                  {
                    "encryption": "none",
                    "flow": "xtls-rprx-vision",
                    "id": "$uuid",
                    "level": 8,
                    "security": "auto"
                  }
                ]
              }
            ]
          },
          "streamSettings": {
            "network": "TCP",
            "security": "REALITY",
            "wsSettings": {
              "headers": {
                "Host": ""
              },
              "path": ""
            },
            "realitySettings": {
              "shortId": "$sid",
              "fingerprint": "chrome",
              "publicKey": "$pbk",
              "spiderX": "",
              "show": false,
              "serverName": "www.google.com"
            }
          },
          "tag": "proxy"
        },
        {
          "protocol": "freedom",
          "settings": {},
          "tag": "direct"
        },
        {
          "protocol": "blackhole",
          "settings": {
            "response": {
              "type": "http"
            }
          },
          "tag": "block"
        }
      ],
      "routing": {
        "domainStrategy": "IPIfNonMatch",
        "rules": [
          {
            "ip": ["1.1.1.1"],
            "outboundTag": "proxy",
            "port": "53",
            "type": "field"
          }
        ]
      }
    }
    """.trimIndent()
    }


}


fun DbServerQueries.upsertDbServer(dbServer: DbServer) {
    this.upsert(
        uuid = dbServer.uuid,
        name = dbServer.name,
        host = dbServer.host,
        protocol = dbServer.protocol,
        source = dbServer.source,
        isFavorite = dbServer.isFavorite,
        isPublic = dbServer.isPublic,

        //Nullable Fields
        country = dbServer.country,
        city = dbServer.city,
        iso = dbServer.iso,
        latitude = dbServer.latitude,
        longitude = dbServer.longitude
    )
}

fun DbCustomVpnConfigQueries.upsertDbCustomConfig(dbCustomVpnConfig: DbCustomVpnConfig) {
    this.upsert(
        uniqueUpdateKey = dbCustomVpnConfig.serverUuid + dbCustomVpnConfig.confKey,
        serverId = dbCustomVpnConfig.serverUuid,
        confKey = dbCustomVpnConfig.confKey,
        confValue = dbCustomVpnConfig.confValue,
    )
}
