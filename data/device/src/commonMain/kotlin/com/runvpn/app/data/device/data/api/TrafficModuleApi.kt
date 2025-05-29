package com.runvpn.app.data.device.data.api

import com.runvpn.app.data.device.data.models.traffic.TrafficInfoDevicesResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface TrafficModuleApi {

    @GET("api/traffics")
    suspend fun getTrafficTotal(
        @Header("Authorization") token: String,
        @Header("x-device-id") xDeviceId: String,
        @Query("device_ids[]") devices: List<String>
    ): TrafficInfoDevicesResponse

    @GET("api/traffics/{day}")
    suspend fun getTrafficByDay(
        @Header("Authorization") token: String,
        @Header("x-device-id") xDeviceId: String,
        @Path("day") day: String,
        @Query("device_ids[]") devices: List<String>
    ): TrafficInfoDevicesResponse

}
