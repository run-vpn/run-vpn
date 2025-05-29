package com.runvpn.app.domain

import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository

class SwitchTokensUseCase(
    private val appSettingsRepository: AppSettingsRepository,
    private val clearAppCacheUseCase: ClearAppCacheUseCase,
) {

    private var isSwitchInProgress = false

    /** Вызывается когда из бекенда приходит запрос [401 Unauthorized].
     * Устанавливает Анонимный токен [AppSettingsRepository.appToken] как текущий.
     * Если текущий токе был анонимным, то происходит
     * сброс данных и повторная регистрация девайса.*/
    operator fun invoke(): String? {
        if (isSwitchInProgress) return null
        isSwitchInProgress = true

        clearAppCacheUseCase()

        val currentToken = appSettingsRepository.appToken
        val anonymousToken = appSettingsRepository.appAnonymousToken

        if (currentToken == anonymousToken) {
            appSettingsRepository.appToken = null
        } else {
            appSettingsRepository.appToken = anonymousToken
        }

        isSwitchInProgress = false
        return appSettingsRepository.appToken
    }

}
