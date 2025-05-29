package com.runvpn.app.data.connection.models

data class Ikev2Config(
    val host: String,
    val username: String,
    val password: String,
    val certificateName: String,
    val certificate: String
)
