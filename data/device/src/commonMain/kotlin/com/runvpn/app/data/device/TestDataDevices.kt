package com.runvpn.app.data.device

import com.runvpn.app.data.device.data.models.device.Activity
import com.runvpn.app.data.device.data.models.device.DeviceDto
import com.runvpn.app.data.device.data.models.device.register.Hardware
import com.runvpn.app.data.device.data.models.device.register.Software
import com.runvpn.app.data.device.domain.models.toDomain
import com.runvpn.app.data.device.data.models.traffic.TrafficInfo
import com.runvpn.app.data.device.data.models.traffic.TrafficModuleInfo
import com.runvpn.app.data.device.data.models.device.DeviceApplication
import com.runvpn.app.data.device.domain.models.user.ContactType
import com.runvpn.app.data.device.domain.models.user.UserContact
import com.runvpn.app.data.device.domain.models.user.UserShortData
import kotlinx.datetime.Clock

object TestDataDevices {
    private val testDevice1Dto = DeviceDto(
        uuid = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        latestSubscriptionUuid = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        software = Software(
            name = "testSoft1",
            versionName = "1.0",
            versionCode = "2",
            platformName = "android"
        ),
        hardware = Hardware(
            brand = "Galaxy",
            name = "tablet",
            manufacture = "Samsung",
            productName = "S21",
            marketName = "Samsung Galaxy S21"
        ),
        name = "somename",
        activity = Activity("127.0.0.1", Clock.System.now()),
        application = DeviceApplication(
            versionName = "versionName",
            code = "code",
            source = "source"
        )
    )

    private val testDevice2Dto = DeviceDto(
        uuid = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        latestSubscriptionUuid = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        software = Software(
            name = "testSoft1",
            versionName = "1.0",
            versionCode = "2",
            platformName = "Android"
        ),
        hardware = Hardware(
            brand = "Pixel",
            name = "phone",
            manufacture = "Google",
            productName = "XL",
            marketName = "Google Pixel XL"
        ),
        name = "somename",
        activity = Activity("127.0.0.1", Clock.System.now()),
        application = DeviceApplication(
            versionName = "versionName",
            code = "code",
            source = "source"
        )
    )


    val testDevicesList = listOf(
        testDevice1Dto,
        testDevice2Dto,
    ).map { it.toDomain() }

    val testDevicesListEmpty = listOf<DeviceDto>().map { it.toDomain() }

    val testTrafficModuleInfo = TrafficModuleInfo(
        TrafficInfo(
            1 * 1024 * 1024,
            2 * 1024 * 1024,
            3 * 1024 * 1024
        ),
        TrafficInfo(
            4 * 1024 * 1024,
            5 * 1024 * 1024,
            6 * 1024 * 1024
        ),

        TrafficInfo(
            7 * 1024 * 1024,
            8 * 1024 * 1024,
            9 * 1024 * 1024
        ),

        TrafficInfo(
            10 * 1024 * 1024,
            11 * 1024 * 1024,
            12 * 1024 * 1024
        ),
    )


    val testUser = UserShortData(
        1000,
        listOf(UserContact(ContactType.EMAIL, "test@test.com", verifiedAt = null))
    )
}
