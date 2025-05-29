package com.runvpn.app.data.device.data

import co.touchlab.kermit.Logger
import com.runvpn.app.data.device.data.api.UpdateApi
import com.runvpn.app.data.device.data.models.update.UpdateInfo
import com.runvpn.app.data.device.domain.UpdateRepository
import com.runvpn.app.data.device.domain.models.update.UpdateStatus
import com.runvpn.app.data.fileSystem
import com.runvpn.app.data.settings.domain.repositories.AppSettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer

class DefaultUpdateRepository(
    private val fileDir: String,
    private val updateApi: UpdateApi,
    private val httpClient: HttpClient,
    private val appSettingsRepository: AppSettingsRepository
) : UpdateRepository {

    private val _status: MutableStateFlow<UpdateStatus?> = MutableStateFlow(null)
    override val status: StateFlow<UpdateStatus?> = _status

    private val _progress: MutableStateFlow<Long> = MutableStateFlow(0)
    override val progress: Flow<Long> = _progress


    private val Path.isDirectory get() = fileSystem.metadataOrNull(this)?.isDirectory == true
    private val Path.isRegularFile get() = fileSystem.metadataOrNull(this)?.isRegularFile == true

    private var isDownloadingInProgress = false


    override suspend fun getUpdateInfo(packageName: String, source: String) =
        updateApi.getUpdateInfo(packageName, source)

    override suspend fun downloadUpdate(updateInfo: UpdateInfo) {
        val link = updateInfo.file ?: return

        if (isDownloadingInProgress) {
            Logger.d("Already Downloading")
            return
        }
        isDownloadingInProgress = true

        val filename = link.link.split("/").last()

        val updateDirPath = "$fileDir/update/".toPath()

        /** Need check file size */
//        val updateFilePath = "$fileDir/update/$filename".toPath()
//        if (updateFilePath.isRegularFile) {
//            _status.emit(UpdateStatus.Success(filePath = filename, updateInfo = updateInfo))
//            return
//        }

        fileSystem.createDirectory(updateDirPath)

        try {
            httpClient.prepareGet(link.link).execute { httpResponse ->

                val total = httpResponse.contentLength() ?: 100

                val sinkBuffer =
                    fileSystem.sink(("$fileDir/update/$filename").toPath(true), false).buffer()

                val cnl: ByteReadChannel = httpResponse.body()

                _status.emit(UpdateStatus.Downloading)

                while (!cnl.isClosedForRead) {
                    val packet = cnl.readRemaining(1024 * 1024)
                    while (!packet.isEmpty) {
                        val bytes = packet.readBytes()
                        sinkBuffer.write(bytes)
                    }
                    _progress.emit(cnl.totalBytesRead * 100 / total)
                }

                sinkBuffer.close()

                if (cnl.totalBytesRead == total) {
                    _status.emit(
                        UpdateStatus.Success(
                            filePath = filename,
                            updateInfo = updateInfo
                        )
                    )

                } else {
                    _status.emit(UpdateStatus.Error("File broken, retry later", updateInfo))
                    clearCache()
                }
            }
        } catch (cause: Throwable) {
            Logger.e("Update Error", cause)
            _status.emit(UpdateStatus.Error("Error, ${cause.message}", updateInfo))
            clearCache()
        } finally {
            isDownloadingInProgress = false
        }

    }

    override fun clearCache() {
        if (fileSystem.exists("$fileDir/update".toPath())) {
            val filesInCache = fileSystem.list("$fileDir/update".toPath())
            Logger.i("Clear cache: $filesInCache")
            filesInCache.forEach {
                fileSystem.delete(it)
            }
        }
    }
}
