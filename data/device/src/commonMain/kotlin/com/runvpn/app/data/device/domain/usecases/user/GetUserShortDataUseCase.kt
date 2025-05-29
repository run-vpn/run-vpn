package com.runvpn.app.data.device.domain.usecases.user

import com.runvpn.app.data.device.domain.UserRepository

class GetUserShortDataUseCase(
    private val userRepository: UserRepository
) {


    suspend operator fun invoke() = runCatching { userRepository.getUserShortData() }

}
