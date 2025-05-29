package com.runvpn.app

import kotlinx.coroutines.CoroutineScope

actual fun sdkStart(@Suppress("unused") deviceUuid: String, scope: CoroutineScope) {
    // ignored
}

actual fun sdkSelectChannel(@Suppress("unused") notificationId: Int) {
    //ignored
}
