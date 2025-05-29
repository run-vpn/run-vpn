package com.runvpn.app.data.device.domain.models.update

import com.runvpn.app.data.device.data.models.update.UpdateInfo
import kotlinx.serialization.Serializable


sealed interface UpdateStatus {

    @Serializable
    data class Success(val filePath: String, val updateInfo: UpdateInfo) : UpdateStatus

    @Serializable
    data object Downloading : UpdateStatus

    @Serializable
    data class Error(val message: String, val updateInfo: UpdateInfo) : UpdateStatus

}
