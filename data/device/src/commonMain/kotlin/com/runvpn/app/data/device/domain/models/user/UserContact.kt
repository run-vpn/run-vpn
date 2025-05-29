package com.runvpn.app.data.device.domain.models.user


data class UserContact(
    val type: ContactType,
    val value: String,
    val verifiedAt: String?
)
