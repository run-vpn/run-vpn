package com.runvpn.app.data.device.domain.models.user

data class UserShortData(
    val balanceInCent: Int,
    val contacts: List<UserContact>
) {
    val balanceInDollar = balanceInCent / 100.0
    val email = contacts.find { it.type == ContactType.EMAIL }?.value
}
