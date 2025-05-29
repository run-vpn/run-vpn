package com.runvpn.app.core.network

import io.ktor.client.plugins.api.createClientPlugin

private const val HEADER_AUTHORIZATION = "Authorization"

val AuthHeader = createClientPlugin("AuthHeader", ::AuthHeaderPluginConfig) {
    onRequest { request, _ ->
        val token =
            this@createClientPlugin.pluginConfig.getTokenDelegate?.invoke() ?: return@onRequest

        request.headers.append(
            HEADER_AUTHORIZATION,
            "Bearer $token"
        )
    }
}

class AuthHeaderPluginConfig {
    var getTokenDelegate: (() -> String)? = null
}
