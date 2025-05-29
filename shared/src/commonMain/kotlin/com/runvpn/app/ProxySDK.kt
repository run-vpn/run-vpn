package com.runvpn.app

import kotlinx.coroutines.CoroutineScope

expect fun sdkStart(deviceUuid: String, scope: CoroutineScope)
expect fun sdkSelectChannel(notificationId: Int)
