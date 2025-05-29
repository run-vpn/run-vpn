package com.runvpn.app

import kotlinx.coroutines.CoroutineScope

//private val sdkManager: SdkManager
//    get() = SharedSDK.koinApplication.koin.get()
//
//private val sdkLogsManager: SdkLogsManager
//    get() = SharedSDK.koinApplication.koin.get()
//
//private val APP_UUID = if (BuildConfig.DEBUG) APP_UUID_DEBUG else APP_UUID_RELEASE

/** Check notification permission before calling!*/
actual fun sdkStart(deviceUuid: String, scope: CoroutineScope) {
//    Logger.i("Sdk started $deviceUuid")
//    sdkLogsManager.startReceive(scope)
//    if (sdkManager.getStatus().isStopped)
//        sdkManager.runSdk(APP_UUID, deviceUuid)
}


/** Check notification permission before calling!*/
actual fun sdkSelectChannel(notificationId: Int) {
//    Logger.i("Sdk sdkSelectChannel $notificationId")
//    sdkManager.selectChannel(notificationId)
}
