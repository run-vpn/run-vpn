package com.runvpn.app.data.device.data.api

import com.runvpn.app.data.device.data.models.update.UpdateInfo
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface UpdateApi {

    @GET("api/applications/{code}/{source}")
    suspend fun getUpdateInfo(
        @Path("code") packageName: String,
        @Path("source") source: String
    ): UpdateInfo

}
