package com.runvpn.app.data.device.data.models.user

import com.runvpn.app.data.device.domain.models.user.ContactType
import com.runvpn.app.data.device.domain.models.user.UserContact
import kotlinx.serialization.Serializable

@Serializable
data class UserContactDto(
    val type: ContactTypeDto,
    val value: String,
    val verifiedAt: String?
)


fun UserContactDto.toDomain() = UserContact(
    type = ContactType.valueOf(type.name),
    value = value,
    verifiedAt = verifiedAt
)
