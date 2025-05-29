package com.runvpn.app.core.common

interface ClipboardManager {
    fun copy(value: String)

    fun paste(): String?
}
