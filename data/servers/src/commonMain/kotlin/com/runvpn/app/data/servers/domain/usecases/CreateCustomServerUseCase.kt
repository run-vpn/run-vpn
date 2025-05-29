package com.runvpn.app.data.servers.domain.usecases

import com.runvpn.app.data.common.models.ConnectionProtocol
import com.runvpn.app.data.servers.data.dto.CustomServerDto

class CreateCustomServerUseCase {


    operator fun invoke(
        host: String,
        config: Map<String, String?>,
        protocol: ConnectionProtocol
    ) = CustomServerDto(
        host = host,
        name = null,
        config = config,
        protocol = protocol.name,
        isPublic = false,
    )
}
