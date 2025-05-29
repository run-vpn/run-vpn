package com.runvpn.app.data.device.domain

import com.runvpn.app.data.device.data.models.user.ChangeUserPasswordRequest
import com.runvpn.app.data.device.domain.models.user.UserShortData

interface UserRepository {

    suspend fun getUserShortData(): UserShortData

    suspend fun changeUserPassword(changeUserPasswordRequest: ChangeUserPasswordRequest): Result<Boolean>


}
