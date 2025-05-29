package com.runvpn.app.feature.subscription

import co.touchlab.kermit.Logger
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.device.domain.models.device.DeviceWithTariff
import com.runvpn.app.data.device.domain.models.user.UserShortData
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.subscription.domain.GetAvailableUserSubscriptionsUseCase
import com.runvpn.app.data.subscription.domain.GetDevicesWithTariffUseCase
import com.runvpn.app.data.subscription.domain.GetSubscriptionsCountUseCase
import com.runvpn.app.data.subscription.domain.entities.Subscription
import com.runvpn.app.data.subscription.domain.entities.SubscriptionsCount
import com.runvpn.app.tea.runtime.coroutines.Effect
import com.runvpn.app.tea.runtime.coroutines.Update
import com.runvpn.app.tea.runtime.coroutines.noEffects
import com.runvpn.app.tea.runtime.coroutines.with

object SubscriptionInfoFeature {
    data class State(
        val devicesInfo: List<DeviceWithTariff>,
        val currentDeviceUuid: String?,
        val isLoading: Boolean,
        val isError: Boolean,
        val userShortData: UserShortData?,
        val subscriptions: List<Subscription>,
        val subscriptionsCount: SubscriptionsCount
    )

    sealed class Message {
        data object Init : Message()
        data object ErrorResponseReceived : Message()

        data object OnResume : Message()

        data class DevicesResponse(val devicesInfo: List<DeviceWithTariff>) : Message()
        data class CurrentDeviceUuidResponse(val deviceUuid: String?) : Message()
        data class UserSubscriptionsReceived(val subscriptions: List<Subscription>) : Message()
        data class SubscriptionsCountReceived(val subscriptionsCount: SubscriptionsCount) :
            Message()
    }

    data class Dependencies(
        val deviceRepository: DeviceRepository,
        val settingsRepository: AppSettingsRepository,
        val getUserSubscriptionsUseCase: GetAvailableUserSubscriptionsUseCase,
        val getDevicesWithTariffUseCase: GetDevicesWithTariffUseCase,
        val getSubscriptionsCountUseCase: GetSubscriptionsCountUseCase,
    )

    object Logic {
        val initialUpdate = State(
            devicesInfo = listOf(),
            currentDeviceUuid = null,
            isLoading = false,
            isError = false,
            userShortData = null,
            subscriptions = listOf(),
            subscriptionsCount = SubscriptionsCount.DEFAULT
        )

        fun restore(state: State): Update<State, Message, Dependencies> = state with noEffects()

        fun update(message: Message, state: State): Update<State, Message, Dependencies> =
            when (message) {
                Message.Init -> handleInit(state)
                is Message.DevicesResponse -> handleDeviceResponse(
                    state,
                    message.devicesInfo
                )

                is Message.UserSubscriptionsReceived -> handleUserReceived(
                    state,
                    message.subscriptions
                )

                is Message.SubscriptionsCountReceived -> handleSubscriptionsCountReceived(
                    state,
                    message.subscriptionsCount
                )

                is Message.CurrentDeviceUuidResponse -> handleCurrentDeviceUuidResponse(
                    state,
                    message.deviceUuid
                )

                Message.ErrorResponseReceived -> handleDeviceErrorResponse(state)
                Message.OnResume -> handleOnResume(state)
            }

        private fun handleCurrentDeviceUuidResponse(
            state: State,
            deviceUuid: String?
        ): Update<State, Message, Dependencies> {
            return state.copy(
                currentDeviceUuid = deviceUuid
            ) with setOf(Effects.GetDevicesInfo(deviceUuid))
        }

        private fun handleSubscriptionsCountReceived(
            state: State,
            subscriptionsCount: SubscriptionsCount
        ): Update<State, Message, Dependencies> {
            return state.copy(
                isLoading = false,
                subscriptionsCount = subscriptionsCount
            ) with noEffects()
        }

        private fun handleOnResume(state: State): Update<State, Message, Dependencies> {
            return state.copy(isLoading = true) with setOf(
                Effects.GetUserSubscriptions(),
                Effects.GetSubscriptionsCount(),
                Effects.GetCurrentDevice()
            )
        }

        private fun handleDeviceErrorResponse(state: State): Update<State, Message, Dependencies> {
            return state.copy(isError = true) with noEffects()
        }

        private fun handleInit(state: State): Update<State, Message, Dependencies> {
            return state.copy(isLoading = true) with setOf(
                Effects.GetUserSubscriptions(),
                Effects.GetSubscriptionsCount(),
                Effects.GetCurrentDevice()
            )
        }

        private fun handleUserReceived(
            state: State,
            subscriptions: List<Subscription>
        ): Update<State, Message, Dependencies> {
            return state.copy(isLoading = false, subscriptions = subscriptions) with noEffects()
        }

        private fun handleDeviceResponse(
            state: State,
            devicesInfo: List<DeviceWithTariff>
        ): Update<State, Message, Dependencies> {
            Logger.d("Devices: $devicesInfo")
            return state.copy(
                devicesInfo = devicesInfo,
                isLoading = false,
                isError = false
            ) with noEffects()
        }
    }

    object Effects {

        class GetUserSubscriptions :
            Effect<Dependencies, Message> by Effect.onDefault.single({ deps ->
                val subscriptions = deps.getUserSubscriptionsUseCase()
                if (subscriptions.isSuccess) {
                    return@single Message.UserSubscriptionsReceived(subscriptions.getOrThrow())
                } else {
                    return@single Message.ErrorResponseReceived
                }
            })

        class GetCurrentDevice : Effect<Dependencies, Message> by Effect.onDefault.single({ deps ->
            val currentDeviceUuid = deps.settingsRepository.deviceUuid
            return@single Message.CurrentDeviceUuidResponse(currentDeviceUuid)
        })

        class GetDevicesInfo(currentDeviceUuid: String?) :
            Effect<Dependencies, Message> by Effect.onDefault.single({ deps ->
                val devicesWithTariffResult = deps.getDevicesWithTariffUseCase()
                if (devicesWithTariffResult.isSuccess) {
                    return@single Message.DevicesResponse(
                        devicesWithTariffResult.getOrThrow()
                            .sortedByDescending {
                                it.device.activity?.createdAt
                            }.sortedByDescending {
                                it.device.uuid == currentDeviceUuid
                            })
                } else {
                    return@single Message.ErrorResponseReceived
                }
            })

        class GetSubscriptionsCount :
            Effect<Dependencies, Message> by Effect.onDefault.single({ deps ->
                val subscriptionCount = deps.getSubscriptionsCountUseCase()
                if (subscriptionCount.isSuccess) {
                    return@single Message.SubscriptionsCountReceived(subscriptionCount.getOrThrow())
                } else {
                    return@single Message.ErrorResponseReceived
                }
            })
    }
}
