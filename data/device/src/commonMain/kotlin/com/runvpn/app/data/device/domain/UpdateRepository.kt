package com.runvpn.app.data.device.domain

import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.models.update.UpdateStatus
import kotlinx.coroutines.flow.Flow


interface UpdateRepository {

    val status: Flow<UpdateStatus?>
    val progress: Flow<Long>

    suspend fun getUpdateInfo(packageName: String, source: String): UpdateInfo
    suspend fun downloadUpdate(updateInfo: UpdateInfo)

    fun clearCache()

}
