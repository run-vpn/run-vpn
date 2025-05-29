package com.runvpn.app.android.vpn.ikev2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ikev2ConnectConfig(
    val host: String,
    val username: String,
    val password: String,
    val certificate: String
) : Parcelable
