package com.runvpn.app.data.servers.domain.entities

import com.runvpn.app.data.common.models.Server
import com.runvpn.app.data.settings.domain.SuggestedServersMode

data class SuggestedServers(
    val mode: SuggestedServersMode,
    val servers: List<Server>
)
