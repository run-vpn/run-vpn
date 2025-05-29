package com.runvpn.app.data.device.domain

import com.runvpn.app.core.network.ApiResponse
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.device.data.models.device.register.RegisterResponse
import com.runvpn.app.data.device.data.models.traffic.TrafficModuleInfo
import com.runvpn.app.data.device.domain.models.device.Device
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.StateFlow

interface DeviceRepository {

    val deviceUuid: StateFlow<String?>
    val trafficModuleInfoData: StateFlow<TrafficModuleInfo?>

    suspend fun registerDevice(deviceInfo: DeviceInfo): ApiResponse<RegisterResponse>
    suspend fun getUserDevices(): ApiResponse<List<Device>>
    suspend fun deleteDevice(token: String, deviceUuid: String): HttpResponse
    suspend fun changeDeviceName(deviceUuid: String, newName: String): HttpResponse

}
