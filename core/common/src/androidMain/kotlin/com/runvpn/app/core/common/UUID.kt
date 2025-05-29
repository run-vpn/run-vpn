package com.runvpn.app.core.common

import java.util.UUID

actual fun uuid4(): String = UUID.randomUUID().toString()
