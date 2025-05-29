package com.runvpn.app.feature.settings.update

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.runvpn.app.core.common.UriManager
import com.runvpn.app.data.device.data.models.device.register.DeviceInfo
import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.UpdateRepository
import com.runvpn.app.feature.settings.Constants.SELF_UPDATE_ALLOWED_SOURCE
import com.runvpn.app.feature.settings.Constants.SOURCE_HUAWEI
import com.runvpn.app.tea.decompose.createCoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DefaultUpdateComponent(
    componentContext: ComponentContext,
    private val updateRepository: UpdateRepository,
    private val uriManager: UriManager,
    deviceInfo: DeviceInfo,
    message: String,
    private val onDismiss: () -> Unit
) : UpdateComponent, ComponentContext by componentContext {

    private val coroutineScope = createCoroutineScope()

    private val _state =
        MutableValue(
            UpdateComponent.State(
                updateMessage = message,
                updateStatus = null,
                updateProgress = null,
                selfUpdateAllowed = deviceInfo.source == SELF_UPDATE_ALLOWED_SOURCE
            )
        )

    override val state: Value<UpdateComponent.State>
        get() = _state

    init {
        coroutineScope.launch {
            updateRepository.status.collectLatest { updateStatus ->
                _state.value = state.value.copy(updateStatus = updateStatus)
            }
        }

        coroutineScope.launch {
            updateRepository.progress.collectLatest {
                _state.value = state.value.copy(updateProgress = it)
            }
        }
    }

    override fun retryClick(updateInfo: UpdateInfo) {
        coroutineScope.launch {
            updateRepository.downloadUpdate(updateInfo)
        }
    }

    override fun onOpenLinkClick(link: String) {
        uriManager.openUri(link)
    }

    override fun onUpdateClick() {
        uriManager.openUri(SOURCE_HUAWEI)
    }

    override fun onDismissClicked() {
        onDismiss()
    }


}
