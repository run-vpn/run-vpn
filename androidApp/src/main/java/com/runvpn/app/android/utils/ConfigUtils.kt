package com.runvpn.app.android.utils

import org.cryptacular.util.CertUtil
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

object ConfigUtils {


    /**
     * Parse string to X509Certificate
     * @param certificate X509Certificate from .pem file
     * @return CN of certificate if parse successful
     * */
    fun getCertificateName(certificate: String?): String? {
        try {
            certificate?.byteInputStream().use { inStream ->
                val certificateFactory = CertificateFactory.getInstance("X.509")
                val x509Certificate =
                    certificateFactory.generateCertificate(inStream) as X509Certificate
                return CertUtil.subjectCN(x509Certificate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
