package com.runvpn.app.core

import com.runvpn.app.core.network.NetworkApiFactory
import com.runvpn.app.core.network.NetworkStatus
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module


fun networkModule(
    baseUrl: String,
    appVersionCode: Int,
    appVersionName: String,
    platformName: String,
    onConnectionStatusChanged: (NetworkStatus) -> Unit
) = module {
    single<Json> { createJson() }

    single {
        NetworkApiFactory(
            json = get(),
            baseUrl = baseUrl,
            appVersionCode = appVersionCode.toString(),
            appVersionName = appVersionName,
            platform = platformName,
            onConnectionStatusChanged = onConnectionStatusChanged
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun createJson(): Json {
    return Json {
        explicitNulls = false
        encodeDefaults = true
        ignoreUnknownKeys = true
        isLenient = false
    }
}
