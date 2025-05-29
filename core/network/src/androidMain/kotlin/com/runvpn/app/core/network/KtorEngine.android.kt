package com.runvpn.app.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import java.security.SecureRandom
import javax.net.ssl.SSLContext

actual val ktorEngine: HttpClientEngine = OkHttp.create() {
    config {
        val trustAllCert = AllCertsTrustManager()
        val sslContext = SSLContext.getInstance("SSL")
        @Suppress("unused")
        hostnameVerifier { hostname, session -> true }
        sslContext.init(null, arrayOf(trustAllCert), SecureRandom())
        sslSocketFactory(sslContext.socketFactory, trustAllCert)
    }
}
