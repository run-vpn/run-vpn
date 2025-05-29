package com.runvpn.app.feature.serveradd

import com.runvpn.app.data.servers.data.dto.CustomServerDto

interface CustomServerConfigComponent {

    suspend fun getServerConfig(): CustomServerDto?

}
