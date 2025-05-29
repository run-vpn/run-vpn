package com.runvpn.app.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("success")
    val isSuccess: Boolean = true,
    val message: String? = null,
    val data: T?
) {
    fun getOrThrow() = checkNotNull(data)

    fun <R> map(transform: (T) -> R): ApiResponse<R> {
        checkNotNull(this.data)

        return ApiResponse(
            isSuccess = this.isSuccess,
            message = this.message,
            data = transform(this.data)
        )
    }
}
