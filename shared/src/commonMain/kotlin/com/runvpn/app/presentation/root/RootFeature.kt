package com.runvpn.app.presentation.root


import co.touchlab.kermit.Logger
import com.runvpn.app.data.connection.VpnConnectionManager
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.DeviceRepository
import com.runvpn.app.data.device.domain.UpdateRepository
import com.runvpn.app.data.device.domain.models.update.UpdateStatus
import com.runvpn.app.data.device.domain.usecases.device.CheckDeviceRegisterUseCase
import com.runvpn.app.data.device.domain.usecases.device.RegisterDeviceUseCase
import com.runvpn.app.data.device.domain.usecases.update.CheckAppUpdateAvailableUseCase
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import com.runvpn.app.data.settings.models.AppVersion
import com.runvpn.app.domain.InitApplicationDataUseCase
import com.runvpn.app.domain.LogOutUserUseCase
import com.runvpn.app.tea.message.data.MessageService
import com.runvpn.app.tea.message.domain.AppMessage
import com.runvpn.app.tea.runtime.coroutines.Effect
import com.runvpn.app.tea.runtime.coroutines.Update
import com.runvpn.app.tea.runtime.coroutines.noEffects
import com.runvpn.app.tea.runtime.coroutines.with
import io.ktor.client.HttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

object RootFeature {

    data class Dependencies(
        val deviceInfo: DeviceInfo,
        val deviceRepository: DeviceRepository,
        val appSettingsRepository: AppSettingsRepository,
        val httpClient: HttpClient,
        val fileDir: String,
        val appVersion: AppVersion,
        val updateRepository: UpdateRepository,
        val checkAppUpdateAvailableUseCase: CheckAppUpdateAvailableUseCase,
        val vpnConnectionManager: VpnConnectionManager,
        val registerDeviceUseCase: RegisterDeviceUseCase,
        val serversRepository: ServersRepository,
        val checkDeviceRegisterUseCase: CheckDeviceRegisterUseCase,
        val initApplicationDataUseCase: InitApplicationDataUseCase,
        val logOutUserUseCase: LogOutUserUseCase,
        val messageService: MessageService
    )

    data class State(
        val isRegistered: Boolean,
        val hasUpdate: Boolean,
        val updateStatus: UpdateStatus?
    )

    sealed class Message {

        data object Init : Message()

        data object SyncServers : Message()

        data class ShowMessage(val message: String) : Message()

        data object DomainNotReachable : Message()

        data object RegisterComplete : Message()
        data object RegisterError : Message()

        data class UpdateRequested(val packageName: String) : Message()
        data class UpdateResponse(val updateInfo: UpdateInfo) : Message()
        data class AppUpdateStatusChanged(val updateStatus: UpdateStatus?) : Message()

        data object NeedClearCache : Message()

    }

    object Logic {

        val initialUpdate = State(
            isRegistered = false,
            hasUpdate = false,
            updateStatus = null
        )

        fun restore(state: State): Update<State, Message, Dependencies> = state with noEffects()

        fun update(message: Message, state: State): Update<State, Message, Dependencies> =
            when (message) {
                is Message.Init -> handleInit(state)

                Message.NeedClearCache -> handleNeedClearCache(state)

                Message.RegisterComplete -> handleRegisterComplete(
                    state
                )

                Message.RegisterError -> handleRegisterError(state)

                is Message.UpdateRequested -> handleGetUpdateInfoRequest(state)
                is Message.UpdateResponse -> handleGetUpdateInfoResponse(
                    state, message.updateInfo
                )

                is Message.AppUpdateStatusChanged -> handleDownloadFileComplete(
                    state, message.updateStatus
                )

                is Message.DomainNotReachable -> handleDomainNotReachable(
                    state
                )

                is Message.ShowMessage -> handleShowMessage(state, message.message)

                is Message.SyncServers -> handleSyncServers(state)
            }

        private fun handleSyncServers(state: State): Update<State, Message, Dependencies> {
            return state with setOf(Effects.SyncServers())
        }

        private fun handleShowMessage(
            state: State,
            message: String
        ): Update<State, Message, Dependencies> {
            return state with setOf(Effects.ShowMessage(message))
        }

        private fun handleRegisterComplete(state: State): Update<State, Message, Dependencies> {
            return state with setOf(
                Effects.SyncServers()
            )
        }

        private fun handleRegisterError(state: State): Update<State, Message, Dependencies> {
            return state with noEffects()
        }


        private fun handleDomainNotReachable(state: State): Update<State, Message, Dependencies> {
            return state with noEffects()
        }

        private fun handleInit(
            state: State
        ): Update<State, Message, Dependencies> {
            return state with setOf(
                Effects.InitApplicationData(),
//                Effects.RegisterDevice()
            )
        }

        private fun handleGetUpdateInfoRequest(
            state: State
        ): Update<State, Message, Dependencies> {
            return state with setOf()//setOf(Effects.CheckAppUpdateAvailable())
        }

        private fun handleGetUpdateInfoResponse(
            state: State, updateInfo: UpdateInfo
        ): Update<State, Message, Dependencies> {
            return if (!updateInfo.file?.link.isNullOrEmpty()) {
                state.copy(hasUpdate = true) with setOf(
                    Effects.DownloadApplicationUpdate(updateInfo)
                )
            } else {
                state with noEffects()
            }
        }

        private fun handleDownloadFileComplete(
            state: State, updateStatus: UpdateStatus?
        ): Update<State, Message, Dependencies> {
            return state.copy(updateStatus = updateStatus) with noEffects()
        }

        private fun handleNeedClearCache(state: State): Update<State, Message, Dependencies> {
            return state with setOf(Effects.ClearUpdateCache())
        }

    }

    object Effects {

        private val logger = Logger.withTag("RootFeatureEffects")

        class InitApplicationData : Effect<Dependencies, Message> by Effect.idle({ deps ->
            deps.initApplicationDataUseCase()
        })


        class RegisterDevice : Effect<Dependencies, Message> by Effect.onDefault.single({ deps ->
            val isRegistered = deps.checkDeviceRegisterUseCase()
            if (isRegistered) return@single Message.RegisterComplete
            Logger.d("DRE: device not registered")

            val result = deps.registerDeviceUseCase(deps.deviceInfo)
            if (result.isSuccess) {
                return@single Message.RegisterComplete
            } else {
                return@single Message.RegisterError
            }
        })

        class SyncServers : Effect<Dependencies, Message> by Effect.idle({ deps ->
            deps.serversRepository.syncServers()
        })

        class CheckAppUpdateAvailable : Effect<Dependencies, Message> by Effect.single({ deps ->
            val result = deps.checkAppUpdateAvailableUseCase()
            return@single if (result.first) {
                Message.UpdateResponse(result.second)
            } else Message.NeedClearCache
        })

        class ClearUpdateCache : Effect<Dependencies, Message> by Effect.idle({ deps ->
            deps.updateRepository.clearCache()
        })

        @OptIn(ExperimentalCoroutinesApi::class)
        class DownloadApplicationUpdate(updateInfo: UpdateInfo) :
            Effect<Dependencies, Message> by Effect.flow({ deps ->

                deps.updateRepository.downloadUpdate(updateInfo)

                return@flow deps.updateRepository.status.flatMapLatest {
                    flowOf(Message.AppUpdateStatusChanged(it))
                }
            })

        class ShowMessage(message: String) : Effect<Dependencies, Message> by Effect.idle({ deps ->
            deps.messageService.showMessage(AppMessage.Common(message = message))
        })

    }
}
