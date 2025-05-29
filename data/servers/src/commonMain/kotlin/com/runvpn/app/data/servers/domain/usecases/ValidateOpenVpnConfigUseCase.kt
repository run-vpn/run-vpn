package com.runvpn.app.data.servers.domain.usecases

import com.runvpn.app.data.servers.domain.entities.ConfigValidationResult

class ValidateOpenVpnConfigUseCase {

    operator fun invoke(
        config: String,
        userName: String?,
        password: String?
    ): ConfigValidationResult {
        var authRequired = false
        var host = ""

        config.split("\n").forEach {
            if (it.contains("remote ")) {
                it.split(" ").getOrNull(1)?.let {
                    host = it
                }
            }

            if (it.contains("auth-user-pass")) {
                authRequired = true
            }
        }

        if (authRequired && (userName.isNullOrEmpty() || password.isNullOrEmpty())) {
            return ConfigValidationResult.AuthRequired
        }

        if (host.isNotEmpty() && config.isNotEmpty()) {
            return ConfigValidationResult.Success(host, config)
        }

        return ConfigValidationResult.ConfigInvalid
    }

}
