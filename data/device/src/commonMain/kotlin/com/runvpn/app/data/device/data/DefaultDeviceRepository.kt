package com.runvpn.app.data.device.data

import com.runvpn.app.core.network.ApiResponse
import com.runvpn.app.data.device.data.api.DeviceApi
import com.runvpn.app.data.device.data.models.device.ChangeDeviceNameRequest
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.device.data.models.traffic.TrafficModuleInfo
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.device.domain.models.device.Device
import com.runvpn.app.data.device.domain.models.toDomain
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class DefaultDeviceRepository(
    private val deviceApi: DeviceApi
) : DeviceRepository {

    private val _appUUID: MutableStateFlow<String?> = MutableStateFlow(null)
    override val deviceUuid: StateFlow<String?> = _appUUID

    private val _trafficModuleInfoData: MutableStateFlow<TrafficModuleInfo?> =
        MutableStateFlow(null)
    override val trafficModuleInfoData: StateFlow<TrafficModuleInfo?> = _trafficModuleInfoData

    override suspend fun registerDevice(deviceInfo: DeviceInfo) = deviceApi.registerDevice(deviceInfo)

    override suspend fun getUserDevices(): ApiResponse<List<Device>> {
        return deviceApi.getUserDevices().map { result -> result.map { it.toDomain() } }
    }

    override suspend fun deleteDevice(token: String, deviceUuid: String): HttpResponse =
        deviceApi.deleteDevice(token, deviceUuid)

    override suspend fun changeDeviceName(deviceUuid: String, newName: String): HttpResponse {
        return deviceApi.changeDeviceName(
            deviceUuid = deviceUuid,
            ChangeDeviceNameRequest(name = newName)
        )
    }
}

