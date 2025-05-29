package com.runvpn.app.data.device.domain.usecases.auth

import com.runvpn.app.data.device.data.models.user.ChangeUserPasswordRequest
import com.runvpn.app.data.device.domain.UserRepository

class SetUserPasswordUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(password: String, confirmPassword: String): Result<Boolean> {
        if (password != confirmPassword) return Result.failure(Exception(""))
        return userRepository.changeUserPassword(ChangeUserPasswordRequest(password))
    }

}
