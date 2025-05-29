package com.runvpn.app.data.device.data.models.user

import com.runvpn.app.data.device.domain.models.user.UserShortData
import kotlinx.serialization.Serializable

@Serializable
data class UserShortDataDto(
    val balance: Int,
    val contacts: List<UserContactDto>
)

fun UserShortDataDto.toDomain() = UserShortData(
    balanceInCent = balance,
    contacts = contacts.map { it.toDomain() }
)
