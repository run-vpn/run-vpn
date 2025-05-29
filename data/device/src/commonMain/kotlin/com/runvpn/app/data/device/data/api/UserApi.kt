package com.runvpn.app.data.device.data.api

import com.runvpn.app.core.network.ApiResponse
import com.runvpn.app.data.device.data.models.user.ChangeUserPasswordRequest
import com.runvpn.app.data.device.data.models.user.UserShortDataDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import io.ktor.client.statement.HttpResponse

interface UserApi {


    /** New endpoint to get User Short Data (balance and contacts)*/
    @GET("api/user")
    suspend fun getUserShortData(): ApiResponse<UserShortDataDto>


    @PATCH("api/user")
    suspend fun changeUserPassword(
        @Body changeUserPasswordRequest: ChangeUserPasswordRequest
    ): HttpResponse

}
