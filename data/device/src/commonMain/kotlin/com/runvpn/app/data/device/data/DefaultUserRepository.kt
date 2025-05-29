package com.runvpn.app.data.device.data

import com.runvpn.app.data.device.data.api.UserApi
import com.runvpn.app.data.device.data.models.user.ChangeUserPasswordRequest
import com.runvpn.app.data.device.data.models.user.toDomain
import com.runvpn.app.data.device.domain.UserRepository
import com.runvpn.app.data.device.domain.models.user.UserShortData
import io.ktor.http.isSuccess

class DefaultUserRepository(private val userApi: UserApi) : UserRepository {

    override suspend fun getUserShortData(): UserShortData =
        userApi.getUserShortData().getOrThrow().toDomain()

    override suspend fun changeUserPassword(
        changeUserPasswordRequest: ChangeUserPasswordRequest
    ): Result<Boolean> {
        val response = runCatching {
            userApi.changeUserPassword(changeUserPasswordRequest)
        }.map {
            it.status.isSuccess()
        }

        return response
    }

}
