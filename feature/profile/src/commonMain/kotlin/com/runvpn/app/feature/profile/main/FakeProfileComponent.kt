package com.runvpn.app.feature.profile.main

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.data.device.domain.models.user.ContactType
import com.runvpn.app.data.device.domain.models.user.UserContact
import com.runvpn.app.data.device.domain.models.user.UserShortData
import com.runvpn.app.data.settings.domain.Tariff

class FakeProfileComponent(
    private val isLoggedIn: Boolean = false,
    private val isLoading: Boolean = false
) : ProfileComponent {
    override val state: Value<ProfileComponent.State>
        get() = MutableValue(
            ProfileComponent.State(
                isLoading = isLoading,
                userShortData = UserShortData(
                    balanceInCent = 1337,
                    contacts = if (!isLoggedIn) listOf(
                        UserContact(
                            type = ContactType.EMAIL,
                            "testuser@test.com",
                            null
                        )
                    ) else listOf(),
                ),
                devices = listOf(),
                tariff = Tariff.FREE
            )
        )

    override fun onAuthClick() {
        TODO("Not yet implemented")
    }

    override fun onSubscriptionClick() {
        TODO("Not yet implemented")
    }

    override fun onReferralClicked() {
        TODO("Not yet implemented")
    }

    override fun onLogoutClicked() {
        TODO("Not yet implemented")
    }

    override fun onTrafficModuleClick() {
        TODO("Not yet implemented")
    }

    override fun onBuySubscriptionClick() {
        TODO("Not yet implemented")
    }

    override fun onActivatePromoClick() {
        TODO("Not yet implemented")
    }

    override fun onBalanceClicked() {
        TODO("Not yet implemented")
    }
}

