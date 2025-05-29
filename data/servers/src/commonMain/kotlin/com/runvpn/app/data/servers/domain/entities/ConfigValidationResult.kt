package com.runvpn.app.data.servers.domain.entities

sealed interface ConfigValidationResult {
    data class Success<T>(val host: String, val config: T) : ConfigValidationResult
    data object AuthRequired : ConfigValidationResult
    data object ConfigInvalid : ConfigValidationResult
}
