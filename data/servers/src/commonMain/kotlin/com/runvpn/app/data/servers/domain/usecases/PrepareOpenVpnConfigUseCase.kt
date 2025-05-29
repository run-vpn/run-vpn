package com.runvpn.app.data.servers.domain.usecases

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.Url

class PrepareOpenVpnConfigUseCase(private val httpClient: HttpClient) {

    private var cachedLastResult: Pair<Url?, String?> = Pair(null, null)

    suspend operator fun invoke(url: Url): String {
        if (cachedLastResult.first == url && !cachedLastResult.second.isNullOrBlank()) {
            return cachedLastResult.second!!
        }
        return try {
            val response = httpClient.get(url)
            val config = response.body<String>()
            cachedLastResult = Pair(url, config)

            config
        } catch (e: Exception) {
            ""
        }
    }

    operator fun invoke(config: String): String {
        return config
    }

}
