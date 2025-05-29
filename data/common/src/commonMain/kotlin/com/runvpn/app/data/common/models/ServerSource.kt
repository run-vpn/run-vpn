package com.runvpn.app.data.common.models

enum class ServerSource {
    MINE,
    SERVICE,
    SHARED,
    UNDEFINED;


    companion object {
        val CUSTOM_SERVER_DEFAULT: String
            get() = MINE.name.lowercase()
    }

}
