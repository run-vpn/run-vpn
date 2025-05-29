package com.runvpn.app.feature.trafficmodule

import com.runvpn.app.data.device.data.models.traffic.TrafficModuleInfo
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.tea.runtime.coroutines.Effect
import com.runvpn.app.tea.runtime.coroutines.Update
import com.runvpn.app.tea.runtime.coroutines.noEffects
import com.runvpn.app.tea.runtime.coroutines.with


object TrafficModuleFeature {
    data class State(
        val trafficModuleInfo: TrafficModuleInfo,
        val isLoading: Boolean,
        val isError: Boolean
    )

    sealed class Message {
        data object Update : Message()
        data class TrafficResponse(val trafficModuleInfo: TrafficModuleInfo) : Message()
    }

    data class Dependencies(
        val deviceRepository: DeviceRepository,
        val settingsRepository: AppSettingsRepository,
    )

    object Logic {
        val initialUpdate = State(
            trafficModuleInfo = TrafficModuleInfo(),
            isLoading = false,
            isError = false
        )

        fun restore(state: State): Update<State, Message, Dependencies> = state with noEffects()

        fun update(message: Message, state: State): Update<State, Message, Dependencies> =
            when (message) {
                Message.Update -> handleUpdate(state)
                is Message.TrafficResponse -> handleTrafficResponse(
                    state,
                    message.trafficModuleInfo
                )
            }

        private fun handleUpdate(state: State): Update<State, Message, Dependencies> {
            return state.copy(isLoading = true) with setOf(Effects.GetTraffic())
        }

        private fun handleTrafficResponse(
            state: State,
            trafficModuleInfo: TrafficModuleInfo
        ): Update<State, Message, Dependencies> {
            return state.copy(
                trafficModuleInfo = trafficModuleInfo,
                isLoading = false,
                isError = false
            ) with noEffects()
        }
    }

    object Effects {
        class GetTraffic : Effect<Dependencies, Message> by Effect.idle({ deps ->

            val token = deps.settingsRepository.appToken!!
            val deviceUuid = deps.settingsRepository.deviceUuid!!
            val appToken = deps.settingsRepository.appToken!!
//            val devicesUuids = deps.deviceRepository.getFullInfo(appToken, deviceUuid).devicesUuids
//
//            val traffic = deps.deviceRepository.getTrafficInfo(
//                token,
//                deviceUuid,
//                devicesUuids
//            )

//            return@single Message.TrafficResponse(traffic)
        })
    }
}
