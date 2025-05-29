package com.runvpn.app.core.network

import com.runvpn.app.core.exceptions.NeedUpdateException
import com.runvpn.app.core.exceptions.setupErrorConverter
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


/**
 * Creates implementations of Ktorfit APIs.
 */
class NetworkApiFactory(
    private val json: Json,
    private val baseUrl: String,
    private val appVersionCode: String,
    private val appVersionName: String,
    private val platform: String,
    private val onConnectionStatusChanged: (NetworkStatus) -> Unit
) {

    companion object {
        private const val CONNECT_TIMEOUT_MILLISECONDS = 60000L
        private const val READ_WRITE_TIMEOUT_MILLISECONDS = 60000L
        const val RECONNECT_PERIOD_MILLIS = 6000L
        const val TOTAL_RECONNECT_REPEAT_ATTEMPTS = 10
        const val CONNECTION_STATUS_CHANGE_REPEAT_ATTEMPTS = 3

        private val ktorLogger = co.touchlab.kermit.Logger.withTag("KtorLogger")
    }

    private val authorizedHttpClient = createHttpClient(authorized = true)
    private val unauthorizedHttpClient = createHttpClient(authorized = false)

    /**
     * Ktorfit for creating an authorized API's
     */
    val authorizedKtorfit = createKtorfit(authorizedHttpClient)

    fun createAuthorizedKtorfit(
        getTokenDelegate: () -> String
    ): Ktorfit = createKtorfit(
        createHttpClient(authorized = true, getTokenDelegate)
    )

    /**
     * Ktorfit for creating that doesn't require authorization
     */
    val unauthorizedKtorfit = createKtorfit(unauthorizedHttpClient)

    private fun createHttpClient(
        authorized: Boolean,
        getTokenDelegate: (() -> String)? = null,
    ): HttpClient {
        return HttpClient(ktorEngine) {

            defaultRequest {
                url(baseUrl)

                header("Content-Type", "application/json")
                header("Accept", "application/json")
                header("User-Agent", "$platform $appVersionName $appVersionCode")
            }

            install(HttpTimeout) {
                connectTimeoutMillis = CONNECT_TIMEOUT_MILLISECONDS
                requestTimeoutMillis = READ_WRITE_TIMEOUT_MILLISECONDS
            }

            install(ContentNegotiation) {
                json(json)
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = TOTAL_RECONNECT_REPEAT_ATTEMPTS)
                constantDelay(millis = RECONNECT_PERIOD_MILLIS)
                modifyRequest {
                    if (retryCount == CONNECTION_STATUS_CHANGE_REPEAT_ATTEMPTS) {
                        onConnectionStatusChanged(NetworkStatus.RECONNECTING)
                    }
                }
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        ktorLogger.i(message)
                    }
                }
                level = LogLevel.ALL
            }

            HttpResponseValidator {
                validateResponse { response ->
                    if (response.status.value == 426) {
                        co.touchlab.kermit.Logger.i("Need Update Bruh.. ${response}")
                        throw NeedUpdateException("Need Update Bruh..")
                    }
                    onConnectionStatusChanged(NetworkStatus.AVAILABLE)
                }
            }

            if (authorized && getTokenDelegate != null) {
                install(AuthHeader) {
                    this.getTokenDelegate = getTokenDelegate
                }
            }

            setupErrorConverter()
        }
    }

    private fun createKtorfit(httpClient: HttpClient): Ktorfit {
        return Ktorfit.Builder()
            .baseUrl(baseUrl)
            .httpClient(httpClient)
            .build()
    }
}
