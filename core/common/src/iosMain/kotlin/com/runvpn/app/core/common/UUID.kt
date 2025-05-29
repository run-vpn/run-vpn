package com.runvpn.app.core.common

import platform.Foundation.NSUUID

actual fun uuid4(): String = NSUUID().UUIDString()
