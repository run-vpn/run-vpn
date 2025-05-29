package com.runvpn.app.data.device.data.api

import com.runvpn.app.core.network.ApiResponse
import com.runvpn.app.data.device.data.models.device.ChangeDeviceNameRequest
import com.runvpn.app.data.device.data.models.device.DeviceDto
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.device.data.models.device.register.RegisterResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

interface DeviceApi {

    @POST("api/devices/")
    suspend fun registerDevice(@Body deviceInfo: DeviceInfo): ApiResponse<RegisterResponse>

    @GET("api/devices")
    suspend fun getUserDevices(): ApiResponse<List<DeviceDto>>

    @DELETE("api/devices/{deviceUuid}")
    suspend fun deleteDevice(
        @Header("Authorization") token: String,
        @Path("deviceUuid") deviceUuid: String
    ): HttpResponse

    @PATCH("api/devices/{deviceUuid}")
    suspend fun changeDeviceName(
        @Path("deviceUuid") deviceUuid: String,
        @Body changeDeviceNameRequest: ChangeDeviceNameRequest
    ): HttpResponse

}
