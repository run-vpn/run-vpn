package com.runvpn.app.data.device.data.models.update

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This entity must not be changed on backend and on frontend.
 * Changed can be applied only if both sides accept it after discussion.
 */
@Serializable
data class UpdateInfo(
    val source: String,
    val code: String,
    @SerialName("auto_update")
    val autoUpdate: Boolean = false,
    @SerialName("platformName")
    val platformName: String,
    @SerialName("versionName")
    val versionName: String,
    @SerialName("file")
    val file: FileInfo? = null
)

@Serializable
data class FileInfo(
    val name: String,
    val link: String,
    val mimeType: String
)
